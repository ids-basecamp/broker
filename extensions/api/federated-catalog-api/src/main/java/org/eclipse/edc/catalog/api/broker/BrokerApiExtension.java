package org.eclipse.edc.catalog.api.broker;

import org.eclipse.edc.catalog.api.broker.controller.InfrastructureController;
import org.eclipse.edc.catalog.api.broker.handler.ConnectorUpdateHandler;
import org.eclipse.edc.catalog.spi.FederatedCacheNodeDirectory;
import org.eclipse.edc.connector.api.management.configuration.ManagementApiConfiguration;
import org.eclipse.edc.protocol.ids.api.multipart.handler.Handler;
import org.eclipse.edc.protocol.ids.spi.transform.IdsTransformerRegistry;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.web.spi.WebService;

import java.util.LinkedList;

import static org.eclipse.edc.protocol.ids.util.ConnectorIdUtil.resolveConnectorId;

@Extension(value = BrokerApiExtension.NAME)
public class BrokerApiExtension implements ServiceExtension {

    public static final String NAME = "Broker Infrastructure API Extension";

    @Inject
    private Monitor monitor;

    @Inject
    private WebService webService;

    @Inject
    private ManagementApiConfiguration managementApiConfig;

    @Inject
    private IdsTransformerRegistry transformerRegistry;

    @Inject
    private FederatedCacheNodeDirectory cacheNodeDirectory;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var connectorId = resolveConnectorId(context);

        var objectMapper = context.getTypeManager().getMapper("ids");

        var handlers = new LinkedList<Handler>();
        handlers.add(new ConnectorUpdateHandler(monitor, connectorId, objectMapper, transformerRegistry,
                cacheNodeDirectory));

        var infrastructureController = new InfrastructureController(monitor, connectorId, objectMapper, handlers);
        webService.registerResource(managementApiConfig.getContextAlias(), infrastructureController);
    }
}
