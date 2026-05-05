package com.upmudoum.trade.domain.account.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccountBalanceDetailDto {

    private String accountNo;
    private BigDecimal totalAssetAmount;
    private BigDecimal cashAmount;
    private BigDecimal orderableCashAmount;
    private BigDecimal totalEvaluationAmount;
    private BigDecimal totalProfitLossAmount;
    private BigDecimal totalProfitLossRate;
    private List<PositionDto> positions;
}
