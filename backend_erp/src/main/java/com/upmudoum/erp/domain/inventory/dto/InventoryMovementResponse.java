package com.upmudoum.erp.domain.inventory.dto;

import com.upmudoum.erp.domain.inventory.entity.InventoryMovement;
import com.upmudoum.erp.domain.inventory.vo.InventoryMovementType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InventoryMovementResponse {

    private Long id;
    private Long itemId;
    private String itemCode;
    private Long warehouseId;
    private String warehouseCode;
    private InventoryMovementType movementType;
    private BigDecimal quantity;
    private BigDecimal balanceAfter;
    private String memo;
    private LocalDateTime occurredAt;

    public static InventoryMovementResponse from(InventoryMovement movement) {
        InventoryMovementResponse response = new InventoryMovementResponse();
        response.id = movement.getId();
        response.itemId = movement.getItem().getId();
        response.itemCode = movement.getItem().getCode().getValue();
        response.warehouseId = movement.getWarehouse().getId();
        response.warehouseCode = movement.getWarehouse().getCode().getValue();
        response.movementType = movement.getMovementType();
        response.quantity = movement.getQuantity().getValue();
        response.balanceAfter = movement.getBalanceAfter().getValue();
        response.memo = movement.getMemo();
        response.occurredAt = movement.getOccurredAt();
        return response;
    }

}
