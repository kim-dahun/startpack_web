package com.upmudoum.trade.domain.trade.dto;

import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import com.upmudoum.trade.domain.trade.vo.TradeSide;
import jakarta.validation.constraints.AssertTrue;
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
public class PlaceOrderRequest {

    @NotBlank
    private String accountNo;

    @NotBlank
    private String itemCode;

    @NotNull
    private TradeSide side;

    @Positive
    private long quantity;

    @NotNull
    @Positive
    private BigDecimal price;

    @NotNull
    private KisTradeMode tradeMode = KisTradeMode.LIVE;

    private boolean confirmLiveOrder;

    @AssertTrue(message = "LIVE order requires confirmLiveOrder=true")
    public boolean isLiveOrderConfirmed() {
        return tradeMode != KisTradeMode.LIVE || confirmLiveOrder;
    }
}
