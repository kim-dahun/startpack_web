package com.upmudoum.erp.domain.warehouse.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseSafetyStockRequest {

    @NotNull
    private Long itemId;

    @NotNull
    private Long warehouseId;

    @NotNull
    @DecimalMin(value = "0.000000")
    private BigDecimal safetyQuantity;

    private boolean active = true;
}
