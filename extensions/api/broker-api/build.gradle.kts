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
    api(edc.spi.core)
    api(project(":spi:federated-catalog-spi"))
    implementation(edc.spi.web)
    implementation(edc.ids)
    implementation(edc.iam.mock)
    implementation(project(":extensions:store:fcc-node-directory-sql"))

    runtimeOnly(edc.core.connector)
    implementation(ids.infomodel)
    implementation(libs.jakarta.rsApi)
    implementation(libs.jersey.multipart)
    implementation(edc.api.management.config)
}

publishing {
    publications {
        create<MavenPublication>("broker-api") {
            artifactId = "broker-api"
            from(components["java"])
        }
    }
}
