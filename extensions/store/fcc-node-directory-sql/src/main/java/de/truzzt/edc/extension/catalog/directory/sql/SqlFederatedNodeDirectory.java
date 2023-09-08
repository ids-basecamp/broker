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
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.eclipse.edc.sql.SqlQueryExecutor.executeQuery;

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

        return transactionContext.execute(() -> {
            try {
                // TODO Implement a definite solution for the query limit. Temporary fix using 5000 as limit.
                var statement = statements.createQuery(QuerySpec.Builder.newInstance().limit(5000).build());
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
                    throwAlreadyExistsException(federatedCacheNode);
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
    public void updateCrawlerExecution(FederatedCacheNode federatedCacheNode) {
        Objects.requireNonNull(federatedCacheNode);
        Objects.requireNonNull(federatedCacheNode.getName());
        Objects.requireNonNull(federatedCacheNode.getOnlineStatus());
        Objects.requireNonNull(federatedCacheNode.getLastCrawled());
        Objects.requireNonNull(federatedCacheNode.getContractOffersCount());

        transactionContext.execute(() -> {
            try (var connection = getConnection()) {
                if (!existsByName(connection, federatedCacheNode.getName())) {
                    throwNoExistsException(federatedCacheNode);
                }

                executeQuery(connection, statements.getUpdateCrawlerExecutionTemplate(),
                        federatedCacheNode.getOnlineStatus(),
                        mapFromZonedDateTime(federatedCacheNode.getLastCrawled()),
                        federatedCacheNode.getContractOffersCount(),
                        federatedCacheNode.getName()
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

        return transactionContext.execute(() -> {
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
    }

    @Override
    public FederatedCacheNode findByName(String name) {
        Objects.requireNonNull(name);
        FederatedCacheNode fcn = null;
        var sql = statements.getFindByNameTemplate();
        try (var connection = getConnection()) {
            if (existsByName(connection, name)) {
                var stream = executeQuery(connection, false, this::mapResultSet, sql, name);
                fcn = stream.findFirst().get();
            }
        } catch (Exception e) {
            throw new EdcPersistenceException(e.getMessage(), e);
        }
        return fcn;
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
                supportedProtocols,
                resultSet.getBoolean(statements.getOnlineStatusColumn()),
                mapToZonedDateTime(resultSet, statements.getLastCrawledColumn()),
                resultSet.getInt(statements.getContractOffersCountColumn()));
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

    private Long mapFromZonedDateTime(ZonedDateTime zonedDateTime) {
        return zonedDateTime != null ?
                zonedDateTime.toEpochSecond() :
                null;
    }

    private ZonedDateTime mapToZonedDateTime(ResultSet resultSet, String column) throws Exception {
        return resultSet.getString(column) != null ?
                Instant.ofEpochSecond(resultSet.getLong(column)).atZone(ZoneId.of("Z")) :
                null;
    }
}
