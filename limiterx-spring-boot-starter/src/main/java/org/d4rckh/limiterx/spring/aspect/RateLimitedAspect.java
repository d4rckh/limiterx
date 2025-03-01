package org.d4rckh.limiterx.spring.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.d4rckh.limiterx.core.Limiter;
import org.d4rckh.limiterx.core.common.Key;
import org.d4rckh.limiterx.spring.annotation.RateLimited;
import org.d4rckh.limiterx.spring.exception.LimiterXTooManyRequests;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitedAspect {

    private final Limiter limiter;
    private final ExpressionParser parser = new SpelExpressionParser();

    @Before("@annotation(annotation)")
    public void rateLimitFunction(JoinPoint joinPoint, RateLimited annotation) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // Extract method parameters and values
        Object[] args = joinPoint.getArgs();
        String[] paramNames = signature.getParameterNames();

        // Create evaluation context
        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        // Parse and evaluate SpEL expression from annotation.value()
        Expression expression = parser.parseExpression(annotation.value());
        String resolvedKey = expression.getValue(context, String.class);

        log.info("Resolved key for rate limiting: {}", resolvedKey);

        // Perform rate limiting with the evaluated key
        if (!limiter.increaseCounterAndCheckIfLimited(
            Key.fromClass(joinPoint.getTarget().getClass(), resolvedKey),
            annotation.maximumRequests(),
            annotation.windowSize(),
            annotation.blockFor() == 0 ? null : annotation.blockFor()
        )) {
            throw new LimiterXTooManyRequests("Too many requests");
        }
    }
}
