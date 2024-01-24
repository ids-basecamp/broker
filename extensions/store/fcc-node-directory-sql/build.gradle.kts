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
    `java-test-fixtures`
}

dependencies {
    api(project(":spi:federated-catalog-spi"))

    implementation(edc.util)
    implementation(edc.core.connector)
    implementation(edc.sql.core)
    implementation(edc.spi.core)

    // required for integration test
    testImplementation(edc.junit)
    testImplementation(edc.core.controlplane)
    testImplementation(libs.postgres)

    testImplementation(project(":core:federated-catalog"))
    testImplementation(project(":extensions:store:postgres-flyway"))

    testImplementation(testFixtures(edc.sql.core))
    testFixturesImplementation(project(":extensions:store:postgres-flyway"))
}
