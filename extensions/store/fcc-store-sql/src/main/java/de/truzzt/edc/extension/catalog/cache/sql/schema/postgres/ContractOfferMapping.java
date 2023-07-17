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

package de.truzzt.edc.extension.catalog.cache.sql.schema.postgres;

import de.truzzt.edc.extension.catalog.cache.sql.schema.ContractOfferStatements;
import org.eclipse.edc.sql.translation.TranslationMapping;

public class ContractOfferMapping extends TranslationMapping {
    public ContractOfferMapping(ContractOfferStatements statements) {
        add("id", statements.getIdColumn());
        add("policy", statements.getPolicyColumn());
        add("asset", statements.getAssetColumn());
        add("uriProvider", statements.getUriProviderColumn());
        add("uriConsumer", statements.getUriConsumerColumn());
        add("offerStart", statements.getOfferStartColumn());
        add("offerEnd", statements.getOfferEndColumn());
        add("contractStart", statements.getContractStartColumn());
        add("contractEnd", statements.getContractEndColumn());
    }
}