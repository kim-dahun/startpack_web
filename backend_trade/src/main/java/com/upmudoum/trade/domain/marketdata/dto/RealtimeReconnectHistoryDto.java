package com.upmudoum.trade.domain.marketdata.dto;

import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RealtimeReconnectHistoryDto {

    private Long id;
    private Instant attemptedAt;
    private boolean success;
    private int subscriptionCount;
    private String failureReason;
}
