package io.github.d4rckh.limiterx.spring.extractor;

import io.github.d4rckh.limiterx.spring.common.KeyExtractor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IPExtractor implements KeyExtractor {

    private final HttpServletRequest httpRequest;

    @Autowired
    public IPExtractor(ObjectFactory<HttpServletRequest> requestFactory) {
        this.httpRequest = requestFactory.getObject();
    }

    @Override
    public String extract() {
        String xForwardedForHeader = httpRequest.getHeader("X-Forwarded-For");

        return xForwardedForHeader == null ? httpRequest.getRemoteAddr() : xForwardedForHeader;
    }
}
