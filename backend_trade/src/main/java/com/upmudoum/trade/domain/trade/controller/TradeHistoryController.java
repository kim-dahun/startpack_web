package com.upmudoum.trade.domain.trade.controller;

import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import com.upmudoum.trade.domain.trade.dto.CreateDryRunTradeRequest;
import com.upmudoum.trade.domain.trade.dto.TradeHistoryDto;
import com.upmudoum.trade.domain.trade.service.TradeHistoryService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trade/histories")
public class TradeHistoryController {

    private final TradeHistoryService tradeHistoryService;

    public TradeHistoryController(TradeHistoryService tradeHistoryService) {
        this.tradeHistoryService = tradeHistoryService;
    }

    @GetMapping
    public List<TradeHistoryDto> histories(
            @RequestParam String accountNo,
            @RequestParam(defaultValue = "LIVE") KisTradeMode tradeMode
    ) {
        return tradeHistoryService.findByAccountNo(accountNo, tradeMode);
    }

    @PostMapping("/dry-run")
    @ResponseStatus(HttpStatus.CREATED)
    public TradeHistoryDto dryRun(@Valid @RequestBody CreateDryRunTradeRequest request) {
        return tradeHistoryService.createDryRun(request);
    }
}
