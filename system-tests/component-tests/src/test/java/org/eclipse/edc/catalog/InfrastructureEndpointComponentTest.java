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
import static org.eclipse.edc.catalog.TestFunctions.queryConnectorsApi;
import static org.eclipse.edc.catalog.TestFunctions.readJsonFile;
import static org.eclipse.edc.catalog.TestFunctions.sendInfrastructureController;

@ComponentTest
@ExtendWith(EdcExtension.class)
public class InfrastructureEndpointComponentTest {

    private static final Duration TEST_TIMEOUT = ofSeconds(10);
    private static final String REQUEST_FILES_BASE_PATH = "./catalog/infrastructure";

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

        assertThat(sendInfrastructureController(getRegisterRequest(true)).getStatusCode()).as(String.valueOf(200));
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

        assertThat(sendInfrastructureController(getRegisterRequest(true)).getStatusCode()).as(String.valueOf(200));
        await().pollDelay(ofSeconds(1))
                .atMost(TEST_TIMEOUT)
                .untilAsserted(() -> {
                    Response response = sendInfrastructureController(getRegisterRequest(true));
                    assertThat(response.getStatusCode()).as(String.valueOf(200));
                    assertThat(response.getBody().asString().contains(String.valueOf(RejectionReason.INTERNAL_RECIPIENT_ERROR.getId())));
                    assertThat(queryConnectorsApi()).hasSize(1);
                });
    }

    @Test
    @DisplayName("Register one Connector with invalid data")
    void queryRegisterConnector_withErrorInvalidData(RemoteMessageDispatcherRegistry registry) {
        createAndRegisterDispatcher(registry);

        Response response = sendInfrastructureController(getRegisterRequest(false));
        assertThat(response.getStatusCode()).as(String.valueOf(200));
        assertThat(response.getBody().asString().contains(String.valueOf(RejectionReason.INTERNAL_RECIPIENT_ERROR.getId())));
        assertThat(queryConnectorsApi()).hasSize(0);
    }

    @Test
    @DisplayName("Unregister one Connector")
    void queryUnregisterConnector_success(RemoteMessageDispatcherRegistry registry) {
        createAndRegisterDispatcher(registry);

        assertThat(sendInfrastructureController(getRegisterRequest(true)).getStatusCode()).as(String.valueOf(200));
        await().pollDelay(ofSeconds(1))
                .atMost(TEST_TIMEOUT)
                .untilAsserted(() -> {
                    assertThat(queryConnectorsApi()).hasSize(1);
                    assertThat(sendInfrastructureController(getUnregisterRequest(true)).getStatusCode()).as(String.valueOf(200));
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

        assertThat(sendInfrastructureController(getRegisterRequest(true)).getStatusCode()).as(String.valueOf(200));
        await().pollDelay(ofSeconds(1))
                .atMost(TEST_TIMEOUT)
                .untilAsserted(() -> {
                    assertThat(queryConnectorsApi()).hasSize(1);
                    Response response = sendInfrastructureController(getUnregisterRequest(false));
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
                    Response response = sendInfrastructureController(getUnregisterRequest(true));
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
                    assertThat(sendInfrastructureController(getUnknownMessageRequest()).getStatusCode()).as(String.valueOf(200));
                    Response response = sendInfrastructureController(getUnknownMessageRequest());
                    assertThat(response.getStatusCode()).as(String.valueOf(200));
                    assertThat(response.getBody().asString().contains(String.valueOf(RejectionReason.MESSAGE_TYPE_NOT_SUPPORTED.getId())));
                });
    }

    public static String getRegisterRequest(Boolean isValid) {
        if (isValid) {
            return readJsonFile(REQUEST_FILES_BASE_PATH + "/register-valid-request.json");
        } else {
            return readJsonFile(REQUEST_FILES_BASE_PATH + "/register-invalid-request.json");
        }
    }

    public static String getUnregisterRequest(Boolean isValid) {
        if (isValid) {
            return readJsonFile(REQUEST_FILES_BASE_PATH + "/unregister-valid-request.json");
        } else {
            return readJsonFile(REQUEST_FILES_BASE_PATH + "/unregister-invalid-request.json");
        }
    }

    public static String getUnknownMessageRequest() {
        return readJsonFile(REQUEST_FILES_BASE_PATH + "/unknown-message.json");
    }
}
