package com.upmudoum.trade.domain.analysis.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MarketGroupStrengthDto {

    private String groupName;
    private int itemCount;
    private BigDecimal strengthScore;
}
