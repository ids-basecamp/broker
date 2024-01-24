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

package de.truzzt.edc.extension.broker.api;

import de.truzzt.edc.extension.broker.api.controller.InfrastructureController;
import de.truzzt.edc.extension.broker.api.handler.ConnectorUnavailableHandler;
import de.truzzt.edc.extension.broker.api.handler.ConnectorUpdateHandler;
import org.eclipse.edc.catalog.spi.directory.FederatedCacheNodeDirectory;
import org.eclipse.edc.connector.api.management.configuration.ManagementApiConfiguration;
import org.eclipse.edc.protocol.ids.api.configuration.IdsApiConfiguration;
import org.eclipse.edc.protocol.ids.api.multipart.handler.Handler;
import org.eclipse.edc.protocol.ids.serialization.IdsTypeManagerUtil;
import org.eclipse.edc.protocol.ids.spi.service.DynamicAttributeTokenService;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Requires;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.web.spi.WebService;

import java.util.LinkedList;

import static org.eclipse.edc.protocol.ids.util.ConnectorIdUtil.resolveConnectorId;

@Extension(value = BrokerApiExtension.NAME)
@Requires(value = {
        WebService.class,
        ManagementApiConfiguration.class,
        FederatedCacheNodeDirectory.class,
        DynamicAttributeTokenService.class,
        IdsApiConfiguration.class
})
public class BrokerApiExtension implements ServiceExtension {

    public static final String NAME = "Broker API Extension";

    @Inject
    private WebService webService;

    @Inject
    private ManagementApiConfiguration managementApiConfig;

    @Inject
    private FederatedCacheNodeDirectory nodeDirectory;

    @Inject
    private DynamicAttributeTokenService tokenService;

    @Inject
    private IdsApiConfiguration idsApiConfiguration;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var connectorId = resolveConnectorId(context);
        var monitor = context.getMonitor();

        IdsTypeManagerUtil.customizeTypeManager(context.getTypeManager());
        var objectMapper = IdsTypeManagerUtil.getIdsObjectMapper(context.getTypeManager());

        var handlers = new LinkedList<Handler>();
        handlers.add(new ConnectorUpdateHandler(monitor, connectorId, nodeDirectory));
        handlers.add(new ConnectorUnavailableHandler(monitor, connectorId, nodeDirectory));

        var infrastructureController = new InfrastructureController(monitor,
                connectorId,
                objectMapper,
                tokenService,
                idsApiConfiguration.getIdsWebhookAddress(),
                handlers);
        webService.registerResource(managementApiConfig.getContextAlias(), infrastructureController);
    }
}
