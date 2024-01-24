/*
 *  Copyright (c) 2021 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - Initial implementation
 *       truzzt GmbH - PostgreSQL implementation
 *
 */

package de.truzzt.edc.extension.broker.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.Message;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.edc.protocol.ids.api.multipart.controller.AbstractMultipartController;
import org.eclipse.edc.protocol.ids.api.multipart.handler.Handler;
import org.eclipse.edc.protocol.ids.api.multipart.message.MultipartRequest;
import org.eclipse.edc.protocol.ids.api.multipart.message.MultipartResponse;
import org.eclipse.edc.protocol.ids.spi.service.DynamicAttributeTokenService;
import org.eclipse.edc.protocol.ids.spi.types.IdsId;
import org.eclipse.edc.spi.monitor.Monitor;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.List;

import static java.lang.String.format;
import static org.eclipse.edc.protocol.ids.api.multipart.util.ResponseUtil.malformedMessage;
import static org.eclipse.edc.protocol.ids.api.multipart.util.ResponseUtil.messageTypeNotSupported;
import static org.eclipse.edc.protocol.ids.api.multipart.util.ResponseUtil.notAuthenticated;

@Path(InfrastructureController.PATH)
public class InfrastructureController extends AbstractMultipartController {

    public static final String PATH = "/infrastructure";

    public InfrastructureController(@NotNull Monitor monitor,
                                    @NotNull IdsId connectorId,
                                    @NotNull ObjectMapper objectMapper,
                                    @NotNull DynamicAttributeTokenService tokenService,
                                    @NotNull String idsWebhookAddress,
                                    @NotNull List<Handler> multipartHandlers) {
        super(monitor, connectorId, objectMapper, tokenService, multipartHandlers, idsWebhookAddress);
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
        } catch (Exception e) {
            monitor.warning(format("InfrastructureController: Header parsing failed: %s", e.getMessage()));
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
}
