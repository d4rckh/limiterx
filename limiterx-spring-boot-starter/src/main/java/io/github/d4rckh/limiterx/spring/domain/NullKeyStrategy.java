package io.github.d4rckh.limiterx.spring.domain;

/**
 * Defines strategies for handling cases where a rate-limiting key is null.
 * <p>
 * This strategy is used when a key extractor does not return a valid key.
 * </p>
 *
 * <ul>
 *     <li>{@link #FORBID} - Rejects the request if the key is null.</li>
 *     <li>{@link #LIMIT} - Applies rate limiting under a default or shared key.</li>
 *     <li>{@link #AUTO} - Automatically determines the strategy based on the extractors used.</li>
 * </ul>
 *
 * <p>Example usage in a {@code @RateLimited} annotation:</p>
 * <pre>
 * {@code
 * @RateLimited(key = UserIdExtractor.class, nullKeyStrategy = NullKeyStrategy.FORBID)
 * public void someMethod() { ... }
 * }
 * </pre>
 *
 */
public enum NullKeyStrategy {
    /**
     * Rejects the request if the key is null.
     * Typically used when a key is required for proper rate limiting.
     */
    FORBID,

    /**
     * Applies rate limiting under a default or shared key.
     * This prevents requests from bypassing limits due to a missing key.
     */
    LIMIT,

    /**
     * Automatically determines the strategy:
     * <ul>
     *     <li>If no extractors are default or NoopExtractor and key expression is blank, defaults to {@link #LIMIT}.</li>
     *     <li>Otherwise, defaults to {@link #FORBID}.</li>
     * </ul>
     */
    AUTO
}
