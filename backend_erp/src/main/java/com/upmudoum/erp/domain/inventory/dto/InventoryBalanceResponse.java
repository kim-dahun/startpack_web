package com.upmudoum.erp.domain.inventory.dto;

import com.upmudoum.erp.domain.inventory.entity.InventoryBalance;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InventoryBalanceResponse {

    private Long itemId;
    private String itemCode;
    private Long warehouseId;
    private String warehouseCode;
    private BigDecimal quantity;

    public static InventoryBalanceResponse from(InventoryBalance balance) {
        InventoryBalanceResponse response = new InventoryBalanceResponse();
        response.itemId = balance.getItem().getId();
        response.itemCode = balance.getItem().getCode().getValue();
        response.warehouseId = balance.getWarehouse().getId();
        response.warehouseCode = balance.getWarehouse().getCode().getValue();
        response.quantity = balance.getQuantity().getValue();
        return response;
    }
}
