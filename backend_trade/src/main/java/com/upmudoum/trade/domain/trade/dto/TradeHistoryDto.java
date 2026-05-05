package com.upmudoum.trade.domain.trade.dto;

import com.upmudoum.trade.domain.trade.vo.TradeSide;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TradeHistoryDto {

    private Long id;
    private String accountNo;
    private String itemCode;
    private String itemName;
    private TradeSide side;
    private long quantity;
    private BigDecimal price;
    private BigDecimal amount;
    private String idempotencyKey;
    private Instant tradedAt;

}
