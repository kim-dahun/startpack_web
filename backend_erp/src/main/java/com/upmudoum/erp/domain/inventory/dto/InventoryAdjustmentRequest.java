package com.upmudoum.erp.domain.inventory.dto;

import com.upmudoum.erp.domain.inventory.vo.InventoryMovementType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryAdjustmentRequest {

    @NotNull
    private Long itemId;

    @NotNull
    private Long warehouseId;

    @NotNull
    private InventoryMovementType movementType;

    @NotNull
    @DecimalMin(value = "0.000001")
    private BigDecimal quantity;

    @Size(max = 200)
    private String memo;

}
