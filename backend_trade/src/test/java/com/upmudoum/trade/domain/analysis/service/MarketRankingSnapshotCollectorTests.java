package com.upmudoum.trade.domain.analysis.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.upmudoum.trade.config.QuerydslConfig;
import com.upmudoum.trade.domain.analysis.querydsl.MarketRankingSnapshotQueryRepository;
import com.upmudoum.trade.domain.analysis.repository.MarketRankingSnapshotRepository;
import com.upmudoum.trade.domain.item.querydsl.ItemMasterQueryRepository;
import com.upmudoum.trade.domain.kis.infra.KisQueryFactory;
import com.upmudoum.trade.domain.kis.infra.KisRestClient;
import com.upmudoum.trade.domain.kis.vo.KisEndpoint;
import com.upmudoum.trade.domain.kis.vo.KisProperties;
import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({
        QuerydslConfig.class,
        ItemMasterQueryRepository.class,
        MarketRankingSnapshotQueryRepository.class,
        MarketAnalysisService.class,
        MarketRankingSnapshotCollector.class,
        MarketRankingSnapshotCollectorTests.Config.class
})
class MarketRankingSnapshotCollectorTests {

    @Autowired
    private MarketRankingSnapshotCollector collector;

    @Autowired
    private MarketRankingSnapshotRepository repository;

    @Test
    void collectTodayStoresSnapshotsFromKisRankingOutputs() {
        int savedCount = collector.collectToday(KisTradeMode.LIVE);

        assertThat(savedCount).isGreaterThanOrEqualTo(1);
        assertThat(repository.existsByBaseDate(LocalDate.now())).isTrue();
        assertThat(repository.findByBaseDateOrderByMarketCapDesc(LocalDate.now()).get(0).getItemCode()).isEqualTo("005930");
    }

    @TestConfiguration
    static class Config {

        @Bean
        KisRestClient kisRestClient() {
            return new TestKisRestClient();
        }

        @Bean
        KisQueryFactory kisQueryFactory() {
            return new KisQueryFactory(new KisProperties("app-key", "app-secret", "http://paper", "http://live", "ws://paper", "ws://live", "01"));
        }
    }

    private static class TestKisRestClient implements KisRestClient {

        @Override
        public Map<String, Object> get(String endpoint, Map<String, String> query, KisTradeMode tradeMode) {
            return Map.of();
        }

        @Override
        public Map<String, Object> get(KisEndpoint endpoint, Map<String, String> query, KisTradeMode tradeMode) {
            Map<String, Object> row = new HashMap<>();
            row.put("mksc_shrn_iscd", "005930");
            row.put("hts_kor_isnm", "삼성전자");
            row.put("stck_prpr", "72000");
            row.put("prdy_vrss", "1000");
            row.put("prdy_ctrt", "1.41");
            row.put("acml_vol", "1234567");
            row.put("acml_tr_pbmn", "88888888888");
            row.put("stck_avls", "430000000000000");
            return Map.of("output", List.of(row));
        }

        @Override
        public Map<String, Object> post(KisEndpoint endpoint, Map<String, String> body, KisTradeMode tradeMode) {
            return Map.of();
        }
    }
}
