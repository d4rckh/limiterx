package org.d4rckh.limiterx.spring.config;

import lombok.extern.slf4j.Slf4j;
import org.d4rckh.limiterx.core.Limiter;
import org.d4rckh.limiterx.core.domain.ClientStats;
import org.d4rckh.limiterx.core.storage.InMemoryStorage;
import org.d4rckh.limiterx.spring.RedisStorage;
import org.d4rckh.limiterx.spring.aspect.RateLimitedAspect;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@Configuration
@AutoConfigureAfter(RedisTemplateConfig.class)
@ComponentScan(basePackageClasses = RateLimitedAspect.class)
public class LimiterAutoConfiguration {

    @Bean
    @ConditionalOnClass(RedisTemplate.class)
    @ConditionalOnProperty(name = "limiterx.storage", havingValue = "redis", matchIfMissing = true)
    public Limiter limiterWithRedis(RedisTemplate<String, ClientStats> redisTemplate) {
        log.info("Configuring limiter using Redis");
        return new Limiter(new RedisStorage(redisTemplate));
    }

    @Bean
    @ConditionalOnMissingBean(Limiter.class)
    @ConditionalOnProperty(name = "limiterx.storage", havingValue = "memory")
    public Limiter limiterWithInMemory() {
        log.info("Configuring limiter using in-memory storage");
        return new Limiter(new InMemoryStorage());
    }
}
