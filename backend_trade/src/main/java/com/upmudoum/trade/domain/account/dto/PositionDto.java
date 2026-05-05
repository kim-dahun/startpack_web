package com.upmudoum.trade.domain.account.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PositionDto {

    private String accountNo;
    private String itemCode;
    private String itemName;
    private long quantity;
    private long orderableQuantity;
    private BigDecimal averagePrice;
    private BigDecimal currentPrice;
    private BigDecimal evaluationAmount;
    private BigDecimal profitLossAmount;
    private BigDecimal profitLossRate;
}
