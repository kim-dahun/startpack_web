package com.upmudoum.trade.domain.workspace.controller;

import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import com.upmudoum.trade.domain.workspace.dto.WorkspaceChartSnapshotDto;
import com.upmudoum.trade.domain.workspace.dto.WorkspaceSnapshotDto;
import com.upmudoum.trade.domain.workspace.dto.WorkspaceTradingSnapshotDto;
import com.upmudoum.trade.domain.workspace.service.WorkspaceSnapshotService;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trade/workspace")
public class WorkspaceController {

    private final WorkspaceSnapshotService workspaceSnapshotService;

    public WorkspaceController(WorkspaceSnapshotService workspaceSnapshotService) {
        this.workspaceSnapshotService = workspaceSnapshotService;
    }

    @GetMapping("/{itemCode}/snapshot")
    public WorkspaceSnapshotDto snapshot(
            @PathVariable String itemCode,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "LIVE") KisTradeMode tradeMode
    ) {
        return workspaceSnapshotService.snapshot(itemCode, userId, tradeMode);
    }

    @GetMapping("/{itemCode}/chart-snapshot")
    public WorkspaceChartSnapshotDto chartSnapshot(
            @PathVariable String itemCode,
            @RequestParam(defaultValue = "DAY") String interval,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "LIVE") KisTradeMode tradeMode
    ) {
        return workspaceSnapshotService.chartSnapshot(itemCode, interval, from, to, userId, tradeMode);
    }

    @GetMapping("/{itemCode}/trading-snapshot")
    public WorkspaceTradingSnapshotDto tradingSnapshot(
            @PathVariable String itemCode,
            @RequestParam String accountNo,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(defaultValue = "LIVE") KisTradeMode tradeMode
    ) {
        return workspaceSnapshotService.tradingSnapshot(itemCode, accountNo, price, tradeMode);
    }
}
