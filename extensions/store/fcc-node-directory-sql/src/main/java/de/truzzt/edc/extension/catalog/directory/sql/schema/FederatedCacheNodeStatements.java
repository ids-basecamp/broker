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

import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.sql.dialect.BaseSqlDialect;
import org.eclipse.edc.sql.translation.SqlQueryStatement;

public interface FederatedCacheNodeStatements {

    default String getFederatedCacheNodeTable() {
        return "edc_federated_cache_node";
    }

    default String getNameColumn() {
        return "name";
    }

    default String getTargetUrlColumn() {
        return "target_url";
    }

    default String getSupportedProtocolsColumn() {
        return "supported_protocols";
    }

    default String getOnlineStatusColumn() {
        return "online_status";
    }

    default String getLastCrawledColumn() {
        return "last_crawled";
    }

    default String getContractOffersCount() {
        return "contract_offers_count";
    }

    String getInsertTemplate();

    String getCountByNameTemplate();

    String getUpdateCrawlerExecutionTemplate();

    String getDeleteByNameTemplate();

    SqlQueryStatement createQuery(QuerySpec querySpec);

    default String getFormatAsJsonOperator() {
        return BaseSqlDialect.getJsonCastOperator();
    }
}
