package com.upmudoum.trade.domain.performance.controller;

import com.upmudoum.trade.domain.performance.dto.PerformanceHistoryDto;
import com.upmudoum.trade.domain.performance.service.PerformanceHistoryService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trade/performance/histories")
public class PerformanceHistoryController {

    private final PerformanceHistoryService service;

    public PerformanceHistoryController(PerformanceHistoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<PerformanceHistoryDto> histories(
            @RequestParam String accountNo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return service.findHistories(accountNo, from, to);
    }
}
