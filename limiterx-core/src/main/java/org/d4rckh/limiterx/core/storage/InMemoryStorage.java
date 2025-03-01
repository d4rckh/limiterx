package org.d4rckh.limiterx.core.storage;

import lombok.RequiredArgsConstructor;
import org.d4rckh.limiterx.core.common.ILimiterStorage;
import org.d4rckh.limiterx.core.domain.ClientStats;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RequiredArgsConstructor
public class InMemoryStorage implements ILimiterStorage {
    private final ConcurrentMap<String, ClientStats> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<ClientStats> findByKey(String key) {
        return Optional.ofNullable(storage.get(key));
    }

    @Override
    public void updateByKey(String key, ClientStats stats) {
        storage.put(key, stats);
    }
}