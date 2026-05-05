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
public class LivePlaceOrderRequest {

    @NotBlank
    private String accountNo;

    @NotBlank
    private String itemCode;

    @Positive
    private long quantity;

    @NotNull
    @Positive
    private BigDecimal price;

    private boolean confirmLiveOrder;

    @AssertTrue(message = "LIVE order requires confirmLiveOrder=true")
    public boolean isLiveOrderConfirmed() {
        return confirmLiveOrder;
    }

    public PlaceOrderRequest toPlaceOrderRequest(TradeSide side) {
        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setAccountNo(accountNo);
        request.setItemCode(itemCode);
        request.setSide(side);
        request.setQuantity(quantity);
        request.setPrice(price);
        request.setTradeMode(KisTradeMode.LIVE);
        request.setConfirmLiveOrder(confirmLiveOrder);
        return request;
    }
}
