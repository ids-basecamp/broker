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

public interface FederatedCacheNodeDirectory extends org.eclipse.edc.catalog.spi.FederatedCacheNodeDirectory {

    void updateCrawlerExecution(FederatedCacheNode node);

    boolean delete(FederatedCacheNode node);

    FederatedCacheNode findByName(String name);
}
