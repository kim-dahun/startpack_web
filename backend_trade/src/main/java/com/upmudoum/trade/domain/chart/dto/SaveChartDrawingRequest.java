package com.upmudoum.trade.domain.chart.dto;

import com.upmudoum.trade.domain.chart.vo.ChartDrawingType;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SaveChartDrawingRequest {

    @NotNull
    private ChartDrawingType drawingType;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private BigDecimal startPrice;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private BigDecimal endPrice;

    private String memo;
}
