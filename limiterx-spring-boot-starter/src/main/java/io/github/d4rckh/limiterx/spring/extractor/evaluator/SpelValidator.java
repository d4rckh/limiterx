package io.github.d4rckh.limiterx.spring.extractor.evaluator;

import io.github.d4rckh.limiterx.spring.annotation.RateLimited;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SpelValidator implements ApplicationListener<ContextRefreshedEvent> {
    private final ApplicationContext context;
    private final KeyExtractorSpelEvaluator keyExtractor;

    @Autowired
    public SpelValidator(ApplicationContext context, KeyExtractorSpelEvaluator keyExtractor) {
        this.context = context;
        this.keyExtractor = keyExtractor;
    }

    public void validateSpELExpressions() {
        List<Method> rateLimitedMethods = findRateLimitedMethods();

        log.info("Checking {} @RateLimited annotated methods", rateLimitedMethods.size());

        for (Method method : rateLimitedMethods) {
            RateLimited annotation = method.getAnnotation(RateLimited.class);
            String spelExpression = annotation.keyExpression();

            if (spelExpression.isBlank()) continue;

            try {
                keyExtractor.evaluate(spelExpression);
            } catch (SpelEvaluationException e) {
                throw new IllegalStateException(String.format("Invalid SpEL expression in @RateLimited on method: %s.%s -> %s",
                    method.getDeclaringClass().getName(), method.getName(), spelExpression), e);
            } catch (Exception _) {
                // Ignored any other exception
            }

        }

        log.info("All @RateLimited SpEL expressions are valid.");
    }

    private List<Method> findRateLimitedMethods() {
        List<Method> annotatedMethods = new ArrayList<>();

        Map<String, Object> beans = context.getBeansOfType(Object.class);

        for (Object bean : beans.values()) {
            Class<?> beanClass = AopProxyUtils.ultimateTargetClass(bean);

            for (Method method : beanClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(RateLimited.class)) {
                    annotatedMethods.add(method);
                }
            }
        }
        return annotatedMethods;
    }

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        validateSpELExpressions();
    }
}
