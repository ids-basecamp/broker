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
 *       Truzzt GmbH - Initial implementation
 *
 */

package org.eclipse.edc.catalog;

import org.eclipse.edc.catalog.spi.Catalog;
import org.eclipse.edc.catalog.spi.CatalogRequest;
import org.eclipse.edc.catalog.spi.FederatedCacheNode;
import org.eclipse.edc.catalog.spi.directory.FederatedCacheNodeDirectory;
import org.eclipse.edc.junit.annotations.ComponentTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.message.MessageContext;
import org.eclipse.edc.spi.message.RemoteMessageDispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

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
                "edc.catalog.cache.execution.period.seconds", "5",
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
    void queryConnector_withEmptyResults() {
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
    @DisplayName("Get Connectors endpoint with one result with online connector status")
    void queryConnector_withManyResultsOnlineConnector(RemoteMessageDispatcher dispatcher, FederatedCacheNodeDirectory directory) {
        // prepare node directory
        insertSingle(directory);

        when(dispatcher.send(eq(Catalog.class), isA(CatalogRequest.class), any(MessageContext.class)))
                .thenReturn(randomCatalog(10))
                .thenReturn(emptyCatalog());

        await().pollDelay(ofSeconds(1))
                .atMost(TEST_TIMEOUT)
                .untilAsserted(() -> {
                    var list = queryConnectorsApi();
                    assertThat(list).hasSize(1);

                    var connector = list.get(0);
                    assertThat(connector.getOnlineStatus()).isTrue();
                    assertThat(connector.getLastCrawled()).isNotNull();
                    assertThat(connector.getContractOffersCount()).isEqualTo(10);
                });
    }

    @Test
    @DisplayName("Get Connectors endpoint with one result with offline connector status")
    void queryConnector_withManyResultsOfflineConnector(RemoteMessageDispatcher dispatcher, FederatedCacheNodeDirectory directory) {
        // prepare node directory
        insertSingle(directory);

        when(dispatcher.send(eq(Catalog.class), isA(CatalogRequest.class), any(MessageContext.class)))
                .thenThrow(new EdcException("Error calling connector"));

        await().pollDelay(ofSeconds(1))
                .atMost(TEST_TIMEOUT)
                .untilAsserted(() -> {
                    var list = queryConnectorsApi();
                    assertThat(list).hasSize(1);

                    var connector = list.get(0);
                    assertThat(connector.getOnlineStatus()).isFalse();
                    assertThat(connector.getLastCrawled()).isNotNull();
                    assertThat(connector.getContractOffersCount()).isEqualTo(0);
                });
    }
}
