package com.upmudoum.trade.domain.account.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyBalanceDto {

    private String accountNo;
    private LocalDate baseDate;
    private BigDecimal totalAssetAmount;
    private BigDecimal profitLossAmount;

}
