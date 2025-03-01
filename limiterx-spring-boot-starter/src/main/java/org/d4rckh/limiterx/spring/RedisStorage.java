package org.d4rckh.limiterx.spring;

import lombok.RequiredArgsConstructor;
import org.d4rckh.limiterx.core.common.ILimiterStorage;
import org.d4rckh.limiterx.core.domain.ClientStats;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

@RequiredArgsConstructor
public class RedisStorage implements ILimiterStorage {

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
