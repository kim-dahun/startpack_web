package com.upmudoum.trade.domain.event.repository;

import com.upmudoum.trade.domain.event.entity.TradeEvent;
import com.upmudoum.trade.domain.event.vo.TradeEventType;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeEventRepository extends JpaRepository<TradeEvent, Long> {

    List<TradeEvent> findTop100ByEventTypeAndEventDateBetweenOrderByEventDateDesc(TradeEventType eventType, LocalDate from, LocalDate to);

    List<TradeEvent> findTop100ByEventDateBetweenOrderByEventDateDesc(LocalDate from, LocalDate to);
}
