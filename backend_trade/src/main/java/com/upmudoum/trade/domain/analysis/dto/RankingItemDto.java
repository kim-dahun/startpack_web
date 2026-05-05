package com.upmudoum.trade.domain.analysis.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RankingItemDto {

    private int rank;
    private String itemCode;
    private String itemName;
    private String marketCode;
    private String countryCode;
    private String sectorName;
    private BigDecimal metricValue;
}
