package io.github.d4rckh.limiterx.core;

import io.github.d4rckh.limiterx.core.common.LimiterStorage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import io.github.d4rckh.limiterx.core.domain.Key;
import io.github.d4rckh.limiterx.core.domain.ClientStats;

import java.time.Instant;
import java.util.Objects;

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
                .orElse(new ClientStats(0, null, Instant.now()));

        // Resetting request window
        if (clientStats.getLastReset().plusSeconds(windowSeconds).isBefore(Instant.now())) {
            clientStats.setLastReset(Instant.now());
            clientStats.setTotalRequests(0);
        }
        clientStats.increaseTotalRequests();

        // Checking it's blocked using blockFor
        if (!Objects.isNull(clientStats.getBlockedAt()) &&
            !Objects.isNull(blockFor)

        ) {
            Instant blockedUntil = clientStats.getBlockedAt().plusSeconds(blockFor);

            if (blockedUntil.isAfter(Instant.now())) {
                storage.updateByKey(rawKey, clientStats);
                return true;
            }

            clientStats.setBlockedAt(null);
            storage.updateByKey(rawKey, clientStats);
        }

        // Checking if we should block it
        if (clientStats.getTotalRequests() > maximumRequests) {
            if (!Objects.isNull(blockFor)) {
                clientStats.setBlockedAt(Instant.now());
            }

            storage.updateByKey(rawKey, clientStats);
            return true;
        }

        storage.updateByKey(rawKey, clientStats);
        return false;
    }
}
