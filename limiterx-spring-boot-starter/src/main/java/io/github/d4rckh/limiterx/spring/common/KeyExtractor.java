package io.github.d4rckh.limiterx.spring.common;

/**
 * Interface for extracting a key used in rate limiting.
 * <p>
 * Implementations of this interface define how to extract a unique key
 * (e.g., user ID, IP address) from the execution context to enforce rate limits.
 * </p>
 *
 */
public interface KeyExtractor {

    /**
     * Extracts a unique key for rate limiting.
     *
     * @return the extracted key as a {@code String}, or {@code null} if no key can be determined
     */
    String extract();
}
