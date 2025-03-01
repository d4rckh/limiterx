package org.d4rckh.limiterx.spring.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.d4rckh.limiterx.core.Limiter;
import org.d4rckh.limiterx.core.common.Key;
import org.d4rckh.limiterx.spring.annotation.RateLimited;
import org.d4rckh.limiterx.spring.exception.LimiterXTooManyRequests;
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

        if (limiter.increaseCounterAndCheckIfLimited(
            Key.fromClass(joinPoint.getTarget().getClass(), joinPoint.getSignature().getName(), key),
            annotation.maximumRequests(),
            annotation.windowSize(),
            annotation.blockFor() == 0 ? null : annotation.blockFor()
        )) {
            throw new LimiterXTooManyRequests("Too many requests");
        }
    }
}
