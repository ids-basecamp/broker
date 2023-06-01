package de.truzzt.edc.extension.broker.api.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.ConnectorUpdateMessage;
import org.eclipse.edc.catalog.spi.FederatedCacheNode;
import org.eclipse.edc.catalog.spi.FederatedCacheNodeDirectory;
import org.eclipse.edc.protocol.ids.api.multipart.handler.Handler;
import org.eclipse.edc.protocol.ids.api.multipart.message.MultipartRequest;
import org.eclipse.edc.protocol.ids.api.multipart.message.MultipartResponse;
import org.eclipse.edc.protocol.ids.spi.transform.IdsTransformerRegistry;
import org.eclipse.edc.protocol.ids.spi.types.IdsId;
import org.eclipse.edc.protocol.ids.spi.types.MessageProtocol;
import org.eclipse.edc.spi.monitor.Monitor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import static org.eclipse.edc.protocol.ids.api.multipart.util.ResponseUtil.badParameters;
import static org.eclipse.edc.protocol.ids.api.multipart.util.ResponseUtil.createMultipartResponse;
import static org.eclipse.edc.protocol.ids.api.multipart.util.ResponseUtil.internalRecipientError;
import static org.eclipse.edc.protocol.ids.api.multipart.util.ResponseUtil.messageProcessedNotification;

public class ConnectorUpdateHandler implements Handler {
    private final Monitor monitor;
    private final ObjectMapper objectMapper;
    private final IdsId connectorId;
    private final IdsTransformerRegistry transformerRegistry;

    private FederatedCacheNodeDirectory cacheNodeDirectory;

    public ConnectorUpdateHandler(
            @NotNull Monitor monitor,
            @NotNull IdsId connectorId,
            @NotNull ObjectMapper objectMapper,
            @NotNull IdsTransformerRegistry transformerRegistry,
            @NotNull FederatedCacheNodeDirectory cacheNodeDirectory) {
        this.monitor = monitor;
        this.connectorId = connectorId;
        this.objectMapper = objectMapper;
        this.transformerRegistry = transformerRegistry;
        this.cacheNodeDirectory = cacheNodeDirectory;
    }

    @Override
    public boolean canHandle(@NotNull MultipartRequest multipartRequest) {
        return multipartRequest.getHeader() instanceof ConnectorUpdateMessage;
    }

    @Override
    public @NotNull MultipartResponse handleRequest(@NotNull MultipartRequest multipartRequest) {

        var header = (ConnectorUpdateMessage) multipartRequest.getHeader();

        Connector connector;
        try {
            connector = objectMapper.readValue(multipartRequest.getPayload(), Connector.class);
        } catch (IOException e) {
            monitor.severe("ConnectorUpdateHandler: Connector Request is invalid", e);
            return createMultipartResponse(badParameters(header, connectorId));
        }

        try {
            var cacheNode = new FederatedCacheNode(
                    connector.getId().toString(),
                    connector.getHasDefaultEndpoint().getAccessURL().toString(),
                    List.of(MessageProtocol.IDS_MULTIPART)
            );
            cacheNodeDirectory.insert(cacheNode);
        } catch (Exception e) {
            monitor.severe("ConnectorUpdateHandler: Error inserting new Federated Cache Node", e);
            return createMultipartResponse(internalRecipientError(header, connectorId));
        }

        return createMultipartResponse(messageProcessedNotification(header, connectorId));
    }
}
