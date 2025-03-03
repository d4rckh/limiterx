package io.github.d4rckh.limiterx.spring.extractor;

import lombok.extern.slf4j.Slf4j;
import io.github.d4rckh.limiterx.spring.common.KeyExtractor;
import org.springframework.stereotype.Component;

/**
 * A no-operation (noop) key extractor that always returns {@code null}.
 * <p>
 * This extractor is used as a default when no specific key extraction strategy is needed.
 * It can also serve as a fallback when no meaningful key can be derived.
 * </p>
 *
 * <p>Example usage in {@code @RateLimited}:</p>
 * <pre>
 * {@code
 * @RateLimited(key = NoopExtractor.class)
 * public void someMethod() { ... }
 * }
 * </pre>
 */
@Slf4j
@Component
public class NoopExtractor implements KeyExtractor {

    /**
     * Always returns {@code null}, effectively disabling key-based rate limiting.
     *
     * @return {@code null}
     */
    @Override
    public String extract() {
        return null;
    }
}
