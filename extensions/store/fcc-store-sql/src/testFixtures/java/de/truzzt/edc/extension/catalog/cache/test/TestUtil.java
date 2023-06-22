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

package de.truzzt.edc.extension.catalog.cache.test;

import de.truzzt.edc.extension.postgresql.migration.DatabaseMigrationManager;
import de.truzzt.edc.extension.postgresql.migration.FlywayService;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractOffer;
import org.eclipse.edc.policy.model.Action;
import org.eclipse.edc.policy.model.AtomicConstraint;
import org.eclipse.edc.policy.model.LiteralExpression;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.policy.model.Permission;
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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

public class TestUtil {

    public static DatabaseMigrationManager setupFlyway() {

        var entries = Map.of("edc.datasource.default.url", "jdbc:postgresql://localhost:5432/itest",
                "edc.datasource.default.user", "postgres",
                "edc.datasource.default.password", "password");
        var config = ConfigFactory.fromMap(entries);

        var flywayService = new FlywayService(new ConsoleMonitor(), false);
        return new DatabaseMigrationManager(config, flywayService);
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static ContractOffer createOffer(String id, Asset asset) {
        var now = ZonedDateTime.now(ZoneId.of("Z")).withNano(0);

        var action = Action.Builder.newInstance()
                .type("USE")
                .build();
        var constraint = AtomicConstraint.Builder.newInstance()
                .operator(Operator.GT)
                .leftExpression(new LiteralExpression("POLICY_EVALUATION_TIME"))
                .rightExpression(new LiteralExpression(now.format(DateTimeFormatter.ISO_DATE_TIME)))
                .build();
        var permission = Permission.Builder.newInstance()
                .uid(UUID.randomUUID().toString())
                .target("test-target")
                .action(action)
                .constraint(constraint)
                .build();
        var policy = Policy.Builder.newInstance()
                .permission(permission)
                .build();

        URI provider;
        URI consumer;
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
    public static Asset createAsset(String assetId) {
        return Asset.Builder.newInstance()
                .id(assetId)
                .name("test-asset")
                .version("0.0.1-test")
                .build();
    }

    @NotNull
    public static DataAddress createDataAddress(String type) {
        return DataAddress.Builder.newInstance()
                .type(type)
                .property("key1", "value1")
                .property("key2", "value2")
                .build();
    }
}
