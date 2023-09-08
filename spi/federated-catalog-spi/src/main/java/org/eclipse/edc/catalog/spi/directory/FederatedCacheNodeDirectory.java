/*
 *  Copyright (c) 2023 Truzzt GmbH
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
import org.eclipse.edc.spi.persistence.EdcPersistenceException;

public interface FederatedCacheNodeDirectory extends org.eclipse.edc.catalog.spi.FederatedCacheNodeDirectory {

    String nodeAlreadyExistsMessage = "Federated Cache Node with Name %s already exists";
    String nodeNoExistsMessage = "Federated Cache Node with Name %s doesn't exists";

    void updateCrawlerExecution(FederatedCacheNode node);

    boolean delete(FederatedCacheNode node);

    FederatedCacheNode findByName(String name);

    default void throwAlreadyExistsException(FederatedCacheNode federatedCacheNode) {
        throw new EdcPersistenceException(String.format(nodeAlreadyExistsMessage, federatedCacheNode.getName()));
    }

    default void throwNoExistsException(FederatedCacheNode federatedCacheNode) {
        throw new EdcPersistenceException(String.format(nodeNoExistsMessage, federatedCacheNode.getName()));
    }

}
