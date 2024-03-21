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
 *       truzzt GmbH - PostgreSQL implementation
 *
 */

rootProject.name = "broker"

pluginManagement {
    repositories {
        maven {
            val user = providers.gradleProperty("gitHubUserName")
            val token = providers.gradleProperty("gitHubUserPassword")
            url = uri("https://maven.pkg.github.com/ids-basecamp/gradle-plugins-fork")
            credentials {
                username = user.orNull
                password = token.orNull
            }
        }
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }
}

dependencyResolutionManagement {
    repositories {
        val user = providers.gradleProperty("gitHubUserName")
        val token = providers.gradleProperty("gitHubUserPassword")
        maven {
            url = uri("https://maven.pkg.github.com/ids-basecamp/gradle-plugins-fork")
            credentials {
                username = user.orNull
                password = token.orNull
            }
        }
        maven {
            url = uri("https://maven.pkg.github.com/ids-basecamp/edc-fork")
            credentials {
                username = user.orNull
                password = token.orNull
            }
        }
        mavenCentral()
        mavenLocal()
    }
    versionCatalogs {
        val gradlePluginsGroup = providers.gradleProperty("gradlePluginsGroup")
        val gradlePluginsVersion = providers.gradleProperty("gradlePluginsVersion")
        create("libs") {
            from(gradlePluginsGroup.get() + ":edc-versions:" + gradlePluginsVersion.get())
        }

        val edcGroup = providers.gradleProperty("edcGroup")
        val edcVersion = providers.gradleProperty("edcVersion")
        create("edc") {
            version("edc", edcVersion.get())
            library("spi-catalog", edcGroup.get(), "catalog-spi").versionRef("edc")
            library("spi-core", edcGroup.get(), "core-spi").versionRef("edc")
            library("spi-web", edcGroup.get(), "web-spi").versionRef("edc")
            library("util", edcGroup.get(), "util").versionRef("edc")
            library("boot", edcGroup.get(), "boot").versionRef("edc")
            library("config-filesystem", edcGroup.get(), "configuration-filesystem").versionRef("edc")
            library("core-controlplane", edcGroup.get(), "control-plane-core").versionRef("edc")
            library("core-connector", edcGroup.get(), "connector-core").versionRef("edc")
            library("core-jetty", edcGroup.get(), "jetty-core").versionRef("edc")
            library("core-jersey", edcGroup.get(), "jersey-core").versionRef("edc")
            library("junit", edcGroup.get(), "junit").versionRef("edc")
            library("api-management-config", edcGroup.get(), "management-api-configuration").versionRef("edc")
            library("api-management", edcGroup.get(), "management-api").versionRef("edc")
            library("api-observability", edcGroup.get(), "api-observability").versionRef("edc")
            library("ext-http", edcGroup.get(), "http").versionRef("edc")
            library("spi-ids", edcGroup.get(), "ids-spi").versionRef("edc")
            library("ids", edcGroup.get(), "ids").versionRef("edc")
            library("ids-jsonld-serdes", edcGroup.get(), "ids-jsonld-serdes").versionRef("edc")
            library("jwt-spi", edcGroup.get(), "jwt-spi").versionRef("edc")
            library("iam-mock", edcGroup.get(), "iam-mock").versionRef("edc")
            library("oauth2-core", edcGroup.get(), "oauth2-core").versionRef("edc")
            library("sql-core", edcGroup.get(), "sql-core").versionRef("edc")
            library("sql-pool", edcGroup.get(), "sql-pool-apache-commons").versionRef("edc")
            library("transaction-local", edcGroup.get(), "transaction-local").versionRef("edc")
            library("vault-filesystem", edcGroup.get(), "vault-filesystem").versionRef("edc")

            // DPF modules
            library("dpf-transferclient", edcGroup.get(), "data-plane-client").versionRef("edc")
            library("dpf-selector-client", edcGroup.get(), "data-plane-selector-client").versionRef("edc")
            library("dpf-selector-spi", edcGroup.get(), "data-plane-selector-spi").versionRef("edc")
            library("dpf-selector-core", edcGroup.get(), "data-plane-selector-core").versionRef("edc")
            library("dpf-framework", edcGroup.get(), "data-plane-framework").versionRef("edc")

            bundle(
                "connector",
                    listOf("boot", "core-connector", "core-jersey", "core-controlplane", "api-observability")
            )
            bundle(
                "dpf",
                    listOf("dpf-transferclient", "dpf-selector-client", "dpf-selector-spi", "dpf-selector-core", "dpf-framework")
            )
        }
    }
}

include(":core:federated-catalog")
include(":extensions:store:fcc-node-directory-sql")
include(":extensions:store:fcc-store-sql")
include(":extensions:store:postgres-flyway")
include(":extensions:api:broker-api")
include(":extensions:api:federated-catalog-api")
include(":spi:federated-catalog-spi")
include(":launchers:in-memory-local")
include(":launchers:postgres-local")
include(":launchers:postgres-prod")
include(":system-tests:component-tests")
include(":system-tests:end2end-test:connector-runtime")
include(":system-tests:end2end-test:catalog-runtime")
include(":system-tests:end2end-test:e2e-junit-runner")
