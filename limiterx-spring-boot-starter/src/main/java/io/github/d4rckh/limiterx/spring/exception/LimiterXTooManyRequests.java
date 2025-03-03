package io.github.d4rckh.limiterx.spring.exception;

/**
 * Exception thrown when a request exceeds the allowed rate limit.
 * <p>
 * This exception is triggered when a method annotated with {@code @RateLimited}
 * exceeds the configured request threshold within the specified time window.
 * </p>
 *
 * <p>Example scenario:</p>
 * <pre>
 * {@code
 * @RateLimited(key = UsernameExtractor.class, maximumRequests = 5, windowSize = 60)
 * public void someMethod() { ... }
 * }
 * </pre>
 * <p>
 * If a user exceeds 5 requests within 60 seconds, this exception will be thrown.
 * </p>
 *
 * @see io.github.d4rckh.limiterx.spring.annotation.RateLimited
 */
public class LimiterXTooManyRequests extends RuntimeException {

    /**
     * Constructs a new {@code LimiterXTooManyRequests} exception with a specified message.
     *
     * @param message the detail message explaining the rate limit violation
     */
    public LimiterXTooManyRequests(String message) {
        super(message);
    }
}
