package com.upmudoum.trade.domain.item.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderbookLevelDto {

    private int level;
    private BigDecimal askPrice;
    private long askQuantity;
    private BigDecimal bidPrice;
    private long bidQuantity;
}
