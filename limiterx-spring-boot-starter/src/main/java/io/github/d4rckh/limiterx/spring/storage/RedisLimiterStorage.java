package io.github.d4rckh.limiterx.spring.storage;

import lombok.RequiredArgsConstructor;
import io.github.d4rckh.limiterx.core.common.LimiterStorage;
import io.github.d4rckh.limiterx.core.domain.ClientStats;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

@RequiredArgsConstructor
public class RedisLimiterStorage implements LimiterStorage {
    private final RedisTemplate<String, ClientStats> redisTemplate;

    @Override
    public Optional<ClientStats> findByKey(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    @Override
    public void updateByKey(String key, ClientStats stats) {
        redisTemplate.opsForValue().set(key, stats);
    }
}
