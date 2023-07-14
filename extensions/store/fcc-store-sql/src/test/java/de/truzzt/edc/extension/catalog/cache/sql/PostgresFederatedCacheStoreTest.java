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

package de.truzzt.edc.extension.catalog.cache.sql;

import de.truzzt.edc.extension.catalog.cache.sql.schema.BaseSqlDialectStatements;
import de.truzzt.edc.extension.catalog.cache.sql.schema.postgres.PostgresDialectStatements;
import de.truzzt.edc.extension.catalog.cache.test.TestUtil;
import de.truzzt.edc.extension.postgresql.migration.DatabaseMigrationManager;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractOffer;
import org.eclipse.edc.junit.annotations.PostgresqlDbIntegrationTest;
import org.eclipse.edc.policy.model.PolicyRegistrationTypes;
import org.eclipse.edc.spi.persistence.EdcPersistenceException;
import org.eclipse.edc.spi.query.Criterion;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.sql.testfixtures.PostgresqlStoreSetupExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@PostgresqlDbIntegrationTest
@ExtendWith(PostgresqlStoreSetupExtension.class)
class PostgresFederatedCacheStoreTest {

    private final BaseSqlDialectStatements sqlStatements = new PostgresDialectStatements();

    private SqlFederatedCacheStore federatedCacheStore;

    private DatabaseMigrationManager migrationManager;

    @BeforeEach
    void setUp(PostgresqlStoreSetupExtension setupExtension) {
        var typeManager = new TypeManager();
        typeManager.registerTypes(PolicyRegistrationTypes.TYPES.toArray(Class<?>[]::new));

        migrationManager = TestUtil.setupFlyway();
        migrationManager.migrateAllDataSources(false);

        federatedCacheStore = new SqlFederatedCacheStore(setupExtension.getDataSourceRegistry(), setupExtension.getDatasourceName(),
                setupExtension.getTransactionContext(), typeManager.getMapper(), sqlStatements);
    }

    @AfterEach
    void tearDown(PostgresqlStoreSetupExtension setupExtension) {
        migrationManager.cleanAllDataSources(false);
    }

    @Test
    @DisplayName("Save successful")
    void save_successful() {
        var contractOffer = createContractOffer(99);

        federatedCacheStore.save(contractOffer);
    }

    @Test
    @DisplayName("Save duplicated ID error")
    void save_duplicated_id_error() {
        var contractOffer1 = createContractOffer(99);

        federatedCacheStore.save(contractOffer1);

        var contractOffer2 = createContractOffer(99);

        assertThatThrownBy(() -> federatedCacheStore.save(contractOffer2))
                .isInstanceOf(EdcPersistenceException.class)
                .hasMessageStartingWith("Contract Offer with ID")
                .hasMessageEndingWith("already exists");
    }

    @Test
    @DisplayName("Expire all successful")
    void expireAll_successful() {
        createAndSaveContractOffers(10);

        federatedCacheStore.expireAll();

        List<Criterion> query = List.of();
        var contractOffers = federatedCacheStore.query(query);

        var contractOffersNotExpired = contractOffers.stream()
                .filter(contractOffer -> Objects.isNull(contractOffer.getOfferEnd()));

        assertThat(contractOffersNotExpired)
                .isNullOrEmpty();
    }

    @Test
    @DisplayName("Delete all expired all successful")
    void deleteAllExpired_successful() {
        createAndSaveContractOffers(10);

        federatedCacheStore.expireAll();

        TestUtil.sleep(1000);
        federatedCacheStore.deleteExpired();

        List<Criterion> query = List.of();
        var contractOffersExpired = federatedCacheStore.query(query);

        assertThat(contractOffersExpired)
                .isNullOrEmpty();
    }

    @Test
    @DisplayName("Query all Offers")
    void query_allOffers() {
        var testContractOffers = createAndSaveContractOffers(3);

        List<Criterion> query = List.of();
        var contractOffers = federatedCacheStore.query(query).stream().toList();

        assertThat(contractOffers)
                .hasSize(3);
        assertThat(contractOffers.get(0).getId())
                .isEqualTo(testContractOffers.get(0).getId());
        assertThat(contractOffers.get(1).getId())
                .isEqualTo(testContractOffers.get(1).getId());
        assertThat(contractOffers.get(2).getId())
                .isEqualTo(testContractOffers.get(2).getId());
    }

    @Test
    @DisplayName("Query Offer by Id")
    void query_filterById() {
        var testContractOffers = createAndSaveContractOffers(2);
        var contractOffer = testContractOffers.get(0);

        var query = List.of(new Criterion("id", "=", contractOffer.getId()));

        var contractOffers = federatedCacheStore.query(query);
        assertThat(contractOffers)
                .isNotEmpty()
                .hasSize(1);

        assertThat(testContractOffers.get(0).getId())
                .isEqualTo(contractOffer.getId());
    }

    @Test
    @DisplayName("Query not found")
    void query_notFound() {
        createAndSaveContractOffers(3);

        var query = List.of(new Criterion("id", "=", "xxx"));

        var contractOffers = federatedCacheStore.query(query);
        assertThat(contractOffers)
                .isEmpty();
    }

    private ContractOffer createContractOffer(int i) {

        var dataAddress = TestUtil.createDataAddress("test-address" + i);
        var asset = TestUtil.createAsset("test-asset" + i);

        return TestUtil.createOffer("test-offer" + i, asset);
    }

    private List<ContractOffer> createAndSaveContractOffers(int amount) {

        return IntStream.range(0, amount).mapToObj(i -> {
            var contractOffer = createContractOffer(i);
            federatedCacheStore.save(contractOffer);
            return contractOffer;
        }).collect(Collectors.toList());
    }
}
