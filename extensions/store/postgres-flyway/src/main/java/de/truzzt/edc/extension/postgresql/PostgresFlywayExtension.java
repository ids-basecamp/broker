/*
 *  Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - Initial implementation
*        truzzt GmbH - PostgreSQL implementation
 *
 */

package de.truzzt.edc.extension.postgresql;

import de.truzzt.edc.extension.postgresql.migration.DatabaseMigrationManager;
import de.truzzt.edc.extension.postgresql.migration.FlywayService;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

public class PostgresFlywayExtension implements ServiceExtension {

    @Setting
    public static final String EDC_DATASOURCE_REPAIR_SETTING = "edc.flyway.repair";

    @Override
    public String name() {
        return "Postgres Flyway Extension";
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var tryRepairOnFailedMigration = context.getSetting(EDC_DATASOURCE_REPAIR_SETTING, false);
        var flywayService = new FlywayService(context.getMonitor(), tryRepairOnFailedMigration);
        var migrationManager = new DatabaseMigrationManager(context.getConfig(), flywayService);
        migrationManager.migrateAllDataSources(true);
    }

}
