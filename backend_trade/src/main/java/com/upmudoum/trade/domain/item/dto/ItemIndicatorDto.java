package com.upmudoum.trade.domain.item.dto;

import java.math.BigDecimal;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItemIndicatorDto {

    private String itemCode;
    private String periodType;
    private Map<String, BigDecimal> movingAverages;
    private BigDecimal rsi;
    private BigDecimal macd;
    private BigDecimal macdSignal;
    private BigDecimal macdHistogram;
    private BigDecimal bollingerUpper;
    private BigDecimal bollingerMiddle;
    private BigDecimal bollingerLower;
    private BigDecimal atr;
    private BigDecimal stochasticK;
    private BigDecimal stochasticD;
    private BigDecimal obv;
    private BigDecimal mfi;
}
