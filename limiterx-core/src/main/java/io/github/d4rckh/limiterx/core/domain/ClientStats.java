package io.github.d4rckh.limiterx.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientStats implements Serializable {
    private long totalRequests;

    private Instant blockedAt;
    private Instant lastReset;

    public void increaseTotalRequests() {
        totalRequests++;
    }
}
