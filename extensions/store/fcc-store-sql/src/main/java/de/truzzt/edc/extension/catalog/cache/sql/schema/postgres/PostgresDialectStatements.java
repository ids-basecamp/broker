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
 *       truzzt GmbH - psql implementation
 */

package de.truzzt.edc.extension.catalog.cache.sql.schema.postgres;

import de.truzzt.edc.extension.catalog.cache.sql.schema.BaseSqlDialectStatements;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.sql.dialect.PostgresDialect;
import org.eclipse.edc.sql.translation.SqlQueryStatement;

/**
 * Contains Postgres-specific SQL statements
 */
public class PostgresDialectStatements extends BaseSqlDialectStatements {
    @Override
    public String getFormatAsJsonOperator() {
        return PostgresDialect.getJsonCastOperator();
    }

    @Override
    public SqlQueryStatement createQuery(QuerySpec querySpec) {
        if (querySpec.containsAnyLeftOperand("selectorExpression.criteria")) {
            return new SqlQueryStatement(getSelectStatement(), querySpec, new ContractOfferMapping(this));
        }
        return super.createQuery(querySpec);
    }
}
