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

package org.eclipse.edc.catalog.store.sql.schema;

import org.eclipse.edc.catalog.store.sql.schema.postgres.ContractOfferMapping;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.sql.translation.SqlQueryStatement;

import java.time.ZonedDateTime;

import static java.lang.String.format;

public class BaseSqlDialectStatements implements ContractOfferStatements {
    @Override
    public String getDeleteByIdTemplate() {
        return format("DELETE FROM %s WHERE %s = ?",
                getContractOfferTable(),
                getIdColumn());
    }

    @Override
    public String getFindByTemplate() {
        return format("SELECT * FROM %s WHERE %s = ?", getContractOfferTable(), getIdColumn());
    }

    @Override
    public String getInsertTemplate() {
        return format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES (?, ?::json, ?, ?, ?, ?, ?, ?, ?)",
                getContractOfferTable(),
                getIdColumn(),
                getPolicyColumn(),
                getAssetIdColumn(),
                getUriProviderColumn(),
                getUriConsumerColumn(),
                getOfferStartColumn(),
                getOfferEndColumn(),
                getContractStartColumn(),
                getContractEndColumn()
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
    public String getUpdateTemplate() {
        return format("UPDATE %s SET %s = ?::json, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?",
                getContractOfferTable(),
                getPolicyColumn(),
                getAssetIdColumn(),
                getUriProviderColumn(),
                getUriConsumerColumn(),
                getOfferStartColumn(),
                getOfferEndColumn(),
                getContractStartColumn(),
                getContractEndColumn(),
                getIdColumn());
    }

    public String getUpdateOfferEndTemplate(){
        return format("UPDATE %s SET %s = ? WHERE %s IS NULL",
                getContractOfferTable(),
                getOfferEndColumn(),
                getOfferEndColumn());
    }

    public String getDeleteExpiredTemplate(){
        return format("DELETE FROM %s WHERE %s < ?",
                getContractOfferTable(),
                getOfferEndColumn());
    }

    @Override
    public SqlQueryStatement createQuery(QuerySpec querySpec) {
        var select = format("SELECT * FROM %s", getContractOfferTable());
        return new SqlQueryStatement(select, querySpec, new ContractOfferMapping(this));
    }

    protected String getSelectStatement() {
        return "SELECT * FROM " + getContractOfferTable();
    }
}
