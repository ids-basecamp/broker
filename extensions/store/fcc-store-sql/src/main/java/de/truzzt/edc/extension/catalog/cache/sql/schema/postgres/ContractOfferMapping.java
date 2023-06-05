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

import de.truzzt.edc.extension.catalog.cache.sql.schema.ContractOfferStatements;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractOffer;
import org.eclipse.edc.sql.translation.TranslationMapping;

/**
 * Maps fields of a {@link ContractOffer} onto the
 * corresponding SQL schema (= column names)
 */
public class ContractOfferMapping extends TranslationMapping {
    public ContractOfferMapping(ContractOfferStatements statements) {
        add("id", statements.getIdColumn());
        add("policy", statements.getPolicyColumn());
        add("asset", statements.getAssetIdColumn());
        add("uriProvider", statements.getUriProviderColumn());
        add("uriConsumer", statements.getUriConsumerColumn());
        add("offerStart", statements.getOfferStartColumn());
        add("offerEnd", statements.getOfferEndColumn());
        add("contractStart", statements.getContractStartColumn());
        add("contractEnd", statements.getContractEndColumn());
        add("selectorExpression", new SelectorExpressionMapping());
    }
}