package de.truzzt.edc.extension.broker.api.handler;

import de.truzzt.edc.extension.broker.api.message.MultipartRequest;
import de.truzzt.edc.extension.broker.api.message.MultipartResponse;
import de.truzzt.edc.extension.broker.api.types.TypeManagerUtil;
import de.truzzt.edc.extension.broker.api.types.jwt.JWTPayload;
import org.eclipse.edc.catalog.spi.FederatedCacheNode;
import org.eclipse.edc.catalog.spi.directory.FederatedCacheNodeDirectory;
import org.eclipse.edc.protocol.ids.spi.types.IdsId;
import org.eclipse.edc.protocol.ids.spi.types.MessageProtocol;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.monitor.Monitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static de.truzzt.edc.extension.broker.api.util.ResponseUtil.badParameters;
import static de.truzzt.edc.extension.broker.api.util.ResponseUtil.createMultipartResponse;
import static de.truzzt.edc.extension.broker.api.util.ResponseUtil.internalRecipientError;
import static de.truzzt.edc.extension.broker.api.util.ResponseUtil.messageProcessedNotification;

public class ConnectorUpdateHandler implements Handler {
    private final Monitor monitor;
    private final IdsId connectorId;
    private final TypeManagerUtil typeManagerUtil;

    private final FederatedCacheNodeDirectory cacheNodeDirectory;

    public ConnectorUpdateHandler(
            @NotNull Monitor monitor,
            @NotNull IdsId connectorId,
            @NotNull TypeManagerUtil typeManagerUtil,
            @NotNull FederatedCacheNodeDirectory cacheNodeDirectory) {
        this.monitor = monitor;
        this.connectorId = connectorId;
        this.typeManagerUtil = typeManagerUtil;
        this.cacheNodeDirectory = cacheNodeDirectory;
    }

    @Override
    public boolean canHandle(@NotNull MultipartRequest multipartRequest) {
        return multipartRequest.getHeader().getType().equals("ids:ConnectorUpdateMessage");
    }

    @Override
    public @NotNull MultipartResponse handleRequest(@NotNull MultipartRequest multipartRequest) {

        var header = multipartRequest.getHeader();

        JWTPayload jwt;
        try {
            jwt = typeManagerUtil.parseToken(header.getSecurityToken());
        } catch (EdcException e) {
            monitor.severe("ConnectorUpdateHandler: Security Token is invalid", e);
            return createMultipartResponse(badParameters(header, connectorId));
        }

        try {
            var cacheNode = new FederatedCacheNode(
                    jwt.getSub(),
                    header.getIssuerConnector().toString(),
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
