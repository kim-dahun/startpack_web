package com.upmudoum.erp.domain.production.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductionOrderRequest {

    @NotBlank
    @Size(max = 50)
    private String orderNo;

    @NotNull
    private Long itemId;

    @NotNull
    private Long bomVersionId;

    private Long routeId;

    private Long plannedProcessId;

    private Long plannedEquipmentId;

    @NotNull
    @DecimalMin(value = "0.000001")
    private BigDecimal plannedQuantity;

    @NotNull
    private LocalDate dueDate;
}
