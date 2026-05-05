package com.upmudoum.trade.domain.marketdata.controller;

import com.upmudoum.trade.domain.marketdata.dto.PublishRealtimeEventRequest;
import com.upmudoum.trade.domain.marketdata.dto.RealtimeReconnectHistoryDto;
import com.upmudoum.trade.domain.marketdata.dto.TradeRealtimeEventDto;
import com.upmudoum.trade.domain.marketdata.dto.TradeRealtimeStatusDto;
import com.upmudoum.trade.domain.marketdata.service.RealtimeReconnectHistoryService;
import com.upmudoum.trade.domain.marketdata.service.TradeRealtimeService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trade/realtime")
public class TradeRealtimeController {

    private final TradeRealtimeService realtimeService;
    private final RealtimeReconnectHistoryService reconnectHistoryService;

    public TradeRealtimeController(
            TradeRealtimeService realtimeService,
            RealtimeReconnectHistoryService reconnectHistoryService
    ) {
        this.realtimeService = realtimeService;
        this.reconnectHistoryService = reconnectHistoryService;
    }

    @GetMapping("/status")
    public TradeRealtimeStatusDto status() {
        return realtimeService.status();
    }

    @PostMapping("/heartbeat")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void heartbeat() {
        realtimeService.heartbeat();
    }

    @GetMapping("/reconnect-histories")
    public List<RealtimeReconnectHistoryDto> reconnectHistories(
            @RequestParam(required = false) Boolean success,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(defaultValue = "50") int limit
    ) {
        return reconnectHistoryService.findRecent(success, from, to, limit);
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void publishEvent(@Valid @RequestBody PublishRealtimeEventRequest request) {
        TradeRealtimeEventDto event = new TradeRealtimeEventDto();
        event.setType(request.getType());
        event.setItemCode(request.getItemCode());
        event.setOccurredAt(Instant.now());
        event.setPayload(request.getPayload() == null ? Map.of() : request.getPayload());
        realtimeService.publish(event);
    }
}
