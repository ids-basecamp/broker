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

package de.truzzt.edc.extension.broker.api.handler;

import de.fraunhofer.iais.eis.ConnectorUpdateMessage;
import org.eclipse.edc.catalog.spi.FederatedCacheNode;
import org.eclipse.edc.catalog.spi.directory.FederatedCacheNodeDirectory;
import org.eclipse.edc.jwt.spi.JwtRegisteredClaimNames;
import org.eclipse.edc.protocol.ids.api.multipart.handler.Handler;
import org.eclipse.edc.protocol.ids.api.multipart.message.MultipartRequest;
import org.eclipse.edc.protocol.ids.api.multipart.message.MultipartResponse;
import org.eclipse.edc.protocol.ids.spi.types.IdsId;
import org.eclipse.edc.protocol.ids.spi.types.MessageProtocol;
import org.eclipse.edc.spi.monitor.Monitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.eclipse.edc.protocol.ids.api.multipart.util.ResponseUtil.*;

public class ConnectorUpdateHandler implements Handler {
    private final Monitor monitor;
    private final IdsId connectorId;
    private final FederatedCacheNodeDirectory nodeDirectory;

    public ConnectorUpdateHandler(
            @NotNull Monitor monitor,
            @NotNull IdsId connectorId,
            @NotNull FederatedCacheNodeDirectory nodeDirectory) {
        this.monitor = monitor;
        this.connectorId = connectorId;
        this.nodeDirectory = nodeDirectory;
    }

    @Override
    public boolean canHandle(@NotNull MultipartRequest multipartRequest) {
        return multipartRequest.getHeader() instanceof ConnectorUpdateMessage;
    }

    @Override
    public @NotNull MultipartResponse handleRequest(@NotNull MultipartRequest multipartRequest) {

        var header = (ConnectorUpdateMessage) multipartRequest.getHeader();
        var sub = multipartRequest.getClaimToken().getStringClaim(JwtRegisteredClaimNames.SUBJECT);

        try {
            var cacheNode = new FederatedCacheNode(sub,
                    header.getIssuerConnector().toString(),
                    List.of(MessageProtocol.IDS_MULTIPART)
            );
            nodeDirectory.insert(cacheNode);
        } catch (Exception e) {
            monitor.severe("ConnectorUpdateHandler: Error inserting new Federated Cache Node", e);
            return createMultipartResponse(internalRecipientError(header, connectorId));
        }

        return createMultipartResponse(messageProcessedNotification(header, connectorId));
    }
}
