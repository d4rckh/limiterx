package io.github.d4rckh.limiterx.spring.aspect;

import io.github.d4rckh.limiterx.core.Limiter;
import io.github.d4rckh.limiterx.core.domain.Key;
import io.github.d4rckh.limiterx.spring.annotation.RateLimited;
import io.github.d4rckh.limiterx.spring.domain.NullKeyStrategy;
import io.github.d4rckh.limiterx.spring.exception.LimiterXMissingKey;
import io.github.d4rckh.limiterx.spring.exception.LimiterXTooManyRequests;
import io.github.d4rckh.limiterx.spring.extractor.NoopExtractor;
import io.github.d4rckh.limiterx.spring.extractor.evaluator.KeyExtractorSpelEvaluator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Aspect for handling rate limiting using the {@link RateLimited} annotation.
 * <p>
 * This aspect intercepts method calls annotated with {@code @RateLimited} and applies
 * rate-limiting logic based on the extracted key. It supports multiple key extraction strategies,
 * fallback handling, and enforcement of request limits.
 * </p>
 *
 * <p>Functionality:</p>
 * <ul>
 *     <li>Extracts a rate-limiting key using a primary {@code KeyExtractor}.</li>
 *     <li>Uses a fallback extractor if the primary extractor returns {@code null}.</li>
 *     <li>Determines the behavior when no key is found (allow, limit, or forbid execution).</li>
 *     <li>Checks if the request exceeds the allowed limit within the time window.</li>
 *     <li>Throws {@link LimiterXTooManyRequests} if the request is blocked.</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * @RateLimited(
 *     key = UserIdExtractor.class,
 *     fallbackKey = IpAddressExtractor.class,
 *     maximumRequests = 5,
 *     windowSize = 60
 * )
 * public void someRateLimitedMethod() {
 *     // Method logic
 * }
 * }
 * </pre>
 *
 * @author d4rck
 * @see RateLimited
 * @see Limiter
 * @see NullKeyStrategy
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitedAspect {

    private final Limiter limiter;
    private final ApplicationContext context;
    private final KeyExtractorSpelEvaluator evaluator;

    /**
     * Intercepts methods annotated with {@link RateLimited} and enforces rate limiting.
     *
     * @param joinPoint  the intercepted method invocation
     * @param annotation the {@code RateLimited} annotation instance
     * @throws LimiterXMissingKey if both key extractors return null and the null key strategy is {@code FORBID}
     * @throws LimiterXTooManyRequests if the request exceeds the allowed rate limit
     */
    @Before("@annotation(annotation)")
    public void rateLimitFunction(JoinPoint joinPoint, RateLimited annotation) {
        String key = evaluator.evaluate(annotation.keyExpression());

        if (key.isEmpty()) {
            key = context.getBean(annotation.key()).extract();
        }

        // Attempt to use fallback key if primary key extraction fails
        if (key == null) {
            key = context.getBean(annotation.fallbackKey()).extract();
        }

        NullKeyStrategy nullKeyStrategy = annotation.nullKeyStrategy();

        // Automatically determine null key handling strategy if set to AUTO
        if (nullKeyStrategy.equals(NullKeyStrategy.AUTO)) {
            if (annotation.key() == NoopExtractor.class && annotation.fallbackKey() == NoopExtractor.class) {
                nullKeyStrategy = NullKeyStrategy.LIMIT;
            } else {
                nullKeyStrategy = NullKeyStrategy.FORBID;
            }
        }

        // Enforce FORBID strategy if no key is found
        if (key == null && nullKeyStrategy.equals(NullKeyStrategy.FORBID)) {
            throw new LimiterXMissingKey();
        }

        // Perform rate limiting
        if (limiter.performLimiting(
            Key.fromClass(
                joinPoint.getTarget().getClass(),
                joinPoint.getSignature().getName(),
                key == null ? "" : key
            ),
            annotation.maximumRequests(),
            annotation.windowSize(),
            annotation.blockFor() == 0 ? null : annotation.blockFor()
        )) {
            throw new LimiterXTooManyRequests("Too many requests");
        }
    }
}
