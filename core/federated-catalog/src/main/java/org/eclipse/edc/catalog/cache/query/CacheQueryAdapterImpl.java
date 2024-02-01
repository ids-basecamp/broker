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
 *       Microsoft Corporation - initial API and implementation
 *
 */

package org.eclipse.edc.catalog.cache.query;

import org.eclipse.edc.catalog.spi.CacheQueryAdapter;
import org.eclipse.edc.catalog.spi.FederatedCacheNode;
import org.eclipse.edc.catalog.spi.FederatedCacheNodeDirectory;
import org.eclipse.edc.catalog.spi.FederatedCacheStore;
import org.eclipse.edc.catalog.spi.model.FederatedCatalogCacheQuery;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractOffer;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class CacheQueryAdapterImpl implements CacheQueryAdapter {

    private FederatedCacheStore store;

    private FederatedCacheNodeDirectory directory;

    public CacheQueryAdapterImpl(FederatedCacheStore store, FederatedCacheNodeDirectory directory) {
        this.store = store;
        this.directory = directory;
    }

    public CacheQueryAdapterImpl(FederatedCacheStore store) {
        this.store = store;
    }

    public CacheQueryAdapterImpl(FederatedCacheNodeDirectory directory) {
        this.directory = directory;
    }
    @Override
    public @NotNull Stream<ContractOffer> executeQuery(FederatedCatalogCacheQuery query) {
        //todo: translate the generic CacheQuery into a list of criteria and
        return store.query(query.getCriteria()).stream();
    }

    @Override
    public @NotNull Stream<FederatedCacheNode> executeConnectorQuery(FederatedCatalogCacheQuery query) {
        return directory.getAll().stream();
    }

    @Override
    public boolean canExecute(FederatedCatalogCacheQuery query) {
        return true; //todo: implement this when the CacheQuery is implemented
    }
}
