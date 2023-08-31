/*
 *
 *
 */

package org.eclipse.edc.catalog.spi.directory;

import org.eclipse.edc.catalog.spi.FederatedCacheNode;

public interface FederatedCacheNodeDirectory extends org.eclipse.edc.catalog.spi.FederatedCacheNodeDirectory {

    void updateCrawlerExecution(FederatedCacheNode node);

    boolean delete(FederatedCacheNode node);

    FederatedCacheNode findByName(String name);
}
