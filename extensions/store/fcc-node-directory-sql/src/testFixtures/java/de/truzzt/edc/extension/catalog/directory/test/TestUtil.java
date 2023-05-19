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
 *
 */

package de.truzzt.edc.extension.catalog.directory.test;

import de.truzzt.edc.extension.postgresql.migration.DatabaseMigrationManager;
import de.truzzt.edc.extension.postgresql.migration.FlywayService;
import org.eclipse.edc.spi.monitor.ConsoleMonitor;
import org.eclipse.edc.spi.system.configuration.ConfigFactory;

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

}
