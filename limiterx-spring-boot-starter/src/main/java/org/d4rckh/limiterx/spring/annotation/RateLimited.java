package org.d4rckh.limiterx.spring.annotation;

import org.d4rckh.limiterx.spring.extractor.KeyExtractor;
import org.d4rckh.limiterx.spring.extractor.NoopExtractor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {
    String value() default "";

    Class<? extends KeyExtractor> key() default NoopExtractor.class;

    int blockFor() default 0;

    int maximumRequests();

    int windowSize();
}