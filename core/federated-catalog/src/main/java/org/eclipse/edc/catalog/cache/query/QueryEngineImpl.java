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
 *
 */

package org.eclipse.edc.catalog.cache.query;

import org.eclipse.edc.catalog.spi.CacheQueryAdapterRegistry;
import org.eclipse.edc.catalog.spi.QueryConnectorResponse;
import org.eclipse.edc.catalog.spi.QueryEngine;
import org.eclipse.edc.catalog.spi.QueryResponse;
import org.eclipse.edc.catalog.spi.model.FederatedCatalogCacheQuery;

public class QueryEngineImpl implements QueryEngine {

    private final CacheQueryAdapterRegistry cacheQueryAdapterRegistry;

    public QueryEngineImpl(CacheQueryAdapterRegistry cacheQueryAdapterRegistry) {
        this.cacheQueryAdapterRegistry = cacheQueryAdapterRegistry;
    }

    @Override
    public QueryResponse getCatalog(FederatedCatalogCacheQuery query) {
        return cacheQueryAdapterRegistry.executeQuery(query);
    }

    @Override
    public QueryConnectorResponse getConnectors(FederatedCatalogCacheQuery query) {
        return cacheQueryAdapterRegistry.executeConnectorQuery(query);
    }
}
