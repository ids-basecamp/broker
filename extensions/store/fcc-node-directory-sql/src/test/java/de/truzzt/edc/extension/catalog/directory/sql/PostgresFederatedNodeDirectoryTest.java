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
import org.eclipse.edc.catalog.spi.FederatedCacheNode;
import org.eclipse.edc.junit.annotations.PostgresqlDbIntegrationTest;
import org.eclipse.edc.policy.model.PolicyRegistrationTypes;
import org.eclipse.edc.spi.persistence.EdcPersistenceException;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.sql.testfixtures.PostgresqlStoreSetupExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@PostgresqlDbIntegrationTest
@ExtendWith(PostgresqlStoreSetupExtension.class)
class PostgresFederatedNodeDirectoryTest {

    private SqlFederatedNodeDirectory federatedNodeDirectory;

    private DatabaseMigrationManager migrationManager;

    @BeforeEach
    void setUp(PostgresqlStoreSetupExtension setupExtension) {
        var typeManager = new TypeManager();
        typeManager.registerTypes(PolicyRegistrationTypes.TYPES.toArray(Class<?>[]::new));

        migrationManager = TestUtil.setupFlyway();
        migrationManager.migrateAllDataSources(false);

        federatedNodeDirectory = new SqlFederatedNodeDirectory(setupExtension.getDataSourceRegistry(),
                setupExtension.getDatasourceName(), setupExtension.getTransactionContext(), typeManager.getMapper(),
                new PostgresDialectStatements());
    }

    @AfterEach
    void tearDown(PostgresqlStoreSetupExtension setupExtension) {
        migrationManager.cleanAllDataSources(false);
    }

    @Test
    @DisplayName("Get all successful")
    void getAll_successful() {
        var testFederatedCacheNodes = createAndSaveFederatedCacheNodes(3);

        var federatedCacheNodes = federatedNodeDirectory.getAll().stream().toList();

        assertThat(federatedCacheNodes)
                .hasSize(3);
        assertThat(federatedCacheNodes.get(0).getName())
                .isEqualTo(testFederatedCacheNodes.get(0).getName());
        assertThat(federatedCacheNodes.get(1).getName())
                .isEqualTo(testFederatedCacheNodes.get(1).getName());
        assertThat(federatedCacheNodes.get(2).getName())
                .isEqualTo(testFederatedCacheNodes.get(2).getName());
    }

    @Test
    @DisplayName("Insert successful")
    void insert_successful() {
        var federatedCacheNode = TestUtil.createFederatedCacheNode(99);

        federatedNodeDirectory.insert(federatedCacheNode);
    }

    @Test
    @DisplayName("Insert duplicated Name error")
    void insert_duplicated_id_error() {
        var federatedCacheNode1 = TestUtil.createFederatedCacheNode(99);

        federatedNodeDirectory.insert(federatedCacheNode1);

        var federatedCacheNode2 = TestUtil.createFederatedCacheNode(99);

        assertThatThrownBy(() -> federatedNodeDirectory.insert(federatedCacheNode2))
                .isInstanceOf(EdcPersistenceException.class)
                .hasMessageStartingWith("Federated Cache Node with Name")
                .hasMessageEndingWith("already exists");
    }

    private List<FederatedCacheNode> createAndSaveFederatedCacheNodes(int amount) {

        return IntStream.range(0, amount).mapToObj(i -> {
            var federatedCacheNode = TestUtil.createFederatedCacheNode(i);
            federatedNodeDirectory.insert(federatedCacheNode);
            return federatedCacheNode;
        }).collect(Collectors.toList());
    }
}
