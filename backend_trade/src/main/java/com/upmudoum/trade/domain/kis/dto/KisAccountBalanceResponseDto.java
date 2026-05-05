package com.upmudoum.trade.domain.kis.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KisAccountBalanceResponseDto {

    private String cano;
    private BigDecimal totalAssetAmount;
    private BigDecimal cashAmount;
}
