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

package de.truzzt.edc.extension.catalog.directory.sql;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.truzzt.edc.extension.catalog.directory.sql.schema.FederatedCacheNodeStatements;
import org.eclipse.edc.catalog.spi.FederatedCacheNode;
import org.eclipse.edc.catalog.spi.FederatedCacheNodeDirectory;
import org.eclipse.edc.sql.store.AbstractSqlStore;
import org.eclipse.edc.transaction.datasource.spi.DataSourceRegistry;
import org.eclipse.edc.transaction.spi.TransactionContext;

import java.util.List;

/**
 * An ephemeral SQL cache node directory.
 */
public class SqlFederatedNodeDirectory extends AbstractSqlStore implements FederatedCacheNodeDirectory {

    private final FederatedCacheNodeStatements statements;

    public SqlFederatedNodeDirectory(DataSourceRegistry dataSourceRegistry,
                                     String dataSourceName,
                                     TransactionContext transactionContext,
                                     ObjectMapper objectMapper,
                                     FederatedCacheNodeStatements statements) {
        super(dataSourceRegistry, dataSourceName, transactionContext, objectMapper);
        this.statements = statements;
    }

    @Override
    public List<FederatedCacheNode> getAll() {
        return null;
    }

    @Override
    public void insert(FederatedCacheNode node) {

    }
}
