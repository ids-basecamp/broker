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
 *       truzzt GmbH - PostgreSQL implementation
 *
 */

package de.truzzt.edc.extension.catalog.directory.sql;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.truzzt.edc.extension.catalog.directory.sql.schema.FederatedCacheNodeStatements;
import org.eclipse.edc.catalog.spi.FederatedCacheNode;
import org.eclipse.edc.catalog.spi.directory.FederatedCacheNodeDirectory;
import org.eclipse.edc.spi.persistence.EdcPersistenceException;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.sql.store.AbstractSqlStore;
import org.eclipse.edc.transaction.datasource.spi.DataSourceRegistry;
import org.eclipse.edc.transaction.spi.TransactionContext;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.eclipse.edc.sql.SqlQueryExecutor.executeQuery;

public class SqlFederatedNodeDirectory extends AbstractSqlStore implements FederatedCacheNodeDirectory {

    private final FederatedCacheNodeStatements statements;

    private final String federatedCacheNodeExistsMessage = "Federated Cache Node with Name %s already exists";


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

        return transactionContext.execute(() -> {
            try {
                var statement = statements.createQuery(QuerySpec.Builder.newInstance().build());
                return executeQuery(getConnection(), true, this::mapResultSet, statement.getQueryAsString(), statement.getParameters())
                        .collect(Collectors.toList());
            } catch (SQLException e) {
                throw new EdcPersistenceException(e);
            }
        });
    }

    @Override
    public void insert(FederatedCacheNode federatedCacheNode) {
        Objects.requireNonNull(federatedCacheNode);
        Objects.requireNonNull(federatedCacheNode.getName());
        Objects.requireNonNull(federatedCacheNode.getTargetUrl());
        Objects.requireNonNull(federatedCacheNode.getSupportedProtocols());

        transactionContext.execute(() -> {
            try (var connection = getConnection()) {
                if (existsByName(connection, federatedCacheNode.getName())) {
                    throw new EdcPersistenceException(String.format(federatedCacheNodeExistsMessage, federatedCacheNode.getName()));
                }

                executeQuery(connection, statements.getInsertTemplate(),
                        federatedCacheNode.getName(),
                        federatedCacheNode.getTargetUrl(),
                        toJson(federatedCacheNode.getSupportedProtocols())
                );

            } catch (Exception e) {
                throw new EdcPersistenceException(e.getMessage(), e);
            }
        });
    }

    @Override
    public boolean delete(FederatedCacheNode federatedCacheNode) {
        Objects.requireNonNull(federatedCacheNode);
        Objects.requireNonNull(federatedCacheNode.getName());

        var deleted = transactionContext.execute(() -> {
            try (var connection = getConnection()) {
                if (!existsByName(connection, federatedCacheNode.getName())) {
                    return false;
                }

                executeQuery(connection, statements.getDeleteByNameTemplate(),
                        federatedCacheNode.getName());
                return true;
            } catch (Exception e) {
                throw new EdcPersistenceException(e.getMessage(), e);
            }
        });
        return deleted;
    }

    private FederatedCacheNode mapResultSet(ResultSet resultSet) throws Exception {

        List<String> supportedProtocols;
        try {
            supportedProtocols = fromJson(resultSet.getString(statements.getSupportedProtocolsColumn()), List.class);
        } catch (EdcPersistenceException e) {
            throw new EdcPersistenceException("Error parsing Supported Protocols JSON column", e);
        }

        return new FederatedCacheNode(
                resultSet.getString(statements.getNameColumn()),
                resultSet.getString(statements.getTargetUrlColumn()),
                supportedProtocols);
    }

    private boolean existsByName(Connection connection, String name) {
        var sql = statements.getCountByNameTemplate();
        try (var stream = executeQuery(connection, false, this::mapCount, sql, name)) {
            return stream.findFirst().orElse(0L) > 0;
        }
    }

    private long mapCount(ResultSet resultSet) throws SQLException {
        return resultSet.getLong(1);
    }
}
