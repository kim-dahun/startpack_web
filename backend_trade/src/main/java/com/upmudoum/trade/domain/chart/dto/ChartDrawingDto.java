package com.upmudoum.trade.domain.chart.dto;

import com.upmudoum.trade.domain.chart.vo.ChartDrawingType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChartDrawingDto {

    private Long id;
    private String userId;
    private String itemCode;
    private ChartDrawingType drawingType;
    private LocalDate startDate;
    private BigDecimal startPrice;
    private LocalDate endDate;
    private BigDecimal endPrice;
    private String memo;
    private Instant createdAt;
    private Instant updatedAt;
}
