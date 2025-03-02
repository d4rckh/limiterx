package io.github.d4rckh.limiterx.spring.exception;

public class LimiterXMissingKey extends RuntimeException {
    public LimiterXMissingKey() {
        super("Not allowed due to having a null key");
    }
}
