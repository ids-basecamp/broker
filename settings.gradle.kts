rootProject.name = "federated-catalog"

include(":core:federated-catalog-core")
include(":extensions:store:fcc-node-directory-cosmos")
include(":extensions:api:federated-catalog-api")
include(":spi:federated-catalog-spi")
include(":launchers")
include(":system-tests:component-tests")
include(":system-tests:end2end-test:connector-runtime")
include(":system-tests:end2end-test:catalog-runtime")
include(":system-tests:end2end-test:e2e-junit-runner")
include(":version-catalog")

// this is needed to have access to snapshot builds of plugins
pluginManagement {
    repositories {
        mavenLocal()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
        mavenCentral()
    }
}
