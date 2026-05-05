package com.upmudoum.trade.domain.marketdata.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TradeRealtimeStatusDto {

    private boolean kisConnected;
    private int sessionCount;
    private int subscriptionCount;
    private int cachedEventCount;

}
