package com.upmudoum.trade.domain.trade.dto;

import com.upmudoum.trade.domain.trade.vo.TradeSide;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderValidationResultDto {

    private String accountNo;
    private String itemCode;
    private TradeSide side;
    private long requestedQuantity;
    private BigDecimal price;
    private BigDecimal requiredAmount;
    private BigDecimal availableCashAmount;
    private Long availableQuantity;
    private boolean allowed;
    private String failureReason;
}
