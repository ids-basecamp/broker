package de.truzzt.edc.extension.broker.api.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.ConnectorUnavailableMessage;
import de.truzzt.edc.extension.broker.api.message.MultipartRequest;
import de.truzzt.edc.extension.broker.api.message.MultipartResponse;
import org.eclipse.edc.catalog.spi.FederatedCacheNode;
import org.eclipse.edc.catalog.spi.directory.FederatedCacheNodeDirectory;
import org.eclipse.edc.protocol.ids.spi.transform.IdsTransformerRegistry;
import org.eclipse.edc.protocol.ids.spi.types.IdsId;
import org.eclipse.edc.spi.monitor.Monitor;
import org.jetbrains.annotations.NotNull;

import static de.truzzt.edc.extension.broker.api.util.ResponseUtil.createMultipartResponse;
import static de.truzzt.edc.extension.broker.api.util.ResponseUtil.internalRecipientError;
import static de.truzzt.edc.extension.broker.api.util.ResponseUtil.messageProcessedNotification;

public class ConnectorUnavailableHandler implements Handler {
    private final Monitor monitor;
    private final ObjectMapper objectMapper;
    private final IdsId connectorId;
    private final IdsTransformerRegistry transformerRegistry;

    private FederatedCacheNodeDirectory cacheNodeDirectory;

    public ConnectorUnavailableHandler(
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
        return multipartRequest.getHeader() instanceof ConnectorUnavailableMessage;
    }

    @Override
    public @NotNull MultipartResponse handleRequest(@NotNull MultipartRequest multipartRequest) {

        var header = multipartRequest.getHeader();

        boolean deleted;
        try {
            var cacheNode = new FederatedCacheNode(null, header.getIssuerConnector().toString(), null);
            deleted = cacheNodeDirectory.delete(cacheNode);
        } catch (Exception e) {
            monitor.severe("ConnectorUnavailableHandler: Error deleting Federated Cache Node", e);
            return createMultipartResponse(internalRecipientError(header, connectorId));
        }

        if (!deleted) {
            monitor.severe("ConnectorUnavailableHandler: Not Found Federated Cache Node");
            return createMultipartResponse(internalRecipientError(header, connectorId));
        }

        return createMultipartResponse(messageProcessedNotification(header, connectorId));
    }
}
