package com.upmudoum.trade.domain.workspace.dto;

import com.upmudoum.trade.domain.item.dto.FrequentSearchItemDto;
import com.upmudoum.trade.domain.item.dto.ItemMetricsDto;
import com.upmudoum.trade.domain.item.dto.ItemPriceDto;
import com.upmudoum.trade.domain.item.dto.OrderbookDto;
import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import com.upmudoum.trade.domain.watchlist.dto.WatchlistItemDto;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WorkspaceSnapshotDto {

    private String itemCode;
    private KisTradeMode tradeMode;
    private Instant capturedAt;
    private ItemPriceDto quote;
    private OrderbookDto orderbook;
    private ItemMetricsDto metrics;
    private List<WatchlistItemDto> watchlistItems;
    private List<FrequentSearchItemDto> frequentSearchItems;
}
