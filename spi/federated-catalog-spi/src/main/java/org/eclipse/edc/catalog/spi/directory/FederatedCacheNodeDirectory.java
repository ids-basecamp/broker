package org.eclipse.edc.catalog.spi.directory;

import org.eclipse.edc.catalog.spi.FederatedCacheNode;

/**
 * A global list of all FederatedCacheNodes that are available in a data space, much like a "phone book" for catalog endpoints.
 */
public interface FederatedCacheNodeDirectory extends org.eclipse.edc.catalog.spi.FederatedCacheNodeDirectory {

    boolean delete(FederatedCacheNode node);
}
