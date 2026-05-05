package com.upmudoum.trade.domain.workspace.service;

import com.upmudoum.trade.domain.account.dto.AccountBalanceDetailDto;
import com.upmudoum.trade.domain.account.dto.PositionDto;
import com.upmudoum.trade.domain.account.service.AccountService;
import com.upmudoum.trade.domain.chart.service.ChartDrawingService;
import com.upmudoum.trade.domain.item.dto.FrequentSearchItemDto;
import com.upmudoum.trade.domain.item.service.FrequentSearchItemService;
import com.upmudoum.trade.domain.item.service.ItemService;
import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import com.upmudoum.trade.domain.trade.dto.OrderableAmountRequest;
import com.upmudoum.trade.domain.trade.service.KisOrderService;
import com.upmudoum.trade.domain.watchlist.dto.WatchlistItemDto;
import com.upmudoum.trade.domain.watchlist.service.WatchlistService;
import com.upmudoum.trade.domain.workspace.dto.WorkspaceChartSnapshotDto;
import com.upmudoum.trade.domain.workspace.dto.WorkspaceSnapshotDto;
import com.upmudoum.trade.domain.workspace.dto.WorkspaceTradingSnapshotDto;
import com.upmudoum.trade.domain.workspace.vo.WorkspaceChartInterval;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceSnapshotService {

    private final ItemService itemService;
    private final WatchlistService watchlistService;
    private final FrequentSearchItemService frequentSearchItemService;
    private final ChartDrawingService chartDrawingService;
    private final AccountService accountService;
    private final KisOrderService kisOrderService;

    public WorkspaceSnapshotService(
            ItemService itemService,
            WatchlistService watchlistService,
            FrequentSearchItemService frequentSearchItemService,
            ChartDrawingService chartDrawingService,
            AccountService accountService,
            KisOrderService kisOrderService
    ) {
        this.itemService = itemService;
        this.watchlistService = watchlistService;
        this.frequentSearchItemService = frequentSearchItemService;
        this.chartDrawingService = chartDrawingService;
        this.accountService = accountService;
        this.kisOrderService = kisOrderService;
    }

    public WorkspaceSnapshotDto snapshot(String itemCode, String userId, KisTradeMode tradeMode) {
        WorkspaceSnapshotDto dto = new WorkspaceSnapshotDto();
        dto.setItemCode(itemCode);
        dto.setTradeMode(tradeMode);
        dto.setCapturedAt(Instant.now());
        dto.setQuote(itemService.findPrice(itemCode, tradeMode));
        dto.setOrderbook(itemService.findOrderbook(itemCode, tradeMode));
        dto.setMetrics(itemService.metrics(itemCode));
        dto.setWatchlistItems(watchlistItems(userId));
        dto.setFrequentSearchItems(frequentSearchItems(userId));
        return dto;
    }

    public WorkspaceChartSnapshotDto chartSnapshot(
            String itemCode,
            String intervalValue,
            LocalDate from,
            LocalDate to,
            String userId,
            KisTradeMode tradeMode
    ) {
        WorkspaceChartInterval interval = WorkspaceChartInterval.from(intervalValue);
        String kisPeriodType = interval.isMinute() ? "DAY" : interval.getKisPeriodType();

        WorkspaceChartSnapshotDto dto = new WorkspaceChartSnapshotDto();
        dto.setItemCode(itemCode);
        dto.setInterval(interval.getRequestValue());
        dto.setKisPeriodType(kisPeriodType);
        dto.setFrom(from);
        dto.setTo(to);
        dto.setTradeMode(tradeMode);
        dto.setCapturedAt(Instant.now());
        dto.setCandles(itemService.findChart(itemCode, kisPeriodType, from, to, tradeMode));
        dto.setIndicators(itemService.indicators(itemCode, kisPeriodType, from, to, tradeMode));
        dto.setDrawings(userId == null || userId.isBlank() ? List.of() : chartDrawingService.find(userId, itemCode));
        return dto;
    }

    public WorkspaceTradingSnapshotDto tradingSnapshot(
            String itemCode,
            String accountNo,
            BigDecimal price,
            KisTradeMode tradeMode
    ) {
        AccountBalanceDetailDto balance = accountService.findBalance(accountNo, tradeMode);
        WorkspaceTradingSnapshotDto dto = new WorkspaceTradingSnapshotDto();
        dto.setItemCode(itemCode);
        dto.setAccountNo(accountNo);
        dto.setTradeMode(tradeMode);
        dto.setCapturedAt(Instant.now());
        dto.setBalance(balance);
        dto.setPositions(balance.getPositions() == null ? List.of() : balance.getPositions());
        dto.setCurrentPosition(currentPosition(dto.getPositions(), itemCode));
        if (price != null) {
            dto.setOrderableAmount(orderableAmount(accountNo, itemCode, price, tradeMode));
        }
        return dto;
    }

    private List<WatchlistItemDto> watchlistItems(String userId) {
        if (userId == null || userId.isBlank()) {
            return List.of();
        }
        return watchlistService.findByUserId(userId);
    }

    private List<FrequentSearchItemDto> frequentSearchItems(String userId) {
        if (userId == null || userId.isBlank()) {
            return List.of();
        }
        return frequentSearchItemService.findByUserId(userId);
    }

    private PositionDto currentPosition(List<PositionDto> positions, String itemCode) {
        return positions.stream()
                .filter(position -> itemCode.equals(position.getItemCode()))
                .findFirst()
                .orElse(null);
    }

    private OrderableAmountRequest orderableAmountRequest(String accountNo, String itemCode, BigDecimal price, KisTradeMode tradeMode) {
        OrderableAmountRequest request = new OrderableAmountRequest();
        request.setAccountNo(accountNo);
        request.setItemCode(itemCode);
        request.setPrice(price);
        request.setTradeMode(tradeMode);
        return request;
    }

    private com.upmudoum.trade.domain.trade.dto.OrderableAmountDto orderableAmount(
            String accountNo,
            String itemCode,
            BigDecimal price,
            KisTradeMode tradeMode
    ) {
        return kisOrderService.orderableAmount(orderableAmountRequest(accountNo, itemCode, price, tradeMode));
    }
}
