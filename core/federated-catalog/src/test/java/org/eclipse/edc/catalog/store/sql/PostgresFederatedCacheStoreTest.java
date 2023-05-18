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

package org.eclipse.edc.catalog.store.sql;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.truzzt.edc.extension.postgresql.migration.DatabaseMigrationManager;
import org.eclipse.edc.catalog.store.sql.schema.BaseSqlDialectStatements;
import org.eclipse.edc.catalog.store.sql.schema.postgres.PostgresDialectStatements;
import org.eclipse.edc.catalog.test.TestUtil;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractOffer;
import org.eclipse.edc.connector.defaults.storage.assetindex.InMemoryAssetIndex;
import org.eclipse.edc.junit.annotations.PostgresqlDbIntegrationTest;
import org.eclipse.edc.policy.model.PolicyRegistrationTypes;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.query.Criterion;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.spi.types.domain.asset.AssetEntry;
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

@PostgresqlDbIntegrationTest
@ExtendWith(PostgresqlStoreSetupExtension.class)
class PostgresFederatedCacheStoreTest {

    private final BaseSqlDialectStatements sqlStatements = new PostgresDialectStatements();

    private final AssetIndex assetIndex = new InMemoryAssetIndex();

    private SqlFederatedCacheStore federatedCacheStore;

    private DatabaseMigrationManager migrationManager;

    @BeforeEach
    void setUp(PostgresqlStoreSetupExtension setupExtension) {
        var typeManager = new TypeManager();
        typeManager.registerTypes(PolicyRegistrationTypes.TYPES.toArray(Class<?>[]::new));

        migrationManager = TestUtil.setupFlyway();
        migrationManager.migrateAllDataSources(false);

        ObjectMapper objectMapper = new ObjectMapper();

        federatedCacheStore = new SqlFederatedCacheStore(setupExtension.getDataSourceRegistry(), setupExtension.getDatasourceName(),
                setupExtension.getTransactionContext(), objectMapper, sqlStatements, assetIndex);
    }

    @AfterEach
    void tearDown(PostgresqlStoreSetupExtension setupExtension) {
        migrationManager.cleanAllDataSources(false);
    }

    @Test
    @DisplayName("Save successful")
    void save_successful() {
        ContractOffer contractOffer = createContractOffer(99);

        federatedCacheStore.save(contractOffer);
    }

    @Test
    @DisplayName("Expire all successful")
    void expire_all_successful() {
        List<ContractOffer> allContractOffers = createAndSaveContractOffers(10);

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
    void delete_all_expired_successful(){

        List<ContractOffer> allContractOffers = createAndSaveContractOffers(10);

        federatedCacheStore.expireAll();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        federatedCacheStore.deleteExpired();
        List<Criterion> query = List.of();

        var contractOffersExpired = federatedCacheStore.query(query);

        assertThat(contractOffersExpired)
                .isNullOrEmpty();


    }

    @Test
    @DisplayName("Query all Offers")
    void query_allOffers() {
        List<ContractOffer> allContractOffers = createAndSaveContractOffers(3);
        List<Criterion> query = List.of();

        var contractOffers = federatedCacheStore.query(query);
        assertThat(contractOffers)
                .isNotEmpty()
                .hasSize(3);
        // TODO Implement other Asserts, comparing the result contents
    }

    @Test
    @DisplayName("Query Offer by Id")
    void query_filterById() {
        List<ContractOffer> allContractOffers = createAndSaveContractOffers(2);
        ContractOffer contractOffer = allContractOffers.get(0);

        List<Criterion> query = List.of(new Criterion("id", "=", contractOffer.getId()));

        var contractOffers = federatedCacheStore.query(query);
        assertThat(contractOffers)
                .isNotEmpty()
                .hasSize(1);
        // TODO Implement other Asserts, comparing the result contents
    }

    @Test
    @DisplayName("Query not found")
    void query_notFound() {
        createAndSaveContractOffers(3);

        List<Criterion> query = List.of(new Criterion("id", "=", "xxx"));

        var contractOffers = federatedCacheStore.query(query);
        assertThat(contractOffers)
                .isEmpty();
    }

    private ContractOffer createContractOffer(int i) {

        var dataAddress = TestUtil.createDataAddress("test-address" + i);
        var asset = TestUtil.createAsset("test-asset" + i);
        assetIndex.accept(new AssetEntry(asset, dataAddress));

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
