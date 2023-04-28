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