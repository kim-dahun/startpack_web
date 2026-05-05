package com.upmudoum.trade.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.upmudoum.trade.domain.account.entity.AccountSnapshot;
import com.upmudoum.trade.domain.account.entity.AccountPositionSnapshot;
import com.upmudoum.trade.domain.account.entity.DailyBalanceSnapshot;
import com.upmudoum.trade.domain.account.entity.RegisteredAccount;
import com.upmudoum.trade.domain.account.repository.AccountPositionSnapshotRepository;
import com.upmudoum.trade.domain.account.repository.AccountSnapshotRepository;
import com.upmudoum.trade.domain.account.repository.DailyBalanceSnapshotRepository;
import com.upmudoum.trade.domain.account.repository.RegisteredAccountRepository;
import com.upmudoum.trade.domain.analysis.entity.MarketRankingSnapshot;
import com.upmudoum.trade.domain.analysis.repository.MarketRankingSnapshotRepository;
import com.upmudoum.trade.domain.chart.entity.ChartDrawing;
import com.upmudoum.trade.domain.chart.repository.ChartDrawingRepository;
import com.upmudoum.trade.domain.chart.vo.ChartDrawingType;
import com.upmudoum.trade.domain.event.entity.TradeEvent;
import com.upmudoum.trade.domain.event.repository.TradeEventRepository;
import com.upmudoum.trade.domain.event.vo.TradeEventType;
import com.upmudoum.trade.domain.item.entity.FrequentSearchItem;
import com.upmudoum.trade.domain.item.entity.ItemMaster;
import com.upmudoum.trade.domain.item.repository.FrequentSearchItemRepository;
import com.upmudoum.trade.domain.item.repository.ItemMasterRepository;
import com.upmudoum.trade.domain.kis.entity.KisApiCallLog;
import com.upmudoum.trade.domain.kis.entity.KisOpenApiToken;
import com.upmudoum.trade.domain.kis.repository.KisApiCallLogRepository;
import com.upmudoum.trade.domain.kis.repository.KisOpenApiTokenRepository;
import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import com.upmudoum.trade.domain.master.entity.TradeMasterImportHistory;
import com.upmudoum.trade.domain.master.entity.TradeMasterImportLock;
import com.upmudoum.trade.domain.master.repository.TradeMasterImportHistoryRepository;
import com.upmudoum.trade.domain.master.repository.TradeMasterImportLockRepository;
import com.upmudoum.trade.domain.master.vo.TradeMasterImportStatus;
import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import com.upmudoum.trade.domain.marketdata.entity.RealtimeReceiveLog;
import com.upmudoum.trade.domain.marketdata.entity.RealtimeReconnectHistory;
import com.upmudoum.trade.domain.marketdata.repository.RealtimeReceiveLogRepository;
import com.upmudoum.trade.domain.marketdata.repository.RealtimeReconnectHistoryRepository;
import com.upmudoum.trade.domain.marketdata.vo.TradeRealtimeEventType;
import com.upmudoum.trade.domain.trade.entity.TradeHistory;
import com.upmudoum.trade.domain.trade.repository.TradeHistoryRepository;
import com.upmudoum.trade.domain.trade.vo.TradeSide;
import com.upmudoum.trade.domain.watchlist.entity.WatchlistGroup;
import com.upmudoum.trade.domain.watchlist.entity.WatchlistItem;
import com.upmudoum.trade.domain.watchlist.repository.WatchlistGroupRepository;
import com.upmudoum.trade.domain.watchlist.repository.WatchlistRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class JpaRepositoryIntegrationTests {

    @Autowired
    private AccountSnapshotRepository accountSnapshotRepository;

    @Autowired
    private AccountPositionSnapshotRepository accountPositionSnapshotRepository;

    @Autowired
    private RegisteredAccountRepository registeredAccountRepository;

    @Autowired
    private DailyBalanceSnapshotRepository dailyBalanceSnapshotRepository;

    @Autowired
    private ItemMasterRepository itemMasterRepository;

    @Autowired
    private FrequentSearchItemRepository frequentSearchItemRepository;

    @Autowired
    private ChartDrawingRepository chartDrawingRepository;

    @Autowired
    private TradeEventRepository tradeEventRepository;

    @Autowired
    private TradeMasterImportHistoryRepository tradeMasterImportHistoryRepository;

    @Autowired
    private TradeMasterImportLockRepository tradeMasterImportLockRepository;

    @Autowired
    private MarketRankingSnapshotRepository marketRankingSnapshotRepository;

    @Autowired
    private WatchlistRepository watchlistRepository;

    @Autowired
    private WatchlistGroupRepository watchlistGroupRepository;

    @Autowired
    private TradeHistoryRepository tradeHistoryRepository;

    @Autowired
    private KisApiCallLogRepository kisApiCallLogRepository;

    @Autowired
    private KisOpenApiTokenRepository kisOpenApiTokenRepository;

    @Autowired
    private RealtimeReceiveLogRepository realtimeReceiveLogRepository;

    @Autowired
    private RealtimeReconnectHistoryRepository realtimeReconnectHistoryRepository;

    @Test
    void repositoriesPersistTradeCoreEntities() {
        accountSnapshotRepository.save(accountSnapshot());
        accountPositionSnapshotRepository.save(accountPositionSnapshot());
        registeredAccountRepository.save(registeredAccount());
        dailyBalanceSnapshotRepository.save(dailyBalanceSnapshot());
        itemMasterRepository.save(itemMaster());
        frequentSearchItemRepository.save(frequentSearchItem());
        chartDrawingRepository.save(chartDrawing());
        tradeEventRepository.save(tradeEvent());
        tradeMasterImportHistoryRepository.save(tradeMasterImportHistory());
        tradeMasterImportLockRepository.save(tradeMasterImportLock());
        marketRankingSnapshotRepository.save(marketRankingSnapshot());
        watchlistGroupRepository.save(watchlistGroup());
        watchlistRepository.save(watchlistItem());
        tradeHistoryRepository.save(tradeHistory());
        kisApiCallLogRepository.save(kisApiCallLog());
        kisOpenApiTokenRepository.save(kisOpenApiToken());
        realtimeReceiveLogRepository.save(realtimeReceiveLog());
        realtimeReconnectHistoryRepository.save(realtimeReconnectHistory());

        assertThat(accountSnapshotRepository.count()).isEqualTo(1);
        assertThat(accountPositionSnapshotRepository.findTopByAccountNoAndItemCodeOrderByCapturedAtDesc("12345678", "005930")).isPresent();
        assertThat(registeredAccountRepository.findByAccountNo("12345678-01")).isPresent();
        assertThat(dailyBalanceSnapshotRepository.count()).isEqualTo(1);
        assertThat(itemMasterRepository.findByItemCode("005930")).isPresent();
        assertThat(frequentSearchItemRepository.findTop20ByUserIdOrderBySearchCountDescLastSearchedAtDesc("user-1")).hasSize(1);
        assertThat(chartDrawingRepository.findByUserIdAndItemCodeOrderByUpdatedAtDesc("user-1", "005930")).hasSize(1);
        assertThat(tradeEventRepository.findTop100ByEventDateBetweenOrderByEventDateDesc(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31))).hasSize(1);
        assertThat(tradeMasterImportHistoryRepository.findTop50ByMasterTypeOrderByStartedAtDesc(TradeMasterType.KOSPI)).hasSize(1);
        assertThat(tradeMasterImportLockRepository.findByMasterTypeForUpdate(TradeMasterType.KOSPI)).isPresent();
        assertThat(marketRankingSnapshotRepository.existsByBaseDate(LocalDate.of(2026, 5, 1))).isTrue();
        assertThat(watchlistGroupRepository.findByUserIdOrderByCreatedAtDesc("user-1")).hasSize(1);
        assertThat(watchlistRepository.findByUserIdAndItemCode("user-1", "005930")).isPresent();
        assertThat(tradeHistoryRepository.findByIdempotencyKey("dry-run-1")).isPresent();
        assertThat(kisApiCallLogRepository.findTop50ByOrderByCalledAtDesc()).hasSize(1);
        assertThat(kisOpenApiTokenRepository.findByTradeMode(KisTradeMode.PAPER)).isPresent();
        assertThat(realtimeReceiveLogRepository.count()).isEqualTo(1);
        assertThat(realtimeReconnectHistoryRepository.findTop50ByOrderByAttemptedAtDesc()).hasSize(1);
    }

    private AccountSnapshot accountSnapshot() {
        AccountSnapshot snapshot = new AccountSnapshot();
        snapshot.setAccountNo("12345678");
        snapshot.setAccountName("테스트 계좌");
        snapshot.setTotalAssetAmount(BigDecimal.TEN);
        snapshot.setCashAmount(BigDecimal.ONE);
        snapshot.setCapturedAt(Instant.now());
        return snapshot;
    }

    private DailyBalanceSnapshot dailyBalanceSnapshot() {
        DailyBalanceSnapshot snapshot = new DailyBalanceSnapshot();
        snapshot.setAccountNo("12345678");
        snapshot.setBaseDate(LocalDate.of(2026, 5, 1));
        snapshot.setTotalAssetAmount(BigDecimal.TEN);
        snapshot.setProfitLossAmount(BigDecimal.ONE);
        snapshot.setCapturedAt(Instant.now());
        return snapshot;
    }

    private AccountPositionSnapshot accountPositionSnapshot() {
        AccountPositionSnapshot snapshot = new AccountPositionSnapshot();
        snapshot.setAccountNo("12345678");
        snapshot.setItemCode("005930");
        snapshot.setItemName("Samsung Electronics");
        snapshot.setQuantity(10);
        snapshot.setOrderableQuantity(7);
        snapshot.setAveragePrice(BigDecimal.TEN);
        snapshot.setCurrentPrice(BigDecimal.TEN);
        snapshot.setEvaluationAmount(BigDecimal.TEN);
        snapshot.setProfitLossAmount(BigDecimal.ZERO);
        snapshot.setProfitLossRate(BigDecimal.ZERO);
        snapshot.setCapturedAt(Instant.now());
        return snapshot;
    }

    private RegisteredAccount registeredAccount() {
        RegisteredAccount account = new RegisteredAccount();
        account.setAccountNo("12345678-01");
        account.setAccountName("main");
        account.setProductCode("01");
        account.setAliasName("main");
        account.setMemo("registered");
        account.setActive(true);
        account.setCreatedAt(Instant.now());
        account.setUpdatedAt(Instant.now());
        return account;
    }

    private ItemMaster itemMaster() {
        ItemMaster item = new ItemMaster();
        item.setMasterType(TradeMasterType.KOSPI);
        item.setItemCode("005930");
        item.setItemName("삼성전자");
        item.setMarketCode("KOSPI");
        item.setSyncedAt(Instant.now());
        return item;
    }

    private ChartDrawing chartDrawing() {
        ChartDrawing drawing = new ChartDrawing();
        drawing.setUserId("user-1");
        drawing.setItemCode("005930");
        drawing.setDrawingType(ChartDrawingType.UPPER_LINE);
        drawing.setStartDate(LocalDate.of(2026, 5, 1));
        drawing.setStartPrice(BigDecimal.TEN);
        drawing.setEndDate(LocalDate.of(2026, 5, 2));
        drawing.setEndPrice(BigDecimal.TEN);
        drawing.setCreatedAt(Instant.now());
        drawing.setUpdatedAt(Instant.now());
        return drawing;
    }

    private TradeEvent tradeEvent() {
        TradeEvent event = new TradeEvent();
        event.setEventType(TradeEventType.IPO_SUBSCRIPTION);
        event.setItemCode("005930");
        event.setTitle("IPO");
        event.setEventDate(LocalDate.of(2026, 5, 1));
        event.setCreatedAt(Instant.now());
        return event;
    }

    private TradeMasterImportHistory tradeMasterImportHistory() {
        TradeMasterImportHistory history = new TradeMasterImportHistory();
        history.setMasterType(TradeMasterType.KOSPI);
        history.setSourceFileName("kospi.csv");
        history.setSourceVersion("20260503");
        history.setImportedCount(1);
        history.setStartedAt(Instant.now());
        history.setFinishedAt(Instant.now());
        history.setSuccess(true);
        return history;
    }

    private TradeMasterImportLock tradeMasterImportLock() {
        TradeMasterImportLock lock = new TradeMasterImportLock();
        lock.setMasterType(TradeMasterType.KOSPI);
        lock.setImportStatus(TradeMasterImportStatus.SUCCESS);
        lock.setHistoryId(1L);
        lock.setLastRequestedAt(Instant.now());
        lock.setUpdatedAt(Instant.now());
        return lock;
    }

    private MarketRankingSnapshot marketRankingSnapshot() {
        MarketRankingSnapshot snapshot = new MarketRankingSnapshot();
        snapshot.setBaseDate(LocalDate.of(2026, 5, 1));
        snapshot.setMasterType(TradeMasterType.KOSPI);
        snapshot.setItemCode("005930");
        snapshot.setItemName("삼성전자");
        snapshot.setMarketCode("KOSPI");
        snapshot.setCountryCode("KR");
        snapshot.setSectorName("semiconductor");
        snapshot.setMarketCap(BigDecimal.TEN);
        snapshot.setVolume(BigDecimal.ONE);
        snapshot.setTurnover(BigDecimal.TEN);
        snapshot.setChangeRate(BigDecimal.ONE);
        snapshot.setCapturedAt(Instant.now());
        return snapshot;
    }

    private FrequentSearchItem frequentSearchItem() {
        FrequentSearchItem item = new FrequentSearchItem();
        item.setUserId("user-1");
        item.setItemCode("005930");
        item.setItemName("삼성전자");
        item.setMarketCode("KOSPI");
        item.setSearchCount(1);
        item.setLastSearchedAt(Instant.now());
        return item;
    }

    private WatchlistItem watchlistItem() {
        WatchlistItem item = new WatchlistItem();
        item.setUserId("user-1");
        item.setItemCode("005930");
        item.setItemName("삼성전자");
        item.setCreatedAt(Instant.now());
        return item;
    }

    private TradeHistory tradeHistory() {
        TradeHistory history = new TradeHistory();
        history.setAccountNo("12345678");
        history.setItemCode("005930");
        history.setItemName("삼성전자");
        history.setSide(TradeSide.BUY);
        history.setQuantity(1);
        history.setPrice(BigDecimal.TEN);
        history.setAmount(BigDecimal.TEN);
        history.setIdempotencyKey("dry-run-1");
        history.setTradedAt(Instant.now());
        return history;
    }

    private WatchlistGroup watchlistGroup() {
        WatchlistGroup group = new WatchlistGroup();
        group.setUserId("user-1");
        group.setGroupName("main");
        group.setCreatedAt(Instant.now());
        return group;
    }

    private KisApiCallLog kisApiCallLog() {
        KisApiCallLog log = new KisApiCallLog();
        log.setMethod("GET");
        log.setEndpoint("/test");
        log.setStatusCode(200);
        log.setElapsedMillis(1);
        log.setCalledAt(Instant.now());
        return log;
    }

    private KisOpenApiToken kisOpenApiToken() {
        KisOpenApiToken token = new KisOpenApiToken();
        token.setTradeMode(KisTradeMode.PAPER);
        token.setAccessToken("token");
        token.setIssuedAt(Instant.now());
        token.setUpdatedAt(Instant.now());
        token.setExpiresAt(Instant.now().plusSeconds(3600));
        return token;
    }

    private RealtimeReceiveLog realtimeReceiveLog() {
        RealtimeReceiveLog log = new RealtimeReceiveLog();
        log.setType(TradeRealtimeEventType.PRICE);
        log.setItemCode("005930");
        log.setOccurredAt(Instant.now());
        log.setPayloadJson("{}");
        log.setReceivedAt(Instant.now());
        return log;
    }

    private RealtimeReconnectHistory realtimeReconnectHistory() {
        RealtimeReconnectHistory history = new RealtimeReconnectHistory();
        history.setAttemptedAt(Instant.now());
        history.setSuccess(true);
        history.setSubscriptionCount(1);
        return history;
    }
}
