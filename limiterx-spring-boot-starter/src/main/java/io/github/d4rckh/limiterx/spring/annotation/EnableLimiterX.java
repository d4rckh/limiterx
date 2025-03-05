package io.github.d4rckh.limiterx.spring.annotation;

import io.github.d4rckh.limiterx.spring.config.LimiterAutoConfiguration;
import io.github.d4rckh.limiterx.spring.config.RedisTemplateConfig;
import io.github.d4rckh.limiterx.spring.extractor.IPExtractor;
import io.github.d4rckh.limiterx.spring.extractor.NoopExtractor;
import io.github.d4rckh.limiterx.spring.extractor.UsernameExtractor;
import io.github.d4rckh.limiterx.spring.extractor.evaluator.KeyExtractorSpelEvaluator;
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
        UsernameExtractor.class,
        KeyExtractorSpelEvaluator.class
})
public @interface EnableLimiterX {
}
