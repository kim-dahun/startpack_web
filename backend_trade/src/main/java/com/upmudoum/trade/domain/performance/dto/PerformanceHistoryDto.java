package com.upmudoum.trade.domain.performance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PerformanceHistoryDto {

    private String accountNo;
    private LocalDate baseDate;
    private BigDecimal totalAssetAmount;
    private BigDecimal profitLossAmount;
}
