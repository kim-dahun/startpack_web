package com.upmudoum.trade.domain.marketdata.repository;

import com.upmudoum.trade.domain.marketdata.entity.RealtimeReconnectHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RealtimeReconnectHistoryRepository extends JpaRepository<RealtimeReconnectHistory, Long> {

    List<RealtimeReconnectHistory> findTop50ByOrderByAttemptedAtDesc();
}
