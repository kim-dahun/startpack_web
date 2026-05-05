package com.upmudoum.trade.domain.account.repository;

import com.upmudoum.trade.domain.account.entity.DailyBalanceSnapshot;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyBalanceSnapshotRepository extends JpaRepository<DailyBalanceSnapshot, Long> {

    List<DailyBalanceSnapshot> findByAccountNoAndBaseDateBetweenOrderByBaseDateAsc(String accountNo, LocalDate from, LocalDate to);
}
