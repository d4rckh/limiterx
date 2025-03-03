package io.github.d4rckh.limiterx.spring.config;

import lombok.extern.slf4j.Slf4j;
import io.github.d4rckh.limiterx.core.Limiter;
import io.github.d4rckh.limiterx.core.domain.ClientStats;
import io.github.d4rckh.limiterx.core.storage.InMemoryLimiterStorage;
import io.github.d4rckh.limiterx.spring.storage.RedisLimiterStorage;
import io.github.d4rckh.limiterx.spring.aspect.RateLimitedAspect;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Auto-configuration for the LimiterX rate limiting library.
 * <p>
 * This configuration automatically sets up the appropriate rate limiter storage
 * based on application properties. It supports:
 * </p>
 * <ul>
 *     <li>Redis-based storage (default) if {@code limiterx.storage=redis}.</li>
 *     <li>In-memory storage if {@code limiterx.storage=memory}.</li>
 * </ul>
 *
 * <p>Configuration properties:</p>
 * <ul>
 *     <li>{@code limiterx.storage=redis} → Uses Redis for storage (default if Redis is available).</li>
 *     <li>{@code limiterx.storage=memory} → Uses an in-memory store.</li>
 * </ul>
 *
 * <p>Example usage in {@code application.yml}:</p>
 * <pre>
 * limiterx:
 *   storage: redis
 * </pre>
 *
 * <p>The configuration ensures that only one {@link Limiter} bean is created.</p>
 *
 * @see Limiter
 * @see RedisLimiterStorage
 * @see InMemoryLimiterStorage
 */
@Slf4j
@Configuration
@AutoConfigureAfter(RedisTemplateConfig.class)
@ComponentScan(basePackageClasses = RateLimitedAspect.class)
public class LimiterAutoConfiguration {

    /**
     * Configures a {@link Limiter} instance using Redis storage.
     * <p>
     * This bean is created if Redis is available in the classpath and
     * the property {@code limiterx.storage=redis} is set (or missing).
     * </p>
     *
     * @param redisTemplate the Redis template used for storing rate limit data
     * @return a {@link Limiter} instance backed by Redis
     */
    @Bean
    @ConditionalOnClass(RedisTemplate.class)
    @ConditionalOnProperty(name = "limiterx.storage", havingValue = "redis", matchIfMissing = true)
    public Limiter limiterWithRedis(RedisTemplate<String, ClientStats> redisTemplate) {
        log.info("Configuring limiter using Redis");
        return new Limiter(new RedisLimiterStorage(redisTemplate));
    }

    /**
     * Configures a {@link Limiter} instance using in-memory storage.
     * <p>
     * This bean is created if the property {@code limiterx.storage=memory} is set
     * and no other {@link Limiter} bean is present.
     * </p>
     *
     * @return a {@link Limiter} instance backed by in-memory storage
     */
    @Bean
    @ConditionalOnMissingBean(Limiter.class)
    @ConditionalOnProperty(name = "limiterx.storage", havingValue = "memory")
    public Limiter limiterWithInMemory() {
        log.info("Configuring limiter using in-memory storage");
        return new Limiter(new InMemoryLimiterStorage());
    }
}
