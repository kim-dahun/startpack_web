package com.upmudoum.trade.domain.analysis.dto;

import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MarketRankingSnapshotDto {

    private LocalDate baseDate;
    private TradeMasterType masterType;
    private String itemCode;
    private String itemName;
    private String marketCode;
    private String countryCode;
    private String sectorName;
    private BigDecimal currentPrice;
    private BigDecimal changeAmount;
    private BigDecimal changeRate;
    private BigDecimal volume;
    private BigDecimal turnover;
    private BigDecimal marketCap;
    private BigDecimal high52WeekPrice;
    private BigDecimal low52WeekPrice;
    private BigDecimal volatility;
}
