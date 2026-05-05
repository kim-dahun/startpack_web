package com.upmudoum.trade.domain.workspace.dto;

import com.upmudoum.trade.domain.chart.dto.ChartDrawingDto;
import com.upmudoum.trade.domain.item.dto.ItemChartCandleDto;
import com.upmudoum.trade.domain.item.dto.ItemIndicatorDto;
import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WorkspaceChartSnapshotDto {

    private String itemCode;
    private String interval;
    private String kisPeriodType;
    private LocalDate from;
    private LocalDate to;
    private KisTradeMode tradeMode;
    private Instant capturedAt;
    private List<ItemChartCandleDto> candles;
    private ItemIndicatorDto indicators;
    private List<ChartDrawingDto> drawings;
}
