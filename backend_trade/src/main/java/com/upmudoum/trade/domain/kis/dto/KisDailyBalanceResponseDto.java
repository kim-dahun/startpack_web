package com.upmudoum.trade.domain.kis.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KisDailyBalanceResponseDto {

    private String accountNo;
    private BigDecimal totalAssetAmount;
    private BigDecimal profitLossAmount;
}
