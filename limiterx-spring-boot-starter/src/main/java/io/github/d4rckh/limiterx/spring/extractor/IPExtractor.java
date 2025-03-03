package io.github.d4rckh.limiterx.spring.extractor;

import io.github.d4rckh.limiterx.spring.common.KeyExtractor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Extracts the client's IP address for rate limiting.
 * <p>
 * This extractor retrieves the IP address from the {@code X-Forwarded-For} header if present.
 * Otherwise, it falls back to {@link HttpServletRequest#getRemoteAddr()}.
 * </p>
 *
 * <p>Example usage in {@code @RateLimited}:</p>
 * <pre>
 * {@code
 * @RateLimited(key = IPExtractor.class, maximumRequests = 10, windowSize = 60)
 * public void someMethod() { ... }
 * }
 * </pre>
 */
@Component
public class IPExtractor implements KeyExtractor {

    private final HttpServletRequest httpRequest;

    /**
     * Constructs an {@code IPExtractor} that retrieves the {@link HttpServletRequest} instance lazily.
     *
     * @param requestFactory Factory for obtaining the {@code HttpServletRequest} instance
     */
    @Autowired
    public IPExtractor(ObjectFactory<HttpServletRequest> requestFactory) {
        this.httpRequest = requestFactory.getObject();
    }

    /**
     * Extracts the client's IP address.
     * <p>
     * Checks the {@code X-Forwarded-For} header first. If unavailable, falls back to
     * {@link HttpServletRequest#getRemoteAddr()}.
     * </p>
     *
     * @return the extracted IP address
     */
    @Override
    public String extract() {
        String xForwardedForHeader = httpRequest.getHeader("X-Forwarded-For");
        return xForwardedForHeader == null ? httpRequest.getRemoteAddr() : xForwardedForHeader;
    }
}
