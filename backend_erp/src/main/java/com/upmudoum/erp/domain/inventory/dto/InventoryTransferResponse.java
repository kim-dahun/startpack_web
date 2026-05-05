package com.upmudoum.erp.domain.inventory.dto;

import com.upmudoum.erp.domain.inventory.entity.InventoryTransfer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InventoryTransferResponse {

    private Long id;
    private String transferNo;
    private Long itemId;
    private String itemCode;
    private Long fromWarehouseId;
    private String fromWarehouseCode;
    private Long toWarehouseId;
    private String toWarehouseCode;
    private BigDecimal quantity;
    private Long outMovementId;
    private Long inMovementId;
    private String memo;
    private LocalDateTime transferredAt;

    public static InventoryTransferResponse from(InventoryTransfer transfer) {
        InventoryTransferResponse response = new InventoryTransferResponse();
        response.id = transfer.getId();
        response.transferNo = transfer.getTransferNo();
        response.itemId = transfer.getItem().getId();
        response.itemCode = transfer.getItem().getCode().getValue();
        response.fromWarehouseId = transfer.getFromWarehouse().getId();
        response.fromWarehouseCode = transfer.getFromWarehouse().getCode().getValue();
        response.toWarehouseId = transfer.getToWarehouse().getId();
        response.toWarehouseCode = transfer.getToWarehouse().getCode().getValue();
        response.quantity = transfer.getQuantity().getValue();
        response.outMovementId = transfer.getOutMovement() == null ? null : transfer.getOutMovement().getId();
        response.inMovementId = transfer.getInMovement() == null ? null : transfer.getInMovement().getId();
        response.memo = transfer.getMemo();
        response.transferredAt = transfer.getTransferredAt();
        return response;
    }
}
