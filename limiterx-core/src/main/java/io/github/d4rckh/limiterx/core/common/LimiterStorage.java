package io.github.d4rckh.limiterx.core.common;

import io.github.d4rckh.limiterx.core.domain.ClientStats;

import java.util.Optional;

public interface LimiterStorage {

    Optional<ClientStats> findByKey(String key);
    void updateByKey(String key, ClientStats stats);

}
