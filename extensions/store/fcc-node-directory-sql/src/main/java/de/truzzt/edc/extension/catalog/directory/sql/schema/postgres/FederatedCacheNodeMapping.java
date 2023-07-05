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

package de.truzzt.edc.extension.catalog.directory.sql.schema.postgres;

import de.truzzt.edc.extension.catalog.directory.sql.schema.FederatedCacheNodeStatements;
import org.eclipse.edc.sql.translation.TranslationMapping;

public class FederatedCacheNodeMapping extends TranslationMapping {
    public FederatedCacheNodeMapping(FederatedCacheNodeStatements statements) {
        add("name", statements.getNameColumn());
        add("targetUrl", statements.getTargetUrlColumn());
        add("supportedProtocols", statements.getSupportedProtocolsColumn());
    }
}