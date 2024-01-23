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

plugins {
    `java-library`
    id("io.swagger.core.v3.swagger-gradle-plugin")
}

dependencies {
    api(edc.spi.core)
    api(project(":spi:federated-catalog-spi"))

    runtimeOnly(edc.core.connector)

    implementation(edc.ids)
    implementation(edc.ids.jsonld.serdes)
    implementation(edc.jwt.spi)
    implementation(libs.jakarta.rsApi)
    implementation(libs.jersey.multipart)
    implementation(edc.api.management.config)
}
