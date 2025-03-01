package org.d4rckh.limiterx.spring.exception;

public class LimiterXTooManyRequests extends RuntimeException {
    public LimiterXTooManyRequests(String message) {
        super(message);
    }
}
