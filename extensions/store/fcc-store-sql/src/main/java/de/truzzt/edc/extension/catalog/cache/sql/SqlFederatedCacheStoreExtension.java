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
 *       sovity GmbH - initial implementation
 *
 */

package de.truzzt.edc.extension.catalog.cache.sql;

import de.truzzt.edc.extension.catalog.cache.sql.schema.postgres.PostgresDialectStatements;
import org.eclipse.edc.catalog.spi.FederatedCacheStore;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provider;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.transaction.datasource.spi.DataSourceRegistry;
import org.eclipse.edc.transaction.spi.TransactionContext;

public class SqlFederatedCacheStoreExtension implements ServiceExtension {

    public static final String NAME = "SQL Federated Cache Store Extension";

    @Inject
    private DataSourceRegistry dataSourceRegistry;
    @Inject
    private TransactionContext transactionContext;
    @Inject
    private TypeManager typeManager;
    @Inject
    private AssetIndex assetIndex;

    @Override
    public String name() {
        return NAME;
    }

    @Provider
    public FederatedCacheStore sqlCacheStore() {
        return new SqlFederatedCacheStore(
                dataSourceRegistry,
                DataSourceRegistry.DEFAULT_DATASOURCE,
                transactionContext,
                typeManager.getMapper(),
                new PostgresDialectStatements(),
                assetIndex
        );
    }
}
