package com.upmudoum.trade.domain.marketdata.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upmudoum.trade.domain.marketdata.dto.TradeRealtimeEventDto;
import com.upmudoum.trade.domain.marketdata.entity.RealtimeReceiveLog;
import com.upmudoum.trade.domain.marketdata.repository.RealtimeReceiveLogRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RealtimeReceiveLogService {

    private final RealtimeReceiveLogRepository repository;
    private final ObjectMapper objectMapper;

    public RealtimeReceiveLogService(RealtimeReceiveLogRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void save(TradeRealtimeEventDto event) {
        RealtimeReceiveLog log = new RealtimeReceiveLog();
        log.setType(event.getType());
        log.setItemCode(event.getItemCode());
        log.setOccurredAt(event.getOccurredAt());
        log.setPayloadJson(toJson(event.getPayload()));
        log.setReceivedAt(Instant.now());
        repository.save(log);
    }

    private String toJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload == null ? java.util.Map.of() : payload);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("failed to serialize realtime payload", ex);
        }
    }
}
