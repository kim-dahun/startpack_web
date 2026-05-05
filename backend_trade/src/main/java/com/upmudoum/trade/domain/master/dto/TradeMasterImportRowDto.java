package com.upmudoum.trade.domain.master.dto;

import java.math.BigDecimal;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TradeMasterImportRowDto {

    private String itemCode;
    private String itemName;
    private String marketCode;
    private String countryCode;
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
    private Map<String, Object> raw;
}
