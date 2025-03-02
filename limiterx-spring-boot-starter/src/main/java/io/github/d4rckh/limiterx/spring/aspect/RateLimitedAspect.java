package io.github.d4rckh.limiterx.spring.aspect;

import io.github.d4rckh.limiterx.core.Limiter;
import io.github.d4rckh.limiterx.core.domain.Key;
import io.github.d4rckh.limiterx.spring.annotation.RateLimited;
import io.github.d4rckh.limiterx.spring.domain.NullKeyStrategy;
import io.github.d4rckh.limiterx.spring.exception.LimiterXMissingKey;
import io.github.d4rckh.limiterx.spring.exception.LimiterXTooManyRequests;
import io.github.d4rckh.limiterx.spring.extractor.NoopExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitedAspect {

    private final Limiter limiter;

    private final ApplicationContext context;

    @Before("@annotation(annotation)")
    public void rateLimitFunction(JoinPoint joinPoint, RateLimited annotation) {
        String key = context.getBean(annotation.key()).extract();
        NullKeyStrategy nullKeyStrategy = annotation.nullKeyStrategy();

        if (key == null) {
            key = context.getBean(annotation.fallbackKey()).extract();
        }

        if (nullKeyStrategy.equals(NullKeyStrategy.AUTO)) {
            if (annotation.key() == NoopExtractor.class && annotation.fallbackKey() == NoopExtractor.class) {
                nullKeyStrategy = NullKeyStrategy.LIMIT;
            } else {
                nullKeyStrategy = NullKeyStrategy.FORBID;
            }
        }

        if (key == null && nullKeyStrategy.equals(NullKeyStrategy.FORBID)) {
            throw new LimiterXMissingKey();
        }

        if (limiter.increaseCounterAndCheckIfLimited(
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
