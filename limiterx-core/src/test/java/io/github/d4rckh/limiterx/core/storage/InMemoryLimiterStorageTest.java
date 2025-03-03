package io.github.d4rckh.limiterx.core.storage;

import io.github.d4rckh.limiterx.core.domain.ClientStats;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class InMemoryLimiterStorageTest {

    @Test
    void findByKey_shouldReturnEmpty_whenKeyDoesNotExist() {
        InMemoryLimiterStorage storage = new InMemoryLimiterStorage();

        assertThat(storage.findByKey("non-existent key").isPresent()).isFalse();
    }

    @Test
    void findByKey_shouldReturnEmpty_whenKeyExpired() {
        InMemoryLimiterStorage storage = new InMemoryLimiterStorage();

        storage.updateByKey("key", new ClientStats());
        storage.setTtlByKey("key", Duration.ofSeconds(1));

        await().atMost(Duration.ofSeconds(3))
            .until(() -> storage.findByKey("key").isEmpty());

        assertThat(storage.findByKey("key")).isEmpty();
    }

    @Test
    void updateByKey_shouldCreateKey() {
        InMemoryLimiterStorage storage = new InMemoryLimiterStorage();

        storage.updateByKey("key", new ClientStats());

        assertThat(storage.findByKey("key").isPresent()).isTrue();
        assertThat(storage.findByKey("key").get().getTotalRequests()).isEqualTo(0);
    }

    @Test
    void getTtlByKey_shouldReturnZero_whenKeyDoesNotHaveTtl() {
        InMemoryLimiterStorage storage = new InMemoryLimiterStorage();

        storage.updateByKey("key", new ClientStats());

        assertThat(storage.getTtlByKey("key")).isEqualTo(Duration.ZERO);
    }

    @Test
    void getTtlByKey_shouldReturnZero_whenKeyDoesHaveTtl() {
        InMemoryLimiterStorage storage = new InMemoryLimiterStorage();

        storage.updateByKey("key", new ClientStats());
        storage.setTtlByKey("key", Duration.ofSeconds(10));

        assertThat(storage.getTtlByKey("key")).isCloseTo(Duration.ofSeconds(10), Duration.ofMillis(500));
    }
}
