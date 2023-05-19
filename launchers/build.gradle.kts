/*
 *  Copyright (c) 2022 Microsoft Corporation
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
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

dependencies {
    runtimeOnly(project(":core:federated-catalog"))
    runtimeOnly(project(":extensions:api:federated-catalog-api"))
    runtimeOnly(project(":extensions:store:fcc-node-directory-sql"))
    runtimeOnly(project(":extensions:store:fcc-store-sql"))
    runtimeOnly(project(":extensions:store:postgres-flyway"))
    runtimeOnly(edc.bundles.connector)
}

application {
    mainClass.set("org.eclipse.edc.boot.system.runtime.BaseRuntime")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
    archiveFileName.set("fc.jar")
}
