package org.d4rckh.limiterx.spring.config;

import lombok.extern.slf4j.Slf4j;
import org.d4rckh.limiterx.core.Limiter;
import org.d4rckh.limiterx.core.domain.ClientStats;
import org.d4rckh.limiterx.spring.RedisStorage;
import org.d4rckh.limiterx.spring.aspect.RateLimitedAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@Configuration
@ComponentScan(basePackageClasses = RateLimitedAspect.class)
public class LimiterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(Limiter.class)
    public Limiter limiter(
        RedisTemplate<String, ClientStats> redisTemplate
    ) {
        log.info("Configuring limiter using Redis");

        return new Limiter(
            new RedisStorage(redisTemplate)
        );
    }

}
