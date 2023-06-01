package de.truzzt.edc.extension.catalog.directory.sql.ext;

import org.eclipse.edc.catalog.spi.FederatedCacheNode;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryNodeDirectory implements FederatedCacheNodeDirectoryExt {
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
    public boolean delete(FederatedCacheNode node) {
        var existingNode = cache.stream()
                .filter(f -> f.getTargetUrl().equals(node.getTargetUrl()))
                .findFirst();

        if (existingNode.isEmpty()) {
            return false;
        }

        return cache.remove(existingNode.get());
    }
}
