package io.github.d4rckh.limiterx.core;

import io.github.d4rckh.limiterx.core.domain.Key;
import io.github.d4rckh.limiterx.core.storage.InMemoryLimiterStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class LimiterTest {

    private Limiter limiter;
    private Key key;

    @BeforeEach
    void setup() {
        limiter = new Limiter(new InMemoryLimiterStorage());
        key = Key.of("hello");
    }

    @Test
    void performLimiting_shouldPermitRequests_whenUnderLimit() {
        boolean req1 = limiter.performLimiting(key, 3, 3, null);
        assertThat(req1).isFalse(); // permitted
        boolean req2 = limiter.performLimiting(key, 3, 3, null);
        assertThat(req2).isFalse(); // permitted
        boolean req3 = limiter.performLimiting(key, 3, 3, null);
        assertThat(req3).isFalse(); // permitted
        boolean req4 = limiter.performLimiting(key, 3, 3, null);
        assertThat(req4).isTrue();  // blocked
    }

    @Test
    void performLimiting_shouldAllowRequests_whenWindowExpires() {
        boolean req1 = limiter.performLimiting(key, 3, 3, null);
        assertThat(req1).isFalse(); // permitted
        boolean req2 = limiter.performLimiting(key, 3, 3, null);
        assertThat(req2).isFalse(); // permitted
        boolean req3 = limiter.performLimiting(key, 3, 3, null);
        assertThat(req3).isFalse(); // permitted
        boolean req4 = limiter.performLimiting(key, 3, 3, null);
        assertThat(req4).isTrue();  // blocked

        await().atMost(Duration.ofSeconds(4)).until(() ->
            !limiter.isClientRateLimited(key, 3, 3, null)
        );

        boolean req5 = limiter.performLimiting(key, 3, 3, null);
        assertThat(req5).isFalse(); // should be allowed again
    }

    @Test
    void performLimiting_shouldBlockClient_whenBlockDurationIsSpecified() {
        boolean req1 = limiter.performLimiting(key, 3, 3, 10);
        assertThat(req1).isFalse(); // permitted
        boolean req2 = limiter.performLimiting(key, 3, 3, 10);
        assertThat(req2).isFalse(); // permitted
        boolean req3 = limiter.performLimiting(key, 3, 3, 10);
        assertThat(req3).isFalse(); // permitted

        boolean requestThatBlocks = limiter.performLimiting(key, 3, 3, 10);
        assertThat(requestThatBlocks).isTrue(); // should be blocked
        boolean blockedRequest = limiter.performLimiting(key, 3, 3, 10);
        assertThat(blockedRequest).isTrue(); // should be blocked

        assertThat(limiter.isClientRateLimited(key, 3, 3, 10)).isTrue(); // still blocked

        // Wait for block time to expire (10 seconds)
        await().atMost(Duration.ofSeconds(11)).until(() ->
            !limiter.isClientRateLimited(key, 3, 3, 10)
        );

        boolean req5 = limiter.performLimiting(key, 3, 3, null);
        assertThat(req5).isFalse(); // should be permitted after block time expires
    }

    @Test
    void performLimiting_shouldBlockRequest_whenLimitIsOne() {
        boolean req1 = limiter.performLimiting(key, 1, 3, null);
        assertThat(req1).isFalse(); // first request allowed
        boolean req2 = limiter.performLimiting(key, 1, 3, null);
        assertThat(req2).isTrue();  // second request blocked

        await().atMost(Duration.ofSeconds(4)).until(() ->
            !limiter.isClientRateLimited(key, 1, 3, null)
        );

        boolean req3 = limiter.performLimiting(key, 1, 3, null);
        assertThat(req3).isFalse(); // should be allowed after window reset
    }

    @Test
    void performLimiting_shouldResetAfterWindowExpires_whenClientIsBlocked() {
        boolean req1 = limiter.performLimiting(key, 3, 3, 10);
        assertThat(req1).isFalse(); // permitted
        boolean req2 = limiter.performLimiting(key, 3, 3, 10);
        assertThat(req2).isFalse(); // permitted
        boolean req3 = limiter.performLimiting(key, 3, 3, 10);
        assertThat(req3).isFalse(); // permitted
        boolean req4 = limiter.performLimiting(key, 3, 3, 10);
        assertThat(req4).isTrue();  // should be blocked

        // Ensure client remains blocked
        assertThat(limiter.isClientRateLimited(key, 3, 3, 10)).isTrue();

        // Wait for rate limit window (3 seconds) + block duration (10 seconds)
        await().atMost(Duration.ofSeconds(14)).until(() ->
            !limiter.isClientRateLimited(key, 3, 3, 10)
        );

        boolean req5 = limiter.performLimiting(key, 3, 3, null);
        assertThat(req5).isFalse(); // should be allowed again after full reset
    }
}