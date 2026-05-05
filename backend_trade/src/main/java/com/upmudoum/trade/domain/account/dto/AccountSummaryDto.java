package com.upmudoum.trade.domain.account.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountSummaryDto {

    private String accountNo;
    private String accountName;
    private BigDecimal totalAssetAmount;
    private BigDecimal cashAmount;

}
