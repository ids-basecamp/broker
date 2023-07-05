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
 *       truzzt GmbH - PostgreSQL implementation
 *
 */

package de.truzzt.edc.extension.catalog.directory.test;

import de.truzzt.edc.extension.postgresql.migration.DatabaseMigrationManager;
import de.truzzt.edc.extension.postgresql.migration.FlywayService;
import org.eclipse.edc.catalog.spi.FederatedCacheNode;
import org.eclipse.edc.spi.monitor.ConsoleMonitor;
import org.eclipse.edc.spi.system.configuration.ConfigFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class TestUtil {

    public static DatabaseMigrationManager setupFlyway() {

        // TODO Find a shared place to store these configurations
        var entries = Map.of("edc.datasource.default.url", "jdbc:postgresql://localhost:5432/itest",
                "edc.datasource.default.user", "postgres",
                "edc.datasource.default.password", "password");
        var config = ConfigFactory.fromMap(entries);

        var flywayService = new FlywayService(new ConsoleMonitor(), false);
        return new DatabaseMigrationManager(config, flywayService);
    }

    @NotNull
    public static FederatedCacheNode createFederatedCacheNode(int id) {
        return new FederatedCacheNode(
                String.format("node-%d", id),
                String.format("https://localhost:9999/protocol/%d", id),
                List.of("https", "http")
        );
    }

}
