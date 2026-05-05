package com.upmudoum.trade.domain.trade.dto;

import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderableAmountRequest {

    @NotBlank
    private String accountNo;

    @NotBlank
    private String itemCode;

    @NotNull
    @Positive
    private BigDecimal price;

    @NotNull
    private KisTradeMode tradeMode = KisTradeMode.LIVE;
}
