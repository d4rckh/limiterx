package org.d4rckh.limiterx.spring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.d4rckh.limiterx.core.domain.ClientStats;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisTemplateConfig {

    @Bean
    public RedisTemplate<String, ClientStats> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ClientStats> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Configure ObjectMapper with JavaTimeModule
        ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())  // Support Java 8 time (Instant, LocalDateTime, etc.)
            .registerModule(new SimpleModule());   // Avoid default LinkedHashMap conversion

        // Configure serializer without deprecated methods
        Jackson2JsonRedisSerializer<ClientStats> serializer =
            new Jackson2JsonRedisSerializer<>(objectMapper, ClientStats.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}
