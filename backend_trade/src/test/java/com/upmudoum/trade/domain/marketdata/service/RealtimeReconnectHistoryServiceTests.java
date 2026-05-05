package com.upmudoum.trade.domain.marketdata.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.upmudoum.trade.config.QuerydslConfig;
import com.upmudoum.trade.domain.marketdata.entity.RealtimeReconnectHistory;
import com.upmudoum.trade.domain.marketdata.querydsl.RealtimeReconnectHistoryQueryRepository;
import com.upmudoum.trade.domain.marketdata.repository.RealtimeReconnectHistoryRepository;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({QuerydslConfig.class, RealtimeReconnectHistoryQueryRepository.class})
class RealtimeReconnectHistoryServiceTests {

    @Autowired
    private RealtimeReconnectHistoryRepository repository;

    @Autowired
    private RealtimeReconnectHistoryQueryRepository queryRepository;

    @Test
    void findRecentFiltersBySuccessAndTimeRangeAndLimit() {
        RealtimeReconnectHistoryService service = new RealtimeReconnectHistoryService(repository, queryRepository);
        Instant now = Instant.now();
        save(now.minusSeconds(60), true);
        save(now.minusSeconds(30), false);
        save(now.minusSeconds(10), true);

        assertThat(service.findRecent(true, now.minusSeconds(20), now.plusSeconds(1), 10))
                .hasSize(1)
                .first()
                .extracting("success")
                .isEqualTo(true);
    }

    @Test
    void findRecentCapsLimitToTwoHundred() {
        RealtimeReconnectHistoryService service = new RealtimeReconnectHistoryService(repository, queryRepository);
        Instant now = Instant.now();
        save(now, true);

        assertThat(service.findRecent(null, null, null, 1000)).hasSize(1);
    }

    @Test
    void findRecentAllowsAllOptionalFiltersToBeNull() {
        RealtimeReconnectHistoryService service = new RealtimeReconnectHistoryService(repository, queryRepository);
        Instant now = Instant.now();
        save(now.minusSeconds(60), true);
        save(now, false);

        assertThat(service.findRecent(null, null, null, 10))
                .hasSize(2)
                .first()
                .extracting("success")
                .isEqualTo(false);
    }

    private void save(Instant attemptedAt, boolean success) {
        RealtimeReconnectHistory history = new RealtimeReconnectHistory();
        history.setAttemptedAt(attemptedAt);
        history.setSuccess(success);
        history.setSubscriptionCount(1);
        repository.save(history);
    }
}
