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
import de.truzzt.edc.extension.broker.api.handler.Handler;
import de.truzzt.edc.extension.broker.api.types.TypeManagerUtil;
import org.eclipse.edc.catalog.spi.directory.FederatedCacheNodeDirectory;
import org.eclipse.edc.connector.api.management.configuration.ManagementApiConfiguration;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.web.spi.WebService;

import java.util.LinkedList;

import static org.eclipse.edc.protocol.ids.util.ConnectorIdUtil.resolveConnectorId;

@Extension(value = BrokerApiExtension.NAME)
public class BrokerApiExtension implements ServiceExtension {

    public static final String NAME = "Broker API Extension";

    @Inject
    private WebService webService;

    @Inject
    private ManagementApiConfiguration managementApiConfig;

    @Inject
    private FederatedCacheNodeDirectory nodeDirectory;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var connectorId = resolveConnectorId(context);

        var typeManagerUtil = new TypeManagerUtil();

        var monitor = context.getMonitor();

        var handlers = new LinkedList<Handler>();
        handlers.add(new ConnectorUpdateHandler(monitor, connectorId, typeManagerUtil, nodeDirectory));
        handlers.add(new ConnectorUnavailableHandler(monitor, connectorId, typeManagerUtil, nodeDirectory));

        var infrastructureController = new InfrastructureController(monitor, connectorId, typeManagerUtil,
                handlers);
        webService.registerResource(managementApiConfig.getContextAlias(), infrastructureController);
    }
}
