package io.github.d4rckh.limiterx.spring.extractor;

import io.github.d4rckh.limiterx.spring.common.KeyExtractor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Extracts the client's IP address for rate limiting.
 */
@Slf4j
@Component
public class IPExtractor implements KeyExtractor {
    private final ObjectFactory<HttpServletRequest> requestFactory;

    /**
     * Constructs an {@code IPExtractor} that retrieves the {@link HttpServletRequest} instance lazily.
     *
     * @param requestFactory Factory for obtaining the {@code HttpServletRequest} instance
     */
    @Autowired
    public IPExtractor(ObjectFactory<HttpServletRequest> requestFactory) {
        this.requestFactory = requestFactory;
    }

    /**
     * Extracts the client's IP address.
     *
     * @return the extracted IP address
     */
    @Override
    public String extract() {
        HttpServletRequest httpRequest = requestFactory.getObject();
        String xForwardedForHeader = httpRequest.getHeader("X-Forwarded-For");
        return xForwardedForHeader == null ? httpRequest.getRemoteAddr() : xForwardedForHeader;
    }
}
