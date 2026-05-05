package com.upmudoum.trade.domain.item.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItemMetricsDto {

    private String itemCode;
    private String itemName;
    private String marketCode;
    private String sectorName;
    private BigDecimal per;
    private BigDecimal pbr;
    private BigDecimal eps;
    private BigDecimal bps;
    private BigDecimal salesAmount;
    private BigDecimal operatingProfit;
    private BigDecimal marketCap;
    private BigDecimal high52WeekPrice;
    private BigDecimal low52WeekPrice;
}
