package org.d4rckh.limiterx.spring.annotation;

import org.d4rckh.limiterx.spring.config.LimiterAutoConfiguration;
import org.d4rckh.limiterx.spring.config.RedisTemplateConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables LimiterX auto-configuration.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({LimiterAutoConfiguration.class, RedisTemplateConfig.class})
public @interface EnableLimiterX {
}
