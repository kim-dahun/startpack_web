package com.upmudoum.trade.domain.marketdata.dto;

import com.upmudoum.trade.domain.marketdata.vo.TradeRealtimeEventType;
import java.time.Instant;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TradeRealtimeEventDto {

    private TradeRealtimeEventType type;
    private String itemCode;
    private Instant occurredAt;
    private Map<String, Object> payload;

}
