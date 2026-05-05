package com.upmudoum.trade.domain.item.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItemSearchResultDto {

    private String itemCode;
    private String itemName;
    private String marketCode;
    private String masterType;
    private BigDecimal currentPrice;
    private BigDecimal changeAmount;
    private BigDecimal changeRate;
    private long accumulatedVolume;
}
