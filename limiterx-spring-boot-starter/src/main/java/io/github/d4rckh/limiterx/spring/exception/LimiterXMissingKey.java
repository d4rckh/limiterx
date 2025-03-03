package io.github.d4rckh.limiterx.spring.exception;

/**
 * Exception thrown when a required rate-limiting key is missing.
 * <p>
 * This exception is triggered when a method annotated with {@code @RateLimited}
 * cannot extract a valid key and the configured {@link io.github.d4rckh.limiterx.spring.domain.NullKeyStrategy}
 * is set to {@code FORBID}.
 * </p>
 *
 * <p>Example scenario:</p>
 * <pre>
 * {@code
 * @RateLimited(key = UsernameExtractor.class, nullKeyStrategy = NullKeyStrategy.FORBID)
 * public void someMethod() { ... }
 * }
 * </pre>
 * <p>
 * If the {@code UsernameExtractor} fails to provide a key, this exception will be thrown.
 * </p>
 *
 * @see io.github.d4rckh.limiterx.spring.domain.NullKeyStrategy
 */
public class LimiterXMissingKey extends RuntimeException {

    /**
     * Constructs a new {@code LimiterXMissingKey} exception with a default message.
     */
    public LimiterXMissingKey() {
        super("Not allowed due to having a null key");
    }
}
