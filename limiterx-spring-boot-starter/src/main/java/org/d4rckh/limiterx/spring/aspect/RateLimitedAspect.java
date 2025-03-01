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
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitedAspect {

    private final Limiter limiter;

    @Before("@annotation(annotation)")
    public void rateLimitFunction(JoinPoint thisJoinPoint, RateLimited annotation) {
        if (!limiter.increaseCounterAndCheckIfLimited(
            Key.fromClass(thisJoinPoint.getClass(), annotation.value()),
            annotation.maximumRequests(),
            annotation.windowSize(),
            annotation.blockFor() == 0 ? null : annotation.blockFor()
        )) {
            throw new LimiterXTooManyRequests();
        }
    }
}