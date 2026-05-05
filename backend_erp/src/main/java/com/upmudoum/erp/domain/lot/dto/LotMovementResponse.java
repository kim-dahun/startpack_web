package com.upmudoum.erp.domain.lot.dto;

import com.upmudoum.erp.domain.inventory.entity.InventoryMovementLot;
import com.upmudoum.erp.domain.inventory.vo.InventoryMovementType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LotMovementResponse {

    private Long id;
    private Long movementId;
    private Long lotId;
    private String lotNo;
    private InventoryMovementType movementType;
    private BigDecimal quantity;
    private LocalDateTime occurredAt;

    public static LotMovementResponse from(InventoryMovementLot movementLot) {
        LotMovementResponse response = new LotMovementResponse();
        response.id = movementLot.getId();
        response.movementId = movementLot.getMovement().getId();
        response.lotId = movementLot.getLot() == null ? null : movementLot.getLot().getId();
        response.lotNo = movementLot.getLot() == null ? null : movementLot.getLot().getLotNo();
        response.movementType = movementLot.getMovement().getMovementType();
        response.quantity = movementLot.getQuantity().getValue();
        response.occurredAt = movementLot.getMovement().getOccurredAt();
        return response;
    }
}
