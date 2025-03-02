package io.github.d4rckh.limiterx.spring.storage;

import lombok.RequiredArgsConstructor;
import io.github.d4rckh.limiterx.core.common.LimiterStorage;
import io.github.d4rckh.limiterx.core.domain.ClientStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class RedisLimiterStorage implements LimiterStorage {
    private final RedisTemplate<String, ClientStats> redisTemplate;

    @Override
    public Optional<ClientStats> findByKey(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    @Override
    public void updateByKey(String key, ClientStats stats) {
        Long existingTtl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (existingTtl != null && existingTtl > 0) {
            redisTemplate.opsForValue().set(key, stats, existingTtl, TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(key, stats); // No TTL, store without expiry
        }
    }

    @Override
    public Duration getTtlByKey(String key) {
        return Duration.ofSeconds(
            Optional.ofNullable(redisTemplate.getExpire(key, TimeUnit.SECONDS)).orElse(0L)
        );
    }

    @Override
    public void setTtlByKey(String key, Duration ttl) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            log.info("Setting expiry for key {} to {}s", key, ttl.getSeconds());
            redisTemplate.expire(key, ttl.getSeconds(), TimeUnit.SECONDS);
        } else {
            log.warn("Attempted to set expiry for non-existent key: {}", key);
        }
    }
}

