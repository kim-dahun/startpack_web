package com.upmudoum.erp.domain.production.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductionResultRequest {

    @NotNull
    private Long warehouseId;

    private Long routeId;

    private Long routeStepId;

    private Long productionOrderStepId;

    private Long processId;

    private Long equipmentId;

    @NotNull
    @DecimalMin(value = "0.000001")
    private BigDecimal goodQuantity;

    @NotNull
    @DecimalMin(value = "0.000000")
    private BigDecimal defectQuantity = BigDecimal.ZERO;

    @Size(max = 80)
    private String finishedLotNo;

    private java.time.LocalDateTime workStartedAt;

    private java.time.LocalDateTime workEndedAt;

    @Valid
    private List<ProductionConsumptionAdjustmentRequest> adjustments;
}
