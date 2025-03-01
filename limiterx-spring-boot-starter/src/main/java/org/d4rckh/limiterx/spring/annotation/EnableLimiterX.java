package org.d4rckh.limiterx.spring.annotation;

import org.d4rckh.limiterx.spring.config.LimiterAutoConfiguration;
import org.d4rckh.limiterx.spring.config.RedisTemplateConfig;
import org.d4rckh.limiterx.spring.extractor.IPExtractor;
import org.d4rckh.limiterx.spring.extractor.KeyExtractor;
import org.d4rckh.limiterx.spring.extractor.NoopExtractor;
import org.d4rckh.limiterx.spring.extractor.UsernameExtractor;
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
@Import({
    RedisTemplateConfig.class,
    LimiterAutoConfiguration.class,
    IPExtractor.class,
    NoopExtractor.class,
    UsernameExtractor.class
})
public @interface EnableLimiterX {
}
