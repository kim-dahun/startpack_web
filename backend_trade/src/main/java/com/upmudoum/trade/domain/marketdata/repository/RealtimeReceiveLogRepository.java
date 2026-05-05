package com.upmudoum.trade.domain.marketdata.repository;

import com.upmudoum.trade.domain.marketdata.entity.RealtimeReceiveLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RealtimeReceiveLogRepository extends JpaRepository<RealtimeReceiveLog, Long> {
}
