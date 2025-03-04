package io.github.d4rckh.limiterx.spring.annotation;

import io.github.d4rckh.limiterx.spring.common.KeyExtractor;
import io.github.d4rckh.limiterx.spring.domain.NullKeyStrategy;
import io.github.d4rckh.limiterx.spring.extractor.NoopExtractor;
import io.github.d4rckh.limiterx.spring.aspect.RateLimitedAspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for rate limiting a method.
 * <p>
 * This annotation can be used to limit the number of requests made to a method
 * within a specified time window. It supports key extraction strategies to identify
 * unique users or request sources.
 * </p>
 *
 * <p>Features include:</p>
 * <ul>
 *     <li>Configurable key extraction via {@link KeyExtractor}.</li>
 *     <li>Optional fallback key extraction strategy.</li>
 *     <li>Blocking mechanism for requests exceeding the limit.</li>
 *     <li>Handling of null or missing keys.</li>
 * </ul>
 *
 * @see KeyExtractor
 * @see NullKeyStrategy
 * @see RateLimitedAspect
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {

    /**
     * Unused
     *
     * @return the rate limit key
     */
    String value() default "";

    /**
     * The primary key extractor used to generate a unique key for rate limiting.
     * This should implement {@link KeyExtractor}.
     *
     * @return the key extractor class
     */
    Class<? extends KeyExtractor> key() default NoopExtractor.class;

    /**
     * SPeL expression that will be checked before the key extractors, if it returns
     * an empty string, the key will be generated using the key and fallbackKey
     * extractors.
     *
     * @return the SPeL expression used
     */
    String keyExpression() default "";

    /**
     * The fallback key extractor in case the primary key extractor returns null.
     *
     * @return the fallback key extractor class
     */
    Class<? extends KeyExtractor> fallbackKey() default NoopExtractor.class;

    /**
     * The duration (in seconds) for which requests should be blocked once the limit is exceeded.
     * If set to 0, no additional blocking occurs beyond normal rate limiting.
     *
     * @return the block duration in seconds
     */
    int blockFor() default 0;

    /**
     * The maximum number of requests allowed within the given {@code windowSize}.
     *
     * @return the request limit
     */
    int maximumRequests();

    /**
     * Strategy for handling cases where both key extractors return a null key.
     * <p>Can be:</p>
     * <ul>
     *     <li>NullKeyStrategy.AUTO: Will apply NullKeyStrategy.LIMIT if key &amp; fallbackKey are default or NoopExtractor.class, otherwise NullKeyStrategy.FORBID</li>
     *     <li>NullKeyStrategy.LIMIT: In case the key resulted in being null, the request will be assigned an empty key. (Not recommended)</li>
     *     <li>NullKeyStrategy.FORBID: In case the key resulted in being null, a LimiterXMissingKey exception will be thrown.</li>
     *     <li>Handling of null or missing keys.</li>
     * </ul>
     *
     * @return the null key handling strategy
     */
    NullKeyStrategy nullKeyStrategy() default NullKeyStrategy.AUTO;

    /**
     * The size of the time window (in seconds) during which the {@code maximumRequests} applies.
     *
     * @return the window size in seconds
     */
    int windowSize();
}
