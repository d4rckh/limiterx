package org.d4rckh.limiterx.core.common;

import org.d4rckh.limiterx.core.domain.ClientStats;

import java.util.Optional;

public interface ILimiterStorage {

    Optional<ClientStats> findByKey(String key);
    void updateByKey(String key, ClientStats stats);

}
