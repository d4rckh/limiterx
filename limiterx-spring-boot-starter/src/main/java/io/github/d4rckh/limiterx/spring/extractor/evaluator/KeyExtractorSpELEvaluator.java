package io.github.d4rckh.limiterx.spring.extractor.evaluator;

import io.github.d4rckh.limiterx.spring.common.KeyExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Evaluates Spring Expression Language (SpEL) expressions to extract rate-limiting keys dynamically.
 * <p>
 * This component registers all available {@link KeyExtractor} beans in the application context,
 * making them accessible within SpEL expressions. Users can specify expressions like:
 * </p>
 * <pre>
 * {@code @RateLimited(keyExpression = "#ipExtractor.extract()")}
 * </pre>
 * <p>
 * or
 * </p>
 * <pre>
 * {@code @RateLimited(keyExpression = "#headerExtractor.extract('x-myheader')")}
 * </pre>
 * <p>
 * This allows dynamic evaluation of key extraction methods at runtime.
 * </p>
 *
 * @author d4rckh
 */
@Slf4j
@Service
public class KeyExtractorSpELEvaluator {
    private final ExpressionParser parser = new SpelExpressionParser();
    private final StandardEvaluationContext context = new StandardEvaluationContext();

    /**
     * Initializes the SpEL evaluator and registers all {@link KeyExtractor} beans as variables.
     *
     * @param applicationContext the Spring application context, used to retrieve {@link KeyExtractor} beans
     */
    @Autowired
    public KeyExtractorSpELEvaluator(ApplicationContext applicationContext) {
        // Retrieve all KeyExtractor beans and register them as SpEL variables
        Map<String, KeyExtractor> extractors = applicationContext.getBeansOfType(KeyExtractor.class);

        extractors.forEach((_, instance) -> context.setVariable(instance.getClass().getSimpleName(), instance));
    }

    /**
     * Evaluates a given SpEL expression and returns the extracted key.
     * <p>
     * Example usage:
     * <pre>
     * {@code String key = evaluator.evaluate("#IPExtractor.extract()");}
     * </pre>
     * or
     * <pre>
     * {@code String key = evaluator.evaluate("#HeaderExtractor.extract('x-myheaderk')");}
     * </pre>
     * </p>
     *
     * @param expression the SpEL expression to evaluate
     * @return the extracted key as a string, or {@code null} if evaluation fails
     */
    public String evaluate(String expression) {
        return parser.parseExpression(expression).getValue(context, String.class);
    }
}
