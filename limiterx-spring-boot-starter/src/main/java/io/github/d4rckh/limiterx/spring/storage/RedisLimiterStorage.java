package io.github.d4rckh.limiterx.spring.storage;

import lombok.RequiredArgsConstructor;
import io.github.d4rckh.limiterx.core.common.LimiterStorage;
import io.github.d4rckh.limiterx.core.domain.ClientStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Redis-based implementation of {@link LimiterStorage} for managing rate-limiting data.
 * <p>
 * This storage backend leverages Redis to persist and retrieve {@link ClientStats}.
 * It supports TTL (time-to-live) management for automatic expiration of rate-limiting records.
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
public class RedisLimiterStorage implements LimiterStorage {
    private final RedisTemplate<String, ClientStats> redisTemplate;

    /**
     * Retrieves the rate-limiting statistics for a given key from Redis.
     *
     * @param key the key identifying the client stats
     * @return an {@link Optional} containing {@link ClientStats} if found, otherwise empty
     */
    @Override
    public Optional<ClientStats> findByKey(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    /**
     * Updates the rate-limiting statistics for a given key.
     * <p>
     * If the key already has a TTL, it preserves the existing TTL while updating the value.
     * Otherwise, the value is stored without an expiration.
     * </p>
     *
     * @param key   the key identifying the client stats
     * @param stats the updated client statistics
     */
    @Override
    public void updateByKey(String key, ClientStats stats) {
        Long existingTtl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (existingTtl != null && existingTtl > 0) {
            redisTemplate.opsForValue().set(key, stats, existingTtl, TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(key, stats); // No TTL, store without expiry
        }
    }

    /**
     * Retrieves the time-to-live (TTL) for a given key.
     *
     * @param key the key to check
     * @return the remaining TTL as a {@link Duration}, or zero if the key does not exist or has no expiry
     */
    @Override
    public Duration getTtlByKey(String key) {
        return Duration.ofSeconds(
            Optional.ofNullable(redisTemplate.getExpire(key, TimeUnit.SECONDS)).map(r -> r == -2 ? 0 : r).orElse(0L)
        );
    }

    /**
     * Sets a TTL (expiration time) for a given key.
     * <p>
     * If the key does not exist, a warning is logged and no action is taken.
     * </p>
     *
     * @param key the key to update
     * @param ttl the duration after which the key should expire
     */
    @Override
    public void setTtlByKey(String key, Duration ttl) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) //{
//            log.info("Setting expiry for key {} to {}s", key, ttl.getSeconds());
            redisTemplate.expire(key, ttl.getSeconds(), TimeUnit.SECONDS);
//        } else {
//            log.warn("Attempted to set expiry for non-existent key: {}", key);
//        }
    }
}
