package com.upmudoum.erp.domain.lot.dto;

import com.upmudoum.erp.domain.inventory.entity.InventoryLotBalance;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LotBalanceResponse {

    private Long id;
    private Long itemId;
    private String itemCode;
    private Long warehouseId;
    private String warehouseCode;
    private Long lotId;
    private String lotNo;
    private BigDecimal quantity;
    private LocalDateTime firstReceivedAt;

    public static LotBalanceResponse from(InventoryLotBalance balance) {
        LotBalanceResponse response = new LotBalanceResponse();
        response.id = balance.getId();
        response.itemId = balance.getItem().getId();
        response.itemCode = balance.getItem().getCode().getValue();
        response.warehouseId = balance.getWarehouse().getId();
        response.warehouseCode = balance.getWarehouse().getCode().getValue();
        response.lotId = balance.getLot() == null ? null : balance.getLot().getId();
        response.lotNo = balance.getLot() == null ? null : balance.getLot().getLotNo();
        response.quantity = balance.getQuantity().getValue();
        response.firstReceivedAt = balance.getFirstReceivedAt();
        return response;
    }
}
