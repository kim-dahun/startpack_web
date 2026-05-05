package com.upmudoum.trade.domain.marketdata.service;

import com.upmudoum.trade.domain.marketdata.dto.RealtimeReconnectHistoryDto;
import com.upmudoum.trade.domain.marketdata.entity.RealtimeReconnectHistory;
import com.upmudoum.trade.domain.marketdata.querydsl.RealtimeReconnectHistoryQueryRepository;
import com.upmudoum.trade.domain.marketdata.repository.RealtimeReconnectHistoryRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RealtimeReconnectHistoryService {

    private final RealtimeReconnectHistoryRepository repository;
    private final RealtimeReconnectHistoryQueryRepository queryRepository;

    public RealtimeReconnectHistoryService(
            RealtimeReconnectHistoryRepository repository,
            RealtimeReconnectHistoryQueryRepository queryRepository
    ) {
        this.repository = repository;
        this.queryRepository = queryRepository;
    }

    @Transactional
    public void saveSuccess(int subscriptionCount) {
        save(true, subscriptionCount, null);
    }

    @Transactional
    public void saveFailure(int subscriptionCount, Exception exception) {
        save(false, subscriptionCount, exception == null ? null : exception.getMessage());
    }

    @Transactional(readOnly = true)
    public List<RealtimeReconnectHistoryDto> findRecent() {
        return findRecent(null, null, null, 50);
    }

    @Transactional(readOnly = true)
    public List<RealtimeReconnectHistoryDto> findRecent(Boolean success, Instant from, Instant to, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 200));
        return queryRepository.findRecent(success, from, to, safeLimit);
    }

    private void save(boolean success, int subscriptionCount, String failureReason) {
        RealtimeReconnectHistory history = new RealtimeReconnectHistory();
        history.setAttemptedAt(Instant.now());
        history.setSuccess(success);
        history.setSubscriptionCount(subscriptionCount);
        history.setFailureReason(limit(failureReason));
        repository.save(history);
    }

    private String limit(String value) {
        if (value == null || value.length() <= 1000) {
            return value;
        }
        return value.substring(0, 1000);
    }

    private RealtimeReconnectHistoryDto toDto(RealtimeReconnectHistory history) {
        RealtimeReconnectHistoryDto dto = new RealtimeReconnectHistoryDto();
        dto.setId(history.getId());
        dto.setAttemptedAt(history.getAttemptedAt());
        dto.setSuccess(history.isSuccess());
        dto.setSubscriptionCount(history.getSubscriptionCount());
        dto.setFailureReason(history.getFailureReason());
        return dto;
    }
}
