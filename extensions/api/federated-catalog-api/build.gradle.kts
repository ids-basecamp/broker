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
 *       Microsoft Corporation - initial API and implementation
 *
 */

plugins {
    `java-library`
    id("io.swagger.core.v3.swagger-gradle-plugin")
}

dependencies {
    api(libs.edc.spi.core)
    implementation(libs.edc.spi.web)
    api(project(":spi:federated-catalog-spi"))

    runtimeOnly(libs.edc.core.connector)
    implementation(libs.edc.api.management.config)

    // required for integration test
    testImplementation(testFixtures(project(":core:federated-catalog-core"))) // provides the TestUtil
    testImplementation(libs.edc.junit)
    testImplementation(libs.edc.ext.http)
    testImplementation(libs.restAssured)
}

edcBuild {
    swagger {
        apiGroup.set("management-api")
    }
}
