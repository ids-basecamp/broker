package de.truzzt.edc.extension.catalog.directory.sql.ext;

import org.eclipse.edc.catalog.spi.FederatedCacheNode;
import org.eclipse.edc.catalog.spi.FederatedCacheNodeDirectory;

public interface FederatedCacheNodeDirectoryExt extends FederatedCacheNodeDirectory {

    boolean delete(FederatedCacheNode node);
}
