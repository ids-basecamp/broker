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
 *       truzzt GmbH - PostgreSQL implementation
 *
 */

package de.truzzt.edc.extension.broker.api.types;

import org.eclipse.edc.protocol.ids.jsonld.JsonLd;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class TypeManagerUtilTest {


    private static final String CONNECTOR_UPDATE_MESSAGE_WITH_TOKEN = generateMessageWithToken(true);

    private static final String CONNECTOR_UPDATE_MESSAGE_WITHOUT_TOKEN = generateMessageWithToken(false);

    private TypeManagerUtil typeManagerUtil;

    @BeforeEach
    void setUp() {
        typeManagerUtil = new TypeManagerUtil(JsonLd.getObjectMapper());
    }

    @Test
    void parseMessageWithToken() {
        InputStream headerInputStream = new ByteArrayInputStream(CONNECTOR_UPDATE_MESSAGE_WITH_TOKEN.getBytes());

        var header = typeManagerUtil.parseMessage(headerInputStream);
        Assertions.assertNotNull(header);

        var jwt = typeManagerUtil.parseToken(header.getSecurityToken());
        Assertions.assertNotNull(jwt);
    }

    @Test
    void parseMessageWithoutToken() {
        InputStream headerInputStream = new ByteArrayInputStream(CONNECTOR_UPDATE_MESSAGE_WITHOUT_TOKEN.getBytes());

        var header = typeManagerUtil.parseMessage(headerInputStream);
        Assertions.assertNotNull(header);
    }

    private static String generateMessageWithToken(boolean withToken) {
        StringBuilder stb =  new StringBuilder();

        stb.append("{ ");
        stb.append("\"@context\" : { ");
        stb.append("\"ids\" : \"https://w3id.org/idsa/core/\",");
        stb.append("\"idsc\" : \"https://w3id.org/idsa/code/\"");
        stb.append(" },");
        stb.append("\"@type\" : \"ids:ConnectorUpdateMessage\",");
        stb.append("\"@id\" : \"https://w3id.org/idsa/autogen/connectorUpdateMessage/6d875403-cfea-4aad-979c-3515c2e71967\",");
        stb.append("\"ids:securityToken\" : { ");
        stb.append("\"@type\" : \"ids:DynamicAttributeToken\",");
        stb.append("\"@id\" : \"https://w3id.org/idsa/autogen/dynamicAttributeToken/7bbbd2c1-2d75-4e3d-bd10-c52d0381cab0\",");
        if(withToken) {
            stb.append("\"ids:tokenValue\" : ");
            stb.append("\"eyJ0eXAiOiJKV1QiLCJraWQiOiJkZWZhdWx0IiwiYWxnIjoiUlMyNTYifQ.eyJzY29wZXMiOlsia");
            stb.append("WRzYzpJRFNfQ09OTkVDVE9SX0FUVFJJQlVURVNfQUxMIl0sImF1ZCI6Imlkc2M6SURTX0NPTk5FQ1RPUlNfQUxMIiwiaXNz");
            stb.append("IjoiaHR0cHM6Ly9kYXBzLmFpc2VjLmZyYXVuaG9mZXIuZGUiLCJuYmYiOjE2MzQ2NTA3MzksImlhdCI6MTYzNDY1MDcz");
            stb.append("OSwianRpIjoiTVRneE9EUXdPVFF6TXpZd05qWXlOVFExTUE9PSIsImV4cCI6MTYzNDY1NDMzOSwic2VjdXJpdHlQcm9maW");
            stb.append("xlIjoiaWRzYzpCQVNFX1NFQ1VSSVRZX1BST0ZJTEUiLCJyZWZlcnJpbmdDb25uZWN0b3IiOiJodHRwOi8vYnJva2VyLmlkcy");
            stb.append("5pc3N0LmZyYXVuaG9mZXIuZGUuZGVtbyIsIkB0eXBlIjoiaWRzOkRhdFBheWxvYWQiLCJAY29udGV4dCI6Imh0dHBzOi8vdz");
            stb.append("NpZC5vcmcvaWRzYS9jb250ZXh0cy9jb250ZXh0Lmpzb25sZCIsInRyYW5zcG9ydENlcnRzU2hhMjU2IjoiOTc0ZTYzMjRmMT");
            stb.append("JmMTA5MTZmNDZiZmRlYjE4YjhkZDZkYTc4Y2M2YTZhMDU2NjAzMWZhNWYxYTM5ZWM4ZTYwMCIsInN1YiI6IjkyOjE0OkU3O");
            stb.append("kFDOjEwOjIyOkYyOkNDOjA1OjZFOjJBOjJCOjhEOkRCOjEwOkQ2OjREOkEwOkExOjUzOmtleWlkOkNCOjhDOkM3OkI2Ojg1Oj");
            stb.append("c5OkE4OjIzOkE2OkNCOjE1OkFCOjE3OjUwOjJGOkU2OjY1OjQzOjVEOkU4In0.Qw3gWMgwnKQyVatbsozcin6qtQbLyXlk");
            stb.append("6QdaLajGaDmxSYqCKEcAje4kiDp5Fqj04WPmVyF0k8c1BJA3KGnaW3Qcikv4MNxqqoenvKIrSTokXsA7-osqBCfxLhV-s2l");
            stb.append("SXVTAtV_Q7f71eSoR5j-7nPPX8_nf4Xup4_VzfnwRmnuAbLfHfWThbupxFazC34r3waXCltOTFVa_XDlwEDMpPY7vEPeaqIt");
            stb.append("2t6ofVGo_HF86UB19liL-UZvp0uSE9z2fhloyxOrx9B_xavGS7pP6oRaumSJEN_x9dfdeDS98HQ_oBSSGBzaI4fM7ik35Yg4");
            stb.append("2KQwmkZesD6P_YSEzVLcJDg\",");
            stb.append("\"ids : tokenFormat\" : { ");
            stb.append("\"@id\" : \"idsc:JWT\"");
            stb.append(" } ");
            stb.append(" },");
        }
        stb.append("\"ids:senderAgent\" : { ");
        stb.append("\"@id\" : \"http://example.org\"");
        stb.append(" },");
        stb.append("\"ids:modelVersion\" : \"4.0.0\",");
        stb.append("\"ids:issuerConnector\" : { ");
        stb.append("\"@id\" : \"https://test.connector.de/testDataModel\"");
        stb.append(" },");
        stb.append("\"ids:issued\" : { ");
        stb.append("\"@value\" : \"2021-06-23T17:27:23.566+02:00\",");
        stb.append("\"@type\" : \"http://www.w3.org/2001/XMLSchema#dateTimeStamp\"");
        stb.append(" }, ");
        stb.append("\"ids:affectedConnector\" : { ");
        stb.append("\"@id\" : \"https://test.connector.de/testDataModel\"");
        stb.append(" } ");
        stb.append(" } ");

        return stb.toString();
    }
}
