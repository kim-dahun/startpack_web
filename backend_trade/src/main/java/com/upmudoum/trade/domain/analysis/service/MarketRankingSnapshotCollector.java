package com.upmudoum.trade.domain.analysis.service;

import com.upmudoum.trade.domain.analysis.dto.MarketRankingSnapshotDto;
import com.upmudoum.trade.domain.analysis.dto.SaveMarketRankingSnapshotsRequest;
import com.upmudoum.trade.domain.kis.infra.KisQueryFactory;
import com.upmudoum.trade.domain.kis.infra.KisResponseExtractor;
import com.upmudoum.trade.domain.kis.infra.KisRestClient;
import com.upmudoum.trade.domain.kis.vo.KisEndpoint;
import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class MarketRankingSnapshotCollector {

    private final KisRestClient kisRestClient;
    private final KisQueryFactory kisQueryFactory;
    private final MarketAnalysisService marketAnalysisService;

    public MarketRankingSnapshotCollector(
            KisRestClient kisRestClient,
            KisQueryFactory kisQueryFactory,
            MarketAnalysisService marketAnalysisService
    ) {
        this.kisRestClient = kisRestClient;
        this.kisQueryFactory = kisQueryFactory;
        this.marketAnalysisService = marketAnalysisService;
    }

    public int collectToday(KisTradeMode tradeMode) {
        LocalDate baseDate = LocalDate.now();
        Map<String, MarketRankingSnapshotDto> snapshots = new LinkedHashMap<>();
        collectMarket("KOSPI", tradeMode, snapshots);
        collectMarket("KOSDAQ", tradeMode, snapshots);
        SaveMarketRankingSnapshotsRequest request = new SaveMarketRankingSnapshotsRequest();
        request.setBaseDate(baseDate);
        request.setSnapshots(snapshots.values().stream().toList());
        return marketAnalysisService.replaceSnapshots(request);
    }

    private void collectMarket(String marketCode, KisTradeMode tradeMode, Map<String, MarketRankingSnapshotDto> snapshots) {
        mergeRows(snapshots, marketCode, kisRestClient.get(
                KisEndpoint.RANKING_EXP_TRANS_UPDOWN,
                kisQueryFactory.rankingExpTransUpdown(marketCode, "0"),
                tradeMode
        ), "gainers");
        mergeRows(snapshots, marketCode, kisRestClient.get(
                KisEndpoint.RANKING_EXP_TRANS_UPDOWN,
                kisQueryFactory.rankingExpTransUpdown(marketCode, "3"),
                tradeMode
        ), "losers");
        mergeRows(snapshots, marketCode, kisRestClient.get(
                KisEndpoint.RANKING_EXP_TRANS_UPDOWN,
                kisQueryFactory.rankingExpTransUpdown(marketCode, "5"),
                tradeMode
        ), "volume");
        mergeRows(snapshots, marketCode, kisRestClient.get(
                KisEndpoint.RANKING_EXP_TRANS_UPDOWN,
                kisQueryFactory.rankingExpTransUpdown(marketCode, "6"),
                tradeMode
        ), "turnover");
        mergeRows(snapshots, marketCode, kisRestClient.get(
                KisEndpoint.RANKING_MARKET_VALUE,
                kisQueryFactory.rankingMarketValue(marketCode, "23"),
                tradeMode
        ), "market-cap");
    }

    private void mergeRows(Map<String, MarketRankingSnapshotDto> snapshots, String marketCode, Map<String, Object> response, String source) {
        rows(response).forEach(row -> {
            String itemCode = KisResponseExtractor.text(row, "mksc_shrn_iscd", "stck_shrn_iscd", "pdno", "itemCode");
            if (itemCode.isBlank()) {
                return;
            }
            MarketRankingSnapshotDto snapshot = snapshots.computeIfAbsent(itemCode, code -> baseSnapshot(code, marketCode, row));
            snapshot.setCurrentPrice(firstNonZero(snapshot.getCurrentPrice(), KisResponseExtractor.decimal(row, "stck_prpr", "currentPrice")));
            snapshot.setChangeAmount(firstNonZero(snapshot.getChangeAmount(), KisResponseExtractor.decimal(row, "prdy_vrss", "changeAmount")));
            snapshot.setChangeRate(firstNonZero(snapshot.getChangeRate(), KisResponseExtractor.decimal(row, "prdy_ctrt", "changeRate")));
            if ("volume".equals(source)) {
                snapshot.setVolume(firstNonZero(snapshot.getVolume(), KisResponseExtractor.decimal(row, "acml_vol", "vol", "volume")));
            } else {
                snapshot.setVolume(firstNonZero(snapshot.getVolume(), KisResponseExtractor.decimal(row, "acml_vol", "vol", "volume")));
            }
            if ("turnover".equals(source)) {
                snapshot.setTurnover(firstNonZero(snapshot.getTurnover(), KisResponseExtractor.decimal(row, "acml_tr_pbmn", "tr_pbmn", "turnover")));
            } else {
                snapshot.setTurnover(firstNonZero(snapshot.getTurnover(), KisResponseExtractor.decimal(row, "acml_tr_pbmn", "tr_pbmn", "turnover")));
            }
            snapshot.setMarketCap(firstNonZero(snapshot.getMarketCap(), KisResponseExtractor.decimal(row, "stck_avls", "avls", "marketCap")));
            snapshot.setHigh52WeekPrice(firstNonZero(snapshot.getHigh52WeekPrice(), KisResponseExtractor.decimal(row, "w52_hgpr", "high52WeekPrice")));
            snapshot.setLow52WeekPrice(firstNonZero(snapshot.getLow52WeekPrice(), KisResponseExtractor.decimal(row, "w52_lwpr", "low52WeekPrice")));
            snapshot.setVolatility(firstNonZero(snapshot.getVolatility(), snapshot.getChangeRate() == null ? BigDecimal.ZERO : snapshot.getChangeRate().abs()));
        });
    }

    private List<Map<String, Object>> rows(Map<String, Object> response) {
        List<Map<String, Object>> output = KisResponseExtractor.list(response, "output");
        if (!output.isEmpty()) {
            return output;
        }
        List<Map<String, Object>> output1 = KisResponseExtractor.list(response, "output1");
        if (!output1.isEmpty()) {
            return output1;
        }
        return KisResponseExtractor.list(response, "output2");
    }

    private MarketRankingSnapshotDto baseSnapshot(String itemCode, String marketCode, Map<String, Object> row) {
        MarketRankingSnapshotDto snapshot = new MarketRankingSnapshotDto();
        snapshot.setMasterType("KOSPI".equals(marketCode) ? TradeMasterType.KOSPI : TradeMasterType.KOSDAQ);
        snapshot.setItemCode(itemCode);
        snapshot.setItemName(KisResponseExtractor.text(row, "hts_kor_isnm", "itemName"));
        snapshot.setMarketCode(marketCode);
        snapshot.setCountryCode("KR");
        return snapshot;
    }

    private BigDecimal firstNonZero(BigDecimal current, BigDecimal next) {
        if (current != null && current.signum() != 0) {
            return current;
        }
        return next;
    }
}
