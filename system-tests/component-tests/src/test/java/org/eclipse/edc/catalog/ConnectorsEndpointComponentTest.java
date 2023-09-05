package org.eclipse.edc.catalog;

import org.eclipse.edc.catalog.spi.CachedAsset;
import org.eclipse.edc.catalog.spi.Catalog;
import org.eclipse.edc.catalog.spi.CatalogRequest;
import org.eclipse.edc.catalog.spi.FederatedCacheNode;
import org.eclipse.edc.catalog.spi.directory.FederatedCacheNodeDirectory;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractOffer;
import org.eclipse.edc.junit.annotations.ComponentTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.message.MessageContext;
import org.eclipse.edc.spi.message.RemoteMessageDispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static java.lang.String.valueOf;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.eclipse.edc.catalog.TestFunctions.emptyCatalog;
import static org.eclipse.edc.catalog.TestFunctions.insertMultiple;
import static org.eclipse.edc.catalog.TestFunctions.insertSingle;
import static org.eclipse.edc.catalog.TestFunctions.queryCatalogApi;
import static org.eclipse.edc.catalog.TestFunctions.queryConnectorsApi;
import static org.eclipse.edc.catalog.TestFunctions.randomCatalog;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ComponentTest
@ExtendWith(EdcExtension.class)
public class ConnectorsEndpointComponentTest {

    private static final Duration TEST_TIMEOUT = ofSeconds(10);

    @BeforeEach
    void setup(EdcExtension extension) {
        extension.setConfiguration(Map.of(
                // make sure only one crawl-run is performed
                "edc.catalog.cache.execution.period.seconds", "2",
                // number of crawlers will be limited by the number of crawl-targets
                "edc.catalog.cache.partition.num.crawlers", "10",
                // give the runtime time to set up everything
                "edc.catalog.cache.execution.delay.seconds", "1",
                "web.http.port", valueOf(TestFunctions.PORT),
                "web.http.path", TestFunctions.BASE_PATH
        ));
    }

    @Test
    @DisplayName("Get Connectors endpoint with empty result")
    void queryConnector_withEmptyResults(FederatedCacheNodeDirectory directory) {
        assertThat(queryConnectorsApi()).hasSize(0);
    }

    @Test
    @DisplayName("Get Connectors endpoint with one result")
    void queryConnector_withOneResults(FederatedCacheNodeDirectory directory) {
        // prepare node directory
        insertSingle(directory);

        assertThat(queryConnectorsApi()).hasSize(1);
    }

    @Test
    @DisplayName("Get Connectors endpoint with many results")
    void queryConnector_withManyResults(FederatedCacheNodeDirectory directory) {
        // prepare node directory
        insertMultiple(directory);

        List<FederatedCacheNode> nodes = queryConnectorsApi();
        assertThat(nodes).hasSize(3);
    }

    @Test
    @DisplayName("Get Connectors endpoint with one result and crawler execution data not null")
    void queryConnector_withManyResultsCrawlerDataNotNull(RemoteMessageDispatcher dispatcher, FederatedCacheNodeDirectory directory) {
        Mockito.reset(dispatcher);
        // prepare node directory
        insertSingle(directory);

        when(dispatcher.send(eq(Catalog.class), isA(CatalogRequest.class), any(MessageContext.class)))
                .thenReturn(randomCatalog(5))
                .thenReturn(emptyCatalog());

        await().pollDelay(ofSeconds(1))
                .atMost(TEST_TIMEOUT)
                .untilAsserted(() -> {
                    List<FederatedCacheNode> nodes = queryConnectorsApi();
                    assertThat(nodes).hasSize(1);
                    assertThat(nodes.get(0).getOnlineStatus()).isTrue();
                    assertThat(nodes.get(0).getContractOffersCount()).isEqualTo(5);
                });

    }

    @Test
    @DisplayName("Get Connectors endpoint with one result and crawler execution data not null")
    void queryConnector_withManyResultsOfflineConnector(RemoteMessageDispatcher dispatcher, FederatedCacheNodeDirectory directory) {
        // prepare node directory
        insertSingle(directory);

        await().pollDelay(ofSeconds(1))
                .atMost(TEST_TIMEOUT)
                .untilAsserted(() -> {
                    List<FederatedCacheNode> nodes = queryConnectorsApi();
                    assertThat(nodes).hasSize(1);
                    assertThat(nodes.get(0).getOnlineStatus()).isFalse();
                });

    }
}
