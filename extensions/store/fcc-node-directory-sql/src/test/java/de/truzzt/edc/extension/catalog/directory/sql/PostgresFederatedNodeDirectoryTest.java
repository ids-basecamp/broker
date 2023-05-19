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

package de.truzzt.edc.extension.catalog.directory.sql;

import de.truzzt.edc.extension.catalog.directory.sql.schema.BaseSqlDialectStatements;
import de.truzzt.edc.extension.catalog.directory.sql.schema.postgres.PostgresDialectStatements;
import de.truzzt.edc.extension.catalog.directory.test.TestUtil;
import de.truzzt.edc.extension.postgresql.migration.DatabaseMigrationManager;
import org.eclipse.edc.junit.annotations.PostgresqlDbIntegrationTest;
import org.eclipse.edc.policy.model.PolicyRegistrationTypes;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.sql.testfixtures.PostgresqlStoreSetupExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

@PostgresqlDbIntegrationTest
@ExtendWith(PostgresqlStoreSetupExtension.class)
class PostgresFederatedNodeDirectoryTest {

    private final BaseSqlDialectStatements sqlStatements = new PostgresDialectStatements();

    private SqlFederatedNodeDirectory federatedNodeDirectory;

    private DatabaseMigrationManager migrationManager;

    @BeforeEach
    void setUp(PostgresqlStoreSetupExtension setupExtension) {
        var typeManager = new TypeManager();
        typeManager.registerTypes(PolicyRegistrationTypes.TYPES.toArray(Class<?>[]::new));

        migrationManager = TestUtil.setupFlyway();
        migrationManager.migrateAllDataSources(false);

        federatedNodeDirectory = new SqlFederatedNodeDirectory(setupExtension.getDataSourceRegistry(), setupExtension.getDatasourceName(),
                setupExtension.getTransactionContext(), typeManager.getMapper(), sqlStatements);
    }

    @AfterEach
    void tearDown(PostgresqlStoreSetupExtension setupExtension) {
        migrationManager.cleanAllDataSources(false);
    }

}
