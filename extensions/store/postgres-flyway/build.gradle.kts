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

val edcVersion: String by project
val edcGroup: String by project
val flywayVersion: String by project
val postgresVersion: String by project

plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    implementation(edc.spi.core)
    implementation(edc.sql.core)
    implementation(edc.sql.pool)
    implementation(edc.transaction.local)

    implementation("org.postgresql:postgresql:${postgresVersion}")
    implementation("org.flywaydb:flyway-core:${flywayVersion}")

    testImplementation(edc.junit)
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}