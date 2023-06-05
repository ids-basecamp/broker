package de.truzzt.edc.extension.broker.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.Message;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.edc.protocol.ids.api.multipart.handler.Handler;
import org.eclipse.edc.protocol.ids.api.multipart.message.MultipartRequest;
import org.eclipse.edc.protocol.ids.api.multipart.message.MultipartResponse;
import org.eclipse.edc.protocol.ids.spi.service.DynamicAttributeTokenService;
import org.eclipse.edc.protocol.ids.spi.types.IdsId;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.monitor.Monitor;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static java.lang.String.format;
import static org.eclipse.edc.protocol.ids.api.multipart.util.ResponseUtil.*;

@Consumes({MediaType.MULTIPART_FORM_DATA})
@Produces({MediaType.MULTIPART_FORM_DATA})
@Path(InfrastructureController.PATH)
public class InfrastructureController {

    public static final String PATH = "/infrastructure";
    private static final String HEADER = "header";
    private static final String PAYLOAD = "payload";

    private final Monitor monitor;
    private final IdsId connectorId;
    private final List<Handler> multipartHandlers;
    private final ObjectMapper objectMapper;
    private final DynamicAttributeTokenService tokenService;
    private final String idsWebhookAddress;

    public InfrastructureController(@NotNull Monitor monitor,
                                   @NotNull IdsId connectorId,
                                   @NotNull ObjectMapper objectMapper,
                                    @NotNull DynamicAttributeTokenService tokenService,
                                    @NotNull List<Handler> multipartHandlers,
                                    @NotNull String idsWebhookAddress) {
        this.monitor = monitor;
        this.connectorId = connectorId;
        this.objectMapper = objectMapper;
        this.tokenService = tokenService;
        this.multipartHandlers = multipartHandlers;
        this.idsWebhookAddress = idsWebhookAddress;
    }

    @POST
    public FormDataMultiPart request(@FormDataParam(HEADER) InputStream headerInputStream,
                                     @FormDataParam(PAYLOAD) String payload) {
        if (headerInputStream == null) {
            return createFormDataMultiPart(malformedMessage(null, connectorId));
        }

        Message header;
        try {
            header = objectMapper.readValue(headerInputStream, Message.class);
        } catch (IOException e) {
            return createFormDataMultiPart(malformedMessage(null, connectorId));
        }

        if (header == null) {
            return createFormDataMultiPart(malformedMessage(null, connectorId));
        }

        // Check if any required header field missing
        if (header.getId() == null || header.getIssuerConnector() == null || header.getSenderAgent() == null) {
            return createFormDataMultiPart(malformedMessage(header, connectorId));
        }

        // Check if DAT present
        var dynamicAttributeToken = header.getSecurityToken();
        if (dynamicAttributeToken == null || dynamicAttributeToken.getTokenValue() == null) {
            monitor.warning("InfrastructureController: Token is missing in header");
            return createFormDataMultiPart(notAuthenticated(header, connectorId));
        }

        // Validate DAT
        var verificationResult = tokenService
                .verifyDynamicAttributeToken(dynamicAttributeToken, header.getIssuerConnector(), idsWebhookAddress);
        if (verificationResult.failed()) {
            monitor.warning(format("InfrastructureController: Token validation failed %s", verificationResult.getFailure().getMessages()));
            return createFormDataMultiPart(notAuthenticated(header, connectorId));
        }

        // Build the multipart request
        var claimToken = verificationResult.getContent();
        var multipartRequest = MultipartRequest.Builder.newInstance()
                .header(header)
                .payload(payload)
                .claimToken(claimToken)
                .build();

        var multipartResponse = multipartHandlers.stream()
                .filter(h -> h.canHandle(multipartRequest))
                .findFirst()
                .map(it -> it.handleRequest(multipartRequest))
                .orElseGet(() -> MultipartResponse.Builder.newInstance()
                        .header(messageTypeNotSupported(header, connectorId))
                        .build());

        return createFormDataMultiPart(multipartResponse.getHeader(), multipartResponse.getPayload());
    }

    private FormDataMultiPart createFormDataMultiPart(Message header, Object payload) {
        var multiPart = createFormDataMultiPart(header);

        if (payload != null) {
            multiPart.bodyPart(new FormDataBodyPart(PAYLOAD, toJson(payload), MediaType.APPLICATION_JSON_TYPE));
        }

        return multiPart;
    }

    private FormDataMultiPart createFormDataMultiPart(Message header) {
        var multiPart = new FormDataMultiPart();
        if (header != null) {
            multiPart.bodyPart(new FormDataBodyPart(HEADER, toJson(header), MediaType.APPLICATION_JSON_TYPE));
        }
        return multiPart;
    }

    private byte[] toJson(Object object) {
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new EdcException(e);
        }
    }
}
