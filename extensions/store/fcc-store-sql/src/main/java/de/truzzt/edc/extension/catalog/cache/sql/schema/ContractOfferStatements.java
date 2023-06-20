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

package de.truzzt.edc.extension.catalog.cache.sql.schema;

import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.sql.dialect.BaseSqlDialect;
import org.eclipse.edc.sql.translation.SqlQueryStatement;

/**
 * Defines all statements that are needed for the ContractOffer store
 */
public interface ContractOfferStatements {

    default String getContractOfferTable() {
        return "edc_contract_offer";
    }

    default String getIdColumn() {
        return "contract_offer_id";
    }

    default String getPolicyColumn() {
        return "policy";
    }

    default String getAssetIdColumn() {
        return "asset_id";
    }

    default String getUriProviderColumn() {
        return "uri_provider";
    }

    default String getUriConsumerColumn() {
        return "uri_consumer";
    }

    default String getOfferStartColumn() {
        return "offer_start";
    }

    default String getOfferEndColumn() {
        return "offer_end";
    }

    default String getContractStartColumn() {
        return "contract_start";
    }

    default String getContractEndColumn() {
        return "contract_end";
    }

    String getInsertTemplate();

    String getCountTemplate();

    String getUpdateOfferEndTemplate();

    String getDeleteExpiredTemplate();

    SqlQueryStatement createQuery(QuerySpec querySpec);

    default String getFormatAsJsonOperator() {
        return BaseSqlDialect.getJsonCastOperator();
    }

}
