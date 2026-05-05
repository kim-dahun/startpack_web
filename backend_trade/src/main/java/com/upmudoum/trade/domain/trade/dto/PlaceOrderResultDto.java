package com.upmudoum.trade.domain.trade.dto;

import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import com.upmudoum.trade.domain.trade.vo.TradeSide;
import java.math.BigDecimal;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlaceOrderResultDto {

    private String accountNo;
    private String itemCode;
    private TradeSide side;
    private long quantity;
    private BigDecimal price;
    private KisTradeMode tradeMode;
    private String orderNo;
    private String branchNo;
    private String responseCode;
    private String message;
    private Map<String, Object> raw;
}
