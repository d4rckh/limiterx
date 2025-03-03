package io.github.d4rckh.limiterx.core.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class ClientStatsTest {

    @Test
    void increaseTotalRequests_shouldIncreaseTotalRequestsBy1() {
        ClientStats stats = new ClientStats();
        stats.increaseTotalRequests();

        assertThat(stats.getTotalRequests()).isEqualTo(1);
    }

}
