package com.upmudoum.trade.domain.trade.repository;

import com.upmudoum.trade.domain.trade.entity.TradeHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeHistoryRepository extends JpaRepository<TradeHistory, Long> {

    List<TradeHistory> findByAccountNoOrderByTradedAtDesc(String accountNo);

    Optional<TradeHistory> findByIdempotencyKey(String idempotencyKey);
}
