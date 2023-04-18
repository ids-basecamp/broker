plugins {
    `java-library`
    id("org.flywaydb.flyway") version "9.16.3"
}

flyway {
    url = "jdbc:postgresql://localhost:5432/federated_catalog"
    driver= "org.postgresql.Driver"
    user = "yourLocalBdUser"
    password = "yourLocalBdPassword"
}

dependencies {
    implementation("org.postgresql:postgresql:42.5.1")
    implementation ("org.flywaydb:flyway-core:9.12.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}