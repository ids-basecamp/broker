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

package de.truzzt.edc.extension.catalog.cache.sql.schema;

import de.truzzt.edc.extension.catalog.cache.sql.schema.postgres.ContractOfferMapping;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.sql.translation.SqlQueryStatement;

import static java.lang.String.format;

public class BaseSqlDialectStatements implements ContractOfferStatements {

    @Override
    public String getInsertTemplate() {
        return format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES (?, ?%s, ?%s, ?, ?, ?, ?, ?, ?)",
                getContractOfferTable(),
                getIdColumn(),
                getPolicyColumn(),
                getAssetColumn(),
                getUriProviderColumn(),
                getUriConsumerColumn(),
                getOfferStartColumn(),
                getOfferEndColumn(),
                getContractStartColumn(),
                getContractEndColumn(),
                getFormatAsJsonOperator(),
                getFormatAsJsonOperator()
        );
    }

    @Override
    public String getCountTemplate() {
        return format("SELECT COUNT (%s) FROM %s WHERE %s = ?",
                getIdColumn(),
                getContractOfferTable(),
                getIdColumn());
    }

    @Override
    public String getUpdateOfferEndTemplate() {
        return format("UPDATE %s SET %s = ? WHERE %s IS NULL",
                getContractOfferTable(),
                getOfferEndColumn(),
                getOfferEndColumn());
    }

    @Override
    public String getDeleteExpiredTemplate() {
        return format("DELETE FROM %s WHERE %s < ?",
                getContractOfferTable(),
                getOfferEndColumn());
    }

    @Override
    public SqlQueryStatement createQuery(QuerySpec querySpec) {
        return new SqlQueryStatement(getSelectStatement(), querySpec, new ContractOfferMapping(this));
    }

    protected String getSelectStatement() {
        return "SELECT * FROM " + getContractOfferTable();
    }
}
