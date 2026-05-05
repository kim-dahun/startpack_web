package com.upmudoum.trade.domain.event.controller;

import com.upmudoum.trade.domain.event.dto.TradeEventDto;
import com.upmudoum.trade.domain.event.service.TradeEventService;
import com.upmudoum.trade.domain.event.vo.TradeEventType;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trade/events")
public class TradeEventController {

    private final TradeEventService service;

    public TradeEventController(TradeEventService service) {
        this.service = service;
    }

    @GetMapping
    public List<TradeEventDto> events(
            @RequestParam(required = false) TradeEventType eventType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return service.find(eventType, from, to);
    }

    @GetMapping("/ipo-subscriptions")
    public List<TradeEventDto> ipo(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.find(TradeEventType.IPO_SUBSCRIPTION, from, to);
    }

    @GetMapping("/par-value-changes")
    public List<TradeEventDto> parValue(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.find(TradeEventType.PAR_VALUE_CHANGE, from, to);
    }

    @GetMapping("/corporate-actions")
    public List<TradeEventDto> corporateActions(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.find(TradeEventType.CORPORATE_ACTION, from, to);
    }
}
