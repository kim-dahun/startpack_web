package com.upmudoum.trade.domain.analysis.controller;

import com.upmudoum.trade.domain.analysis.dto.MarketGroupStrengthDto;
import com.upmudoum.trade.domain.analysis.dto.RankingItemDto;
import com.upmudoum.trade.domain.analysis.dto.SaveMarketRankingSnapshotsRequest;
import com.upmudoum.trade.domain.analysis.service.MarketAnalysisService;
import com.upmudoum.trade.domain.analysis.service.MarketRankingSnapshotCollector;
import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trade/analysis")
public class MarketAnalysisController {

    private final MarketAnalysisService service;
    private final MarketRankingSnapshotCollector collector;

    public MarketAnalysisController(MarketAnalysisService service, MarketRankingSnapshotCollector collector) {
        this.service = service;
        this.collector = collector;
    }

    @GetMapping("/rankings/{rankingType}")
    public List<RankingItemDto> rankings(
            @PathVariable String rankingType,
            @RequestParam(required = false) TradeMasterType masterType
    ) {
        return service.ranking(rankingType, masterType);
    }

    @GetMapping("/sectors")
    public List<MarketGroupStrengthDto> sectors() {
        return service.sectors();
    }

    @GetMapping("/themes")
    public List<MarketGroupStrengthDto> themes() {
        return service.themes();
    }

    @PostMapping("/ranking-snapshots")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    public java.util.Map<String, Integer> saveSnapshots(@Valid @RequestBody SaveMarketRankingSnapshotsRequest request) {
        return java.util.Map.of("savedCount", service.saveSnapshots(request));
    }

    @PostMapping("/ranking-snapshots/collect")
    public java.util.Map<String, Integer> collectSnapshots(@RequestParam(defaultValue = "LIVE") KisTradeMode tradeMode) {
        return java.util.Map.of("savedCount", collector.collectToday(tradeMode));
    }
}
