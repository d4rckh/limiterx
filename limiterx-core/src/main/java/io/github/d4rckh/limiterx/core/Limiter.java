package io.github.d4rckh.limiterx.core;

import io.github.d4rckh.limiterx.core.common.LimiterStorage;
import io.github.d4rckh.limiterx.core.domain.ClientStats;
import io.github.d4rckh.limiterx.core.domain.Key;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.Instant;

@RequiredArgsConstructor
public class Limiter {
    private final LimiterStorage storage;

    public boolean increaseCounterAndCheckIfLimited(
        @NonNull Key key,
        int maximumRequests, int windowSeconds,
        Integer blockFor
    ) {
        final String rawKey = key.getKey();
        ClientStats clientStats = storage.findByKey(rawKey)
            .orElse(null);

        if (clientStats == null) {
            clientStats = new ClientStats(0, null, Instant.now());
            storage.updateByKey(rawKey, clientStats);
        }

        // Reset request window if expired
        resetRequestWindowIfExpired(clientStats, rawKey, windowSeconds);

        if (clientStats.getTotalRequests() == 0 && !isClientBlocked(clientStats, blockFor)) {
            storage.setTtlByKey(rawKey, Duration.ofSeconds(windowSeconds));
        }

        // Always increment request count
        clientStats.increaseTotalRequests();
        storage.updateByKey(rawKey, clientStats);

        // Check if client is currently blocked
        if (isClientBlocked(clientStats, blockFor)) {
            return true;
        }

        // Check if client exceeded request limit and should be blocked
        if (shouldBlockClient(clientStats, maximumRequests)) {
            blockClient(clientStats, blockFor);
            updateTtlForBlockedClient(rawKey, windowSeconds, blockFor);
            storage.updateByKey(rawKey, clientStats);
            return true;
        }

        return false; // Request is allowed
    }

    private void resetRequestWindowIfExpired(ClientStats clientStats, String rawKey, int windowSeconds) {
        if (clientStats.getLastReset().plusSeconds(windowSeconds).isBefore(Instant.now())) {
            clientStats.setLastReset(Instant.now());
            clientStats.setTotalRequests(0);
        }
    }

    private boolean isClientBlocked(ClientStats clientStats, Integer blockFor) {
        if (clientStats.getBlockedAt() == null || blockFor == null) {
            return false;
        }

        Instant blockedUntil = clientStats.getBlockedAt().plusSeconds(blockFor);
        if (blockedUntil.isAfter(Instant.now())) {
            return true;
        }

        // Unblock client after block duration has expired
        clientStats.setBlockedAt(null);
        return false;
    }

    private boolean shouldBlockClient(ClientStats clientStats, int maximumRequests) {
        return clientStats.getTotalRequests() > maximumRequests; // Removed `blockFor != null`
    }

    private void blockClient(ClientStats clientStats, Integer blockFor) {
        if (blockFor != null) {
            clientStats.setBlockedAt(Instant.now());
        }
    }

    private void updateTtlForBlockedClient(String rawKey, int windowSeconds, Integer blockFor) {
        if (blockFor == null) return; // Avoid setting TTL if blocking is disabled

        Duration remainingWindow = Duration.ofSeconds(windowSeconds);
        Duration blockDuration = Duration.ofSeconds(blockFor);

        storage.setTtlByKey(rawKey, remainingWindow.plus(blockDuration).plusSeconds(1));
    }
}
