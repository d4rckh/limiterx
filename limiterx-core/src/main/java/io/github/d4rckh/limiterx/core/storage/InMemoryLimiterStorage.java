package io.github.d4rckh.limiterx.core.storage;

import io.github.d4rckh.limiterx.core.common.LimiterStorage;
import lombok.RequiredArgsConstructor;
import io.github.d4rckh.limiterx.core.domain.ClientStats;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RequiredArgsConstructor
public class InMemoryLimiterStorage implements LimiterStorage {
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