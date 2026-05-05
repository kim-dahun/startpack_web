package com.upmudoum.trade.domain.performance.service;

import com.upmudoum.trade.domain.account.entity.DailyBalanceSnapshot;
import com.upmudoum.trade.domain.account.repository.DailyBalanceSnapshotRepository;
import com.upmudoum.trade.domain.performance.dto.PerformanceHistoryDto;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PerformanceHistoryService {

    private final DailyBalanceSnapshotRepository repository;

    public PerformanceHistoryService(DailyBalanceSnapshotRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<PerformanceHistoryDto> findHistories(String accountNo, LocalDate from, LocalDate to) {
        return repository.findByAccountNoAndBaseDateBetweenOrderByBaseDateAsc(accountNo, from, to).stream()
                .map(this::toDto)
                .toList();
    }

    private PerformanceHistoryDto toDto(DailyBalanceSnapshot snapshot) {
        PerformanceHistoryDto dto = new PerformanceHistoryDto();
        dto.setAccountNo(snapshot.getAccountNo());
        dto.setBaseDate(snapshot.getBaseDate());
        dto.setTotalAssetAmount(snapshot.getTotalAssetAmount());
        dto.setProfitLossAmount(snapshot.getProfitLossAmount());
        return dto;
    }
}
