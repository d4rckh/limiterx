package io.github.d4rckh.limiterx.spring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.d4rckh.limiterx.core.domain.ClientStats;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Configuration for Redis integration in the LimiterX library.
 * <p>
 * This class provides a customized {@link RedisTemplate} bean for storing
 * {@link ClientStats} in Redis. It uses Jackson-based JSON serialization
 * for structured data storage.
 * </p>
 *
 * <p>This configuration is only active if the property {@code limiterx.storage=redis}
 * is set or missing.</p>
 *
 * <p>Example usage in {@code application.yml}:</p>
 * <pre>
 * limiterx:
 *   storage: redis
 * </pre>
 *
 * @see RedisTemplate
 * @see ClientStats
 */
@Configuration
public class RedisTemplateConfig {

    /**
     * Creates and configures a {@link RedisTemplate} for storing {@link ClientStats}.
     * <p>
     * This template uses JSON serialization for values and {@link StringRedisSerializer}
     * for keys, ensuring compatibility with Redis storage.
     * </p>
     *
     * @param connectionFactory the Redis connection factory
     * @return a configured {@link RedisTemplate} instance
     */
    @Bean
    @ConditionalOnProperty(name = "limiterx.storage", havingValue = "redis", matchIfMissing = true)
    public RedisTemplate<String, ClientStats> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ClientStats> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Configure ObjectMapper with JavaTimeModule to handle Java 8 time types
        ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .registerModule(new SimpleModule());

        // Use Jackson JSON serialization for storing ClientStats in Redis
        Jackson2JsonRedisSerializer<ClientStats> serializer =
            new Jackson2JsonRedisSerializer<>(objectMapper, ClientStats.class);

        // Set key and value serializers
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}
