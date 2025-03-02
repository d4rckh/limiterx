package io.github.d4rckh.limiterx.spring.extractor;

import lombok.extern.slf4j.Slf4j;
import io.github.d4rckh.limiterx.spring.common.KeyExtractor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NoopExtractor implements KeyExtractor {
    @Override
    public String extract() {
        return null;
    }
}
