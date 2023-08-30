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

package de.truzzt.edc.extension.catalog.directory.sql.schema;

import de.truzzt.edc.extension.catalog.directory.sql.schema.postgres.FederatedCacheNodeMapping;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.sql.translation.SqlQueryStatement;

import static java.lang.String.format;

public class BaseSqlDialectStatements implements FederatedCacheNodeStatements {

    @Override
    public String getInsertTemplate() {
        return format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?%s)",
                getFederatedCacheNodeTable(),
                getNameColumn(),
                getTargetUrlColumn(),
                getSupportedProtocolsColumn(),
                getFormatAsJsonOperator()
        );
    }

    @Override
    public String getCountByNameTemplate() {
        return format("SELECT COUNT (*) FROM %s WHERE %s = ?",
                getFederatedCacheNodeTable(),
                getNameColumn());
    }

    @Override
    public String getFindByNameTemplate() {
        return format("SELECT * FROM %s WHERE %s = ?",
                getFederatedCacheNodeTable(),
                getNameColumn());
    }

    @Override
    public String getUpdateCrawlerExecutionTemplate() {
        return format("UPDATE %s SET %s = ?, %s = ?, %s = ? WHERE %s = ?",
                getFederatedCacheNodeTable(),
                getOnlineStatusColumn(),
                getLastCrawledColumn(),
                getContractOffersCountColumn(),
                getNameColumn());
    }

    @Override
    public String getDeleteByNameTemplate() {
        return format("DELETE FROM %s WHERE %s = ?",
                getFederatedCacheNodeTable(),
                getNameColumn());
    }

    @Override
    public SqlQueryStatement createQuery(QuerySpec querySpec) {
        return new SqlQueryStatement(getSelectStatement(), querySpec, new FederatedCacheNodeMapping(this));
    }

    protected String getSelectStatement() {
        return "SELECT * FROM " + getFederatedCacheNodeTable();
    }
}
