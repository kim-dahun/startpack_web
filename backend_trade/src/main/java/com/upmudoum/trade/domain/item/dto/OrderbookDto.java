package com.upmudoum.trade.domain.item.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderbookDto {

    private String itemCode;
    private Instant receivedAt;
    private List<OrderbookLevelDto> levels;
    private Map<String, Object> raw;
}
