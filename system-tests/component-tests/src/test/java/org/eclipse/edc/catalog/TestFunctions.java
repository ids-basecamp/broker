package org.eclipse.edc.catalog;

import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.eclipse.edc.catalog.spi.Catalog;
import org.eclipse.edc.catalog.spi.FederatedCacheNode;
import org.eclipse.edc.catalog.spi.FederatedCacheNodeDirectory;
import org.eclipse.edc.catalog.spi.model.FederatedCatalogCacheQuery;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractOffer;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.message.RemoteMessageDispatcher;
import org.eclipse.edc.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.edc.spi.types.domain.asset.Asset;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.eclipse.edc.junit.testfixtures.TestUtils.getFreePort;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestFunctions {
    public static final String BASE_PATH = "/api";
    public static final int PORT = getFreePort();
    private static final String PATH = "/federatedcatalog";

    private static final String CONNECTOR_PATH = "/connectors";

    private static final String INFRASTRUCTURE_PATH = "/infrastructure";

    private static final TypeRef<List<ContractOffer>> CONTRACT_OFFER_LIST_TYPE = new TypeRef<>() {
    };

    private static final TypeRef<List<FederatedCacheNode>> FEDERATED_CACHE_NODE_LIST_TYPE = new TypeRef<>() {
    };

    public static RemoteMessageDispatcher createAndRegisterDispatcher(RemoteMessageDispatcherRegistry registry) {
        var dispatcher = mock(RemoteMessageDispatcher.class);
        when(dispatcher.protocol()).thenReturn("ids-multipart");
        registry.register(dispatcher);

        return dispatcher;
    }

    public static CompletableFuture<Catalog> emptyCatalog() {
        return completedFuture(catalogBuilder()
                .build());
    }

    public static Catalog.Builder catalogBuilder() {
        return Catalog.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .contractOffers(Collections.emptyList());
    }

    public static CompletableFuture<Catalog> catalogOf(ContractOffer... offers) {
        return completedFuture(catalogBuilder().contractOffers(asList(offers)).build());
    }

    public static CompletableFuture<Catalog> randomCatalog(int howMany) {
        return completedFuture(catalogBuilder()
                .contractOffers(IntStream.range(0, howMany).mapToObj(i -> createOffer("Offer_" + UUID.randomUUID())).collect(Collectors.toList()))
                .build());
    }

    public static ContractOffer createOffer(String id) {
        return ContractOffer.Builder.newInstance()
                .id(id)
                .asset(Asset.Builder.newInstance().id(id).build())
                .policy(Policy.Builder.newInstance().build())
                .contractStart(ZonedDateTime.now())
                .contractEnd(ZonedDateTime.now().plus(365, ChronoUnit.DAYS))
                .build();
    }

    public static void insertSingle(FederatedCacheNodeDirectory directory) {
        directory.insert(new FederatedCacheNode("test-node", "http://test-node.com", singletonList("ids-multipart")));
    }

    public static void insertMultiple(FederatedCacheNodeDirectory directory) {
        directory.insert(new FederatedCacheNode("test-node1", "http://test-node1.com", singletonList("ids-multipart")));
        directory.insert(new FederatedCacheNode("test-node2", "http://test-node2.com", singletonList("ids-multipart")));
        directory.insert(new FederatedCacheNode("test-node3", "http://test-node3.com", singletonList("ids-multipart")));
    }

    public static List<ContractOffer> queryCatalogApi() {
        return baseRequest()
                .body(FederatedCatalogCacheQuery.Builder.newInstance().build())
                .post(PATH)
                .body()
                .as(CONTRACT_OFFER_LIST_TYPE);
    }

    public static List<FederatedCacheNode> queryConnectorsApi() {
        return baseRequest()
                .body(FederatedCatalogCacheQuery.Builder.newInstance().build())
                .post(CONNECTOR_PATH)
                .body()
                .as(FEDERATED_CACHE_NODE_LIST_TYPE);
    }

    public static Response queryInfrastructureController(String header) {
        return given()
                .baseUri("http://localhost:" + PORT)
                .basePath(BASE_PATH)
                .contentType("multipart/form-data")
                .multiPart("header", header)
                .when()
                .post(INFRASTRUCTURE_PATH);
    }


    private static RequestSpecification baseRequest() {
        return given()
                .header("X-API-Key", "password")
                .baseUri("http://localhost:" + PORT)
                .basePath(BASE_PATH)
                .contentType(ContentType.JSON)
                .when();
    }

    public static String getHeader(Boolean isValid){
        String header = "{" +
                "  \"@context\" : {" +
                "    \"ids\" : \"https://w3id.org/idsa/core/\"," +
                "    \"idsc\" : \"https://w3id.org/idsa/code/\"" +
                "  }," +
                "  \"@type\" : \"ids:ConnectorUpdateMessage\"," +
                "  \"@id\" : \"https://w3id.org/idsa/autogen/connectorUpdateMessage/6d875403-cfea-4aad-979c-3515c2e71967\"," +
                "  \"ids:securityToken\" : {" +
                "    \"@type\" : \"ids:DynamicAttributeToken\"," +
                "    \"@id\" : \"https://w3id.org/idsa/autogen/dynamicAttributeToken/7bbbd2c1-2d75-4e3d-bd10-c52d0381cab0\",";

                if(isValid){
                    header +="    \"ids:tokenValue\" : \"eyJ0eXAiOiJKV1QiLCJraWQiOiJkZWZhdWx0IiwiYWxnIjoiUlMyNTYifQ.eyJzY29wZXMiOlsiaWRzYzpJRFNfQ09OTkVDVE9SX0FUVFJJQlVURVNfQUxMIl0sImF1ZCI6Imlkc2M6SURTX0NPTk5FQ1RPUlNfQUxMIiwiaXNzIjoiaHR0cHM6Ly9kYXBzLmFpc2VjLmZyYXVuaG9mZXIuZGUiLCJuYmYiOjE2MzQ2NTA3MzksImlhdCI6MTYzNDY1MDczOSwianRpIjoiTVRneE9EUXdPVFF6TXpZd05qWXlOVFExTUE9PSIsImV4cCI6MTYzNDY1NDMzOSwic2VjdXJpdHlQcm9maWxlIjoiaWRzYzpCQVNFX1NFQ1VSSVRZX1BST0ZJTEUiLCJyZWZlcnJpbmdDb25uZWN0b3IiOiJodHRwOi8vYnJva2VyLmlkcy5pc3N0LmZyYXVuaG9mZXIuZGUuZGVtbyIsIkB0eXBlIjoiaWRzOkRhdFBheWxvYWQiLCJAY29udGV4dCI6Imh0dHBzOi8vdzNpZC5vcmcvaWRzYS9jb250ZXh0cy9jb250ZXh0Lmpzb25sZCIsInRyYW5zcG9ydENlcnRzU2hhMjU2IjoiOTc0ZTYzMjRmMTJmMTA5MTZmNDZiZmRlYjE4YjhkZDZkYTc4Y2M2YTZhMDU2NjAzMWZhNWYxYTM5ZWM4ZTYwMCIsInN1YiI6IjkyOjE0OkU3OkFDOjEwOjIyOkYyOkNDOjA1OjZFOjJBOjJCOjhEOkRCOjEwOkQ2OjREOkEwOkExOjUzOmtleWlkOkNCOjhDOkM3OkI2Ojg1Ojc5OkE4OjIzOkE2OkNCOjE1OkFCOjE3OjUwOjJGOkU2OjY1OjQzOjVEOkU4In0.Qw3gWMgwnKQyVatbsozcin6qtQbLyXlk6QdaLajGaDmxSYqCKEcAje4kiDp5Fqj04WPmVyF0k8c1BJA3KGnaW3Qcikv4MNxqqoenvKIrSTokXsA7-osqBCfxLhV-s2lSXVTAtV_Q7f71eSoR5j-7nPPX8_nf4Xup4_VzfnwRmnuAbLfHfWThbupxFazC34r3waXCltOTFVa_XDlwEDMpPY7vEPeaqIt2t6ofVGo_HF86UB19liL-UZvp0uSE9z2fhloyxOrx9B_xavGS7pP6oRaumSJEN_x9dfdeDS98HQ_oBSSGBzaI4fM7ik35Yg42KQwmkZesD6P_YSEzVLcJDg\"," ;
                } else {
                    header +="    \"ids:tokenValue\" : \"eyJ0eXAiOiJKV1XMiOlsiaWRzYzpJRFNfQ09OTkVDVE9SX0FUVFJJQlVURVNfQUxMIl0sImF1ZCI6Imlkc2M6SURTX0NPTk5FQ1RPUlNfQUxMIiwiaXNzIjoiaHR0cHM6Ly9kYXBzLmFpc2VjLmZyYXVuaG9mZXIuZGUiLCJuYmYiOjE2MzQ2NTA3MzksImlhdCI6MTYzNDY1MDczOSwianRpIjoiTVRneE9EUXdPVFF6TXpZd05qWXlOVFExTUE9PSIsImV4cCI6MTYzNDY1NDMzOSwic2VjdXJpdHlQcm9maWxlIjoiaWRzYzpCQVNFX1NFQ1VSSVRZX1BST0ZJTEUiLCJyZWZlcnJpbmdDb25uZWN0b3IiOiJodHRwOi8vYnJva2VyLmlkcy5pc3N0LmZyYXVuaG9mZXIuZGUuZGVtbyIsIkB0eXBlIjoiaWRzOkRhdFBheWxvYWQiLCJAY29udGV4dCI6Imh0dHBzOi8vdzNpZC5vcmcvaWRzYS9jb250ZXh0cy9jb250ZXh0Lmpzb25sZCIsInRyYW5zcG9ydENlcnRzU2hhMjU2IjoiOTc0ZTYzMjRmMTJmMTA5MTZmNDZiZmRlYjE4YjhkZDZkYTc4Y2M2YTZhMDU2NjAzMWZhNWYxYTM5ZWM4ZTYwMCIsInN1YiI6IjkyOjE0OkU3OkFDOjEwOjIyOkYyOkNDOjA1OjZFOjJBOjJCOjhEOkRCOjEwOkQ2OjREOkEwOkExOjUzOmtleWlkOkNCOjhDOkM3OkI2Ojg1Ojc5OkE4OjIzOkE2OkNCOjE1OkFCOjE3OjUwOjJGOkU2OjY1OjQzOjVEOkU4In0.Qw3gWMgwnKQyVatbsozcin6qtQbLyXlk6QdaLajGaDmxSYqCKEcAje4kiDp5Fqj04WPmVyF0k8c1BJA3KGnaW3Qcikv4MNxqqoenvKIrSTokXsA7-osqBCfxLhV-s2lSXVTAtV_Q7f71eSoR5j-7nPPX8_nf4Xup4_VzfnwRmnuAbLfHfWThbupxFazC34r3waXCltOTFVa_XDlwEDMpPY7vEPeaqIt2t6ofVGo_HF86UB19liL-UZvp0uSE9z2fhloyxOrx9B_xavGS7pP6oRaumSJEN_x9dfdeDS98HQ_oBSSGBzaI4fM7ik35Yg42KQwmkZesD6P_YSEzVLcJDg\"," ;

                }

                header += "    \"ids:tokenFormat\" : {" +
                "      \"@id\" : \"idsc:JWT\"" +
                "    }" +
                "  }," +
                "  \"ids:senderAgent\" : {" +
                "    \"@id\" : \"http://example.org\"" +
                "  }," +
                "  \"ids:modelVersion\" : \"4.0.0\"," +
                "  \"ids:issuerConnector\" : {" +
                "    \"@id\" : \"http://localhost:9193\"" +
                "  }," +
                "  \"ids:issued\" : {" +
                "    \"@value\" : \"2021-06-23T17:27:23.566+02:00\"," +
                "    \"@type\" : \"http://www.w3.org/2001/XMLSchema#dateTimeStamp\"" +
                "  }," +
                "  \"ids:affectedConnector\" : {" +
                "    \"@id\" : \"http://localhost:9193\"" +
                "  }" +
                "}";

                return header;
    }

    public static String getUnregisterHeader(Boolean isValid){
        String header =  "{" +
                "  \"@context\" : {" +
                "    \"ids\" : \"https://w3id.org/idsa/core/\"," +
                "    \"idsc\" : \"https://w3id.org/idsa/code/\"" +
                "  }," +
                "  \"@type\" : \"ids:ConnectorUnavailableMessage\"," +
                "  \"@id\" : \"https://w3id.org/idsa/autogen/connectorUpdateMessage/6d875403-cfea-4aad-979c-3515c2e71967\"," +
                "  \"ids:securityToken\" : {\n" +
                "    \"@type\" : \"ids:DynamicAttributeToken\",\n" +
                "    \"@id\" : \"https://w3id.org/idsa/autogen/dynamicAttributeToken/7bbbd2c1-2d75-4e3d-bd10-c52d0381cab0\"," ;

                if(isValid){
                    header += "    \"ids:tokenValue\" : \"eyJ0eXAiOiJKV1QiLCJraWQiOiJkZWZhdWx0IiwiYWxnIjoiUlMyNTYifQ.eyJzY29wZXMiOlsiaWRzYzpJRFNfQ09OTkVDVE9SX0FUVFJJQlVURVNfQUxMIl0sImF1ZCI6Imlkc2M6SURTX0NPTk5FQ1RPUlNfQUxMIiwiaXNzIjoiaHR0cHM6Ly9kYXBzLmFpc2VjLmZyYXVuaG9mZXIuZGUiLCJuYmYiOjE2MzQ2NTA3MzksImlhdCI6MTYzNDY1MDczOSwianRpIjoiTVRneE9EUXdPVFF6TXpZd05qWXlOVFExTUE9PSIsImV4cCI6MTYzNDY1NDMzOSwic2VjdXJpdHlQcm9maWxlIjoiaWRzYzpCQVNFX1NFQ1VSSVRZX1BST0ZJTEUiLCJyZWZlcnJpbmdDb25uZWN0b3IiOiJodHRwOi8vYnJva2VyLmlkcy5pc3N0LmZyYXVuaG9mZXIuZGUuZGVtbyIsIkB0eXBlIjoiaWRzOkRhdFBheWxvYWQiLCJAY29udGV4dCI6Imh0dHBzOi8vdzNpZC5vcmcvaWRzYS9jb250ZXh0cy9jb250ZXh0Lmpzb25sZCIsInRyYW5zcG9ydENlcnRzU2hhMjU2IjoiOTc0ZTYzMjRmMTJmMTA5MTZmNDZiZmRlYjE4YjhkZDZkYTc4Y2M2YTZhMDU2NjAzMWZhNWYxYTM5ZWM4ZTYwMCIsInN1YiI6IjkyOjE0OkU3OkFDOjEwOjIyOkYyOkNDOjA1OjZFOjJBOjJCOjhEOkRCOjEwOkQ2OjREOkEwOkExOjUzOmtleWlkOkNCOjhDOkM3OkI2Ojg1Ojc5OkE4OjIzOkE2OkNCOjE1OkFCOjE3OjUwOjJGOkU2OjY1OjQzOjVEOkU4In0.Qw3gWMgwnKQyVatbsozcin6qtQbLyXlk6QdaLajGaDmxSYqCKEcAje4kiDp5Fqj04WPmVyF0k8c1BJA3KGnaW3Qcikv4MNxqqoenvKIrSTokXsA7-osqBCfxLhV-s2lSXVTAtV_Q7f71eSoR5j-7nPPX8_nf4Xup4_VzfnwRmnuAbLfHfWThbupxFazC34r3waXCltOTFVa_XDlwEDMpPY7vEPeaqIt2t6ofVGo_HF86UB19liL-UZvp0uSE9z2fhloyxOrx9B_xavGS7pP6oRaumSJEN_x9dfdeDS98HQ_oBSSGBzaI4fM7ik35Yg42KQwmkZesD6P_YSEzVLcJDg\",";

                } else {
                    header += "    \"ids:tokenValue\" : \" \",";

                }

                header += "    \"ids:tokenFormat\" : {" +
                "      \"@id\" : \"idsc:JWT\"" +
                "    }\n" +
                "  },  \n" +
                "  \"ids:senderAgent\" : {\n" +
                "    \"@id\" : \"http://example.org\"" +
                "  },\n" +
                "  \"ids:modelVersion\" : \"4.0.0\"," +
                "  \"ids:issuerConnector\" : {" +
                "    \"@id\" : \"https://test.connector.de/testDataModel\"" +
                "  },\n" +
                "  \"ids:issued\" : {\n" +
                "    \"@value\" : \"2021-06-23T17:27:23.566+02:00\"," +
                "    \"@type\" : \"http://www.w3.org/2001/XMLSchema#dateTimeStamp\"" +
                "  },\n" +
                "  \"ids:affectedConnector\" : {" +
                "    \"@id\" : \"https://test.connector.de/testDataModel\"" +
                "  }" +
                "}";

                return header;
    }

    public static String getHeaderUnknownMessage(){
        String header =  "{" +
                "  \"@context\" : {" +
                "    \"ids\" : \"https://w3id.org/idsa/core/\"," +
                "    \"idsc\" : \"https://w3id.org/idsa/code/\"" +
                "  }," +
                "  \"@type\" : \" Blank \"," +
                "  \"@id\" : \"https://w3id.org/idsa/autogen/connectorUpdateMessage/6d875403-cfea-4aad-979c-3515c2e71967\"," +
                "  \"ids:securityToken\" : {" +
                "    \"@type\" : \"ids:DynamicAttributeToken\"," +
                "    \"@id\" : \"https://w3id.org/idsa/autogen/dynamicAttributeToken/7bbbd2c1-2d75-4e3d-bd10-c52d0381cab0\"," +
                "    \"ids:tokenValue\" : \"eyJ0eXAiOiJKV1QiLCJraWQiOiJkZWZhdWx0IiwiYWxnIjoiUlMyNTYifQ.eyJzY29wZXMiOlsiaWRzYzpJRFNfQ09OTkVDVE9SX0FUVFJJQlVURVNfQUxMIl0sImF1ZCI6Imlkc2M6SURTX0NPTk5FQ1RPUlNfQUxMIiwiaXNzIjoiaHR0cHM6Ly9kYXBzLmFpc2VjLmZyYXVuaG9mZXIuZGUiLCJuYmYiOjE2MzQ2NTA3MzksImlhdCI6MTYzNDY1MDczOSwianRpIjoiTVRneE9EUXdPVFF6TXpZd05qWXlOVFExTUE9PSIsImV4cCI6MTYzNDY1NDMzOSwic2VjdXJpdHlQcm9maWxlIjoiaWRzYzpCQVNFX1NFQ1VSSVRZX1BST0ZJTEUiLCJyZWZlcnJpbmdDb25uZWN0b3IiOiJodHRwOi8vYnJva2VyLmlkcy5pc3N0LmZyYXVuaG9mZXIuZGUuZGVtbyIsIkB0eXBlIjoiaWRzOkRhdFBheWxvYWQiLCJAY29udGV4dCI6Imh0dHBzOi8vdzNpZC5vcmcvaWRzYS9jb250ZXh0cy9jb250ZXh0Lmpzb25sZCIsInRyYW5zcG9ydENlcnRzU2hhMjU2IjoiOTc0ZTYzMjRmMTJmMTA5MTZmNDZiZmRlYjE4YjhkZDZkYTc4Y2M2YTZhMDU2NjAzMWZhNWYxYTM5ZWM4ZTYwMCIsInN1YiI6IjkyOjE0OkU3OkFDOjEwOjIyOkYyOkNDOjA1OjZFOjJBOjJCOjhEOkRCOjEwOkQ2OjREOkEwOkExOjUzOmtleWlkOkNCOjhDOkM3OkI2Ojg1Ojc5OkE4OjIzOkE2OkNCOjE1OkFCOjE3OjUwOjJGOkU2OjY1OjQzOjVEOkU4In0.Qw3gWMgwnKQyVatbsozcin6qtQbLyXlk6QdaLajGaDmxSYqCKEcAje4kiDp5Fqj04WPmVyF0k8c1BJA3KGnaW3Qcikv4MNxqqoenvKIrSTokXsA7-osqBCfxLhV-s2lSXVTAtV_Q7f71eSoR5j-7nPPX8_nf4Xup4_VzfnwRmnuAbLfHfWThbupxFazC34r3waXCltOTFVa_XDlwEDMpPY7vEPeaqIt2t6ofVGo_HF86UB19liL-UZvp0uSE9z2fhloyxOrx9B_xavGS7pP6oRaumSJEN_x9dfdeDS98HQ_oBSSGBzaI4fM7ik35Yg42KQwmkZesD6P_YSEzVLcJDg\"," +
                "    \"ids:tokenFormat\" : {" +
                "      \"@id\" : \"idsc:JWT\"" +
                "    }" +
                "  },  " +
                "  \"ids:senderAgent\" : {" +
                "    \"@id\" : \"http://example.org\"" +
                "  }," +
                "  \"ids:modelVersion\" : \"4.0.0\"," +
                "  \"ids:issuerConnector\" : {" +
                "    \"@id\" : \"https://test.connector.de/testDataModel\"" +
                "  }," +
                "  \"ids:issued\" : {" +
                "    \"@value\" : \"2021-06-23T17:27:23.566+02:00\"," +
                "    \"@type\" : \"http://www.w3.org/2001/XMLSchema#dateTimeStamp\"" +
                "  },\n" +
                "  \"ids:affectedConnector\" : {" +
                "    \"@id\" : \"https://test.connector.de/testDataModel\"" +
                "  }" +
                "}";

        return header;
    }
}
