package io.github.d4rckh.limiterx.spring.storage;

import io.github.d4rckh.limiterx.core.domain.ClientStats;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedisLimiterStorageTest {

    @Mock
    private RedisTemplate<String, ClientStats> redisTemplate;

    @Mock
    private ValueOperations<String, ClientStats> valueOperations;

    @InjectMocks
    private RedisLimiterStorage redisLimiterStorage;

    private final String testKey = "testKey";
    private final ClientStats testStats = new ClientStats();

    @Test
    void findByKey_shouldReturnClientStats_whenKeyExists() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(testKey)).thenReturn(testStats);

        Optional<ClientStats> result = redisLimiterStorage.findByKey(testKey);

        assertTrue(result.isPresent());
        assertEquals(testStats, result.get());
        verify(valueOperations, times(1)).get(testKey);
    }

    @Test
    void findByKey_shouldReturnEmpty_whenKeyDoesNotExist() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(testKey)).thenReturn(null);

        Optional<ClientStats> result = redisLimiterStorage.findByKey(testKey);

        assertFalse(result.isPresent());
        verify(valueOperations, times(1)).get(testKey);
    }

    @Test
    void updateByKey_shouldSetValueWithTtl_whenTtlExists() {
        when(redisTemplate.getExpire(testKey, TimeUnit.SECONDS)).thenReturn(60L);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        redisLimiterStorage.updateByKey(testKey, testStats);

        verify(valueOperations, times(1)).set(testKey, testStats, 60L, TimeUnit.SECONDS);
    }

    @Test
    void updateByKey_shouldSetValueWithoutTtl_whenTtlDoesNotExist() {
        when(redisTemplate.getExpire(testKey, TimeUnit.SECONDS)).thenReturn(-1L);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        redisLimiterStorage.updateByKey(testKey, testStats);

        verify(valueOperations, times(1)).set(testKey, testStats);
    }

    @Test
    void getTtlByKey_shouldReturnTtl_whenKeyExists() {
        when(redisTemplate.getExpire(testKey, TimeUnit.SECONDS)).thenReturn(30L);

        Duration ttl = redisLimiterStorage.getTtlByKey(testKey);

        assertEquals(Duration.ofSeconds(30), ttl);
        verify(redisTemplate, times(1)).getExpire(testKey, TimeUnit.SECONDS);
    }

    @Test
    void getTtlByKey_shouldReturnZero_whenKeyDoesNotExist() {
        when(redisTemplate.getExpire(testKey, TimeUnit.SECONDS)).thenReturn(-2L);

        Duration ttl = redisLimiterStorage.getTtlByKey(testKey);

        assertEquals(Duration.ZERO, ttl);
        verify(redisTemplate, times(1)).getExpire(testKey, TimeUnit.SECONDS);
    }

    @Test
    void setTtlByKey_shouldSetTtl_whenKeyExists() {
        when(redisTemplate.hasKey(testKey)).thenReturn(true);

        Duration ttl = Duration.ofSeconds(120);
        redisLimiterStorage.setTtlByKey(testKey, ttl);

        verify(redisTemplate, times(1)).expire(testKey, ttl.getSeconds(), TimeUnit.SECONDS);
    }

    @Test
    void setTtlByKey_shouldNotSetTtl_whenKeyDoesNotExist() {
        when(redisTemplate.hasKey(testKey)).thenReturn(false);

        Duration ttl = Duration.ofSeconds(120);
        redisLimiterStorage.setTtlByKey(testKey, ttl);

        verify(redisTemplate, never()).expire(testKey, ttl.getSeconds(), TimeUnit.SECONDS);
    }
}