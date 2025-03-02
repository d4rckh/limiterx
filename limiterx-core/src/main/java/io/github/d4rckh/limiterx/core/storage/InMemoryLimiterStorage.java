package io.github.d4rckh.limiterx.core.storage;

import io.github.d4rckh.limiterx.core.common.LimiterStorage;
import io.github.d4rckh.limiterx.core.domain.ClientStats;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RequiredArgsConstructor
public class InMemoryLimiterStorage implements LimiterStorage {
    private final ConcurrentMap<String, ClientStats> storage = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Instant> ttlMap = new ConcurrentHashMap<>();

    @Override
    public Optional<ClientStats> findByKey(String key) {
        cleanupExpiredKeys();
        if (isExpired(key)) {
            storage.remove(key);
            ttlMap.remove(key);
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(key));
    }

    @Override
    public void updateByKey(String key, ClientStats stats) {
        storage.put(key, stats);
    }

    @Override
    public Duration getTtlByKey(String key) {
        cleanupExpiredKeys();
        return Optional.ofNullable(ttlMap.get(key))
            .map(expiry -> Duration.between(Instant.now(), expiry))
            .orElse(Duration.ZERO);
    }

    @Override
    public void setTtlByKey(String key, Duration ttl) {
        ttlMap.put(key, Instant.now().plus(ttl));
    }

    private boolean isExpired(String key) {
        Instant expiry = ttlMap.get(key);
        return expiry != null && expiry.isBefore(Instant.now());
    }

    private void cleanupExpiredKeys() {
        Instant now = Instant.now();
        ttlMap.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
        storage.entrySet().removeIf(entry -> isExpired(entry.getKey()));
    }
}
