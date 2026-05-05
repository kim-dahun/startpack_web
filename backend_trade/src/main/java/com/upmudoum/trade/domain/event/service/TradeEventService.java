package com.upmudoum.trade.domain.event.service;

import com.upmudoum.trade.domain.event.dto.TradeEventDto;
import com.upmudoum.trade.domain.event.querydsl.TradeEventQueryRepository;
import com.upmudoum.trade.domain.event.vo.TradeEventType;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TradeEventService {

    private final TradeEventQueryRepository queryRepository;

    public TradeEventService(TradeEventQueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    public List<TradeEventDto> find(TradeEventType eventType, LocalDate from, LocalDate to) {
        LocalDate start = from == null ? LocalDate.now().minusMonths(3) : from;
        LocalDate end = to == null ? LocalDate.now().plusMonths(6) : to;
        return queryRepository.find(eventType, start, end, 100);
    }
}
