package org.eclipse.edc.catalog;

import de.truzzt.edc.extension.broker.api.types.ids.RejectionReason;
import io.restassured.response.Response;
import org.eclipse.edc.junit.annotations.ComponentTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.message.RemoteMessageDispatcherRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Duration;
import java.util.Map;

import static java.lang.String.valueOf;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.eclipse.edc.catalog.TestFunctions.createAndRegisterDispatcher;
import static org.eclipse.edc.catalog.TestFunctions.getHeader;
import static org.eclipse.edc.catalog.TestFunctions.getHeaderUnknownMessage;
import static org.eclipse.edc.catalog.TestFunctions.getUnregisterHeader;
import static org.eclipse.edc.catalog.TestFunctions.queryConnectorsApi;
import static org.eclipse.edc.catalog.TestFunctions.queryInfrastructureController;

@ComponentTest
@ExtendWith(EdcExtension.class)
public class InfrastructureEndpointComponentTest {

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
    @DisplayName("Register one Connector")
    void queryRegisterConnector_success(RemoteMessageDispatcherRegistry registry) {
        createAndRegisterDispatcher(registry);

        assertThat(queryInfrastructureController(getHeader(true)).getStatusCode()).as(String.valueOf(200));
        await().pollDelay(ofSeconds(1))
                .atMost(TEST_TIMEOUT)
                .untilAsserted(() -> {
                    assertThat(queryConnectorsApi()).hasSize(1);
                });
    }

    @Test
    @DisplayName("Register one Connector Duplicated")
    void queryRegisterConnector_withErrorDuplicated(RemoteMessageDispatcherRegistry registry) {
        createAndRegisterDispatcher(registry);

        assertThat(queryInfrastructureController(getHeader(true)).getStatusCode()).as(String.valueOf(200));
        await().pollDelay(ofSeconds(1))
                .atMost(TEST_TIMEOUT)
                .untilAsserted(() -> {
                    Response response = queryInfrastructureController(getHeader(true));
                    assertThat(response.getStatusCode()).as(String.valueOf(200));
                    assertThat(response.getBody().asString().contains(String.valueOf(RejectionReason.INTERNAL_RECIPIENT_ERROR.getId())));
                    assertThat(queryConnectorsApi()).hasSize(1);
                });
    }

    @Test
    @DisplayName("Register one Connector with invalid data")
    void queryRegisterConnector_withErrorInvalidData(RemoteMessageDispatcherRegistry registry) {
        createAndRegisterDispatcher(registry);

        Response response = queryInfrastructureController(getHeader(false));
        assertThat(response.getStatusCode()).as(String.valueOf(200));
        assertThat(response.getBody().asString().contains(String.valueOf(RejectionReason.INTERNAL_RECIPIENT_ERROR.getId())));
        assertThat(queryConnectorsApi()).hasSize(0);
    }

    @Test
    @DisplayName("Unregister one Connector")
    void queryUnregisterConnector_success(RemoteMessageDispatcherRegistry registry) {
        createAndRegisterDispatcher(registry);

        assertThat(queryInfrastructureController(getHeader(true)).getStatusCode()).as(String.valueOf(200));
        await().pollDelay(ofSeconds(1))
                .atMost(TEST_TIMEOUT)
                .untilAsserted(() -> {
                    assertThat(queryConnectorsApi()).hasSize(1);
                    assertThat(queryInfrastructureController(getUnregisterHeader(true)).getStatusCode()).as(String.valueOf(200));
                    await().pollDelay(ofSeconds(1))
                            .atMost(TEST_TIMEOUT)
                            .untilAsserted(() -> {
                                assertThat(queryConnectorsApi()).hasSize(0);
                            });
                });

    }

    @Test
    @DisplayName("Unregister one Connector with invalid data ")
    void queryUnregisterConnector_withErrorInvalidData(RemoteMessageDispatcherRegistry registry) {
        createAndRegisterDispatcher(registry);

        assertThat(queryInfrastructureController(getHeader(true)).getStatusCode()).as(String.valueOf(200));
        await().pollDelay(ofSeconds(1))
                .atMost(TEST_TIMEOUT)
                .untilAsserted(() -> {
                    assertThat(queryConnectorsApi()).hasSize(1);
                    Response response = queryInfrastructureController(getUnregisterHeader(false));
                    assertThat(response.getStatusCode()).as(String.valueOf(200));
                    assertThat(response.getBody().asString().contains(String.valueOf(RejectionReason.INTERNAL_RECIPIENT_ERROR.getId())));
                    assertThat(queryConnectorsApi()).hasSize(1);
                });

    }

    @Test
    @DisplayName("Unregister one connector not registered")
    void queryUnregisterConnector_withConnectorNotRegistered(RemoteMessageDispatcherRegistry registry) {
        createAndRegisterDispatcher(registry);

        await().pollDelay(ofSeconds(1))
                .atMost(TEST_TIMEOUT)
                .untilAsserted(() -> {
                    Response response = queryInfrastructureController(getUnregisterHeader(true));
                    assertThat(response.getStatusCode()).as(String.valueOf(200));
                    assertThat(response.getBody().asString().contains(String.valueOf(RejectionReason.INTERNAL_RECIPIENT_ERROR.getId())));
                    assertThat(queryConnectorsApi()).hasSize(0);
                });
    }

    @Test
    @DisplayName("Call endpoint with unknown message")
    void queryUnregisterConnector_withUnknownMessage(RemoteMessageDispatcherRegistry registry) {
        createAndRegisterDispatcher(registry);

        await().pollDelay(ofSeconds(1))
                .atMost(TEST_TIMEOUT)
                .untilAsserted(() -> {
                    assertThat(queryInfrastructureController(getHeaderUnknownMessage()).getStatusCode()).as(String.valueOf(200));
                    Response response = queryInfrastructureController(getHeaderUnknownMessage());
                    assertThat(response.getStatusCode()).as(String.valueOf(200));
                    assertThat(response.getBody().asString().contains(String.valueOf(RejectionReason.MESSAGE_TYPE_NOT_SUPPORTED.getId())));
                });

    }
}
