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

package org.eclipse.edc.catalog.test;

import de.truzzt.edc.extension.postgresql.migration.DatabaseMigrationManager;
import de.truzzt.edc.extension.postgresql.migration.FlywayService;
import org.eclipse.edc.catalog.spi.Catalog;
import org.eclipse.edc.catalog.spi.FederatedCacheNode;
import org.eclipse.edc.catalog.spi.WorkItem;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractOffer;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.monitor.ConsoleMonitor;
import org.eclipse.edc.spi.system.configuration.ConfigFactory;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TestUtil {

    public static final String TEST_PROTOCOL = "test-protocol";

    public static WorkItem createWorkItem() {
        return new WorkItem("test-url", "test-protocol");
    }

    public static DatabaseMigrationManager setupFlyway() {

        var entries = Map.of("edc.datasource.default.url", "jdbc:postgresql://localhost:5432/itest",
                "edc.datasource.default.user", "postgres",
                "edc.datasource.default.password", "password");
        var config = ConfigFactory.fromMap(entries);

        var flywayService = new FlywayService(new ConsoleMonitor(), false);
        return new DatabaseMigrationManager(config, flywayService);
    }

    @NotNull
    public static ContractOffer createOffer(String id) {
        var asset = Asset.Builder.newInstance().id(id).build();
        return createOffer(id, asset);
    }

    @NotNull
    public static ContractOffer createOffer(String id, Asset asset) {
        // TODO Change more policy default attributes
        var policy = Policy.Builder.newInstance().build();
        var now = ZonedDateTime.now(ZoneId.of("Z")).withNano(0);

        URI provider = null;
        URI consumer = null;
        try {
            provider = new URI("http://localhost/" + id + "/provider/");
            consumer = new URI("http://localhost/" + id + "/consumer/");
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid provider or consumer URI", e);
        }

        return ContractOffer.Builder.newInstance()
                .id(id)
                .policy(policy)
                .asset(asset)
                .provider(provider)
                .consumer(consumer)
                .contractStart(now)
                .contractEnd(now.plus(12, ChronoUnit.MONTHS))
                .build();
    }

    @NotNull
    public static Catalog createCatalog() {
        return Catalog.Builder.newInstance()
                .id("test-catalog")
                .contractOffers(List.of(createOffer("test-offer")))
                .build();
    }

    @NotNull
    public static FederatedCacheNode createNode() {
        return new FederatedCacheNode("testnode" + UUID.randomUUID(),
                "http://test.com",
                List.of(TEST_PROTOCOL)
        );
    }

    @NotNull
    public static Asset createAsset(String assetId) {
        return Asset.Builder.newInstance()
                .id(assetId)
                .name("test-asset")
                .version("0.0.1-test")
                .build();
    }

    @NotNull
    public static DataAddress createDataAddress(String type) {
        // TODO Change more data address default attributes
        return DataAddress.Builder.newInstance()
                .type(type)
                .build();
    }
}
