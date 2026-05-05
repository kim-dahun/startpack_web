package com.upmudoum.trade.domain.trade.dto;

import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import java.math.BigDecimal;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderableAmountDto {

    private String accountNo;
    private String itemCode;
    private BigDecimal price;
    private BigDecimal orderableCashAmount;
    private long orderableQuantity;
    private KisTradeMode tradeMode;
    private Map<String, Object> raw;
}
