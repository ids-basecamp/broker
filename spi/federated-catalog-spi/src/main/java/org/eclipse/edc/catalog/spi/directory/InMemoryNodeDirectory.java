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
 *       Truzzt GmbH - Initial implementation
 *
 */

package org.eclipse.edc.catalog.spi.directory;

import org.eclipse.edc.catalog.spi.FederatedCacheNode;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryNodeDirectory implements FederatedCacheNodeDirectory {
    private final List<FederatedCacheNode> cache = new CopyOnWriteArrayList<>();

    @Override
    public List<FederatedCacheNode> getAll() {
        return List.copyOf(cache);
    }

    @Override
    public void insert(FederatedCacheNode node) {
        cache.add(node);
    }

    @Override
    public void updateCrawlerExecution(FederatedCacheNode node) {
        var existingNode = cache.stream()
                .filter(f -> f.getName().equals(node.getName()))
                .findFirst().get();
        cache.remove(existingNode);

        var updatedNode = new FederatedCacheNode(existingNode.getName(),
                existingNode.getTargetUrl(),
                existingNode.getSupportedProtocols(),
                node.getOnlineStatus(),
                node.getLastCrawled(),
                node.getContractOffersCount()
        );
        cache.add(updatedNode);
    }

    @Override
    public boolean delete(FederatedCacheNode node) {
        var existingNode = cache.stream()
                .filter(f -> f.getName().equals(node.getName()))
                .findFirst();

        if (existingNode.isEmpty()) {
            return false;
        }

        return cache.remove(existingNode.get());
    }

    @Override
    public FederatedCacheNode findByName(String name) {
        return cache.stream().filter(c -> c.getName().equals(name)).findFirst().orElse(null);
    }
}
