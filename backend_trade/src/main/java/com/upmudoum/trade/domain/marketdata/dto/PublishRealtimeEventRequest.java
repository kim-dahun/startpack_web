package com.upmudoum.trade.domain.marketdata.dto;

import com.upmudoum.trade.domain.marketdata.vo.TradeRealtimeEventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PublishRealtimeEventRequest {

    @NotNull
    private TradeRealtimeEventType type;

    @NotBlank
    private String itemCode;

    private Map<String, Object> payload;

}
