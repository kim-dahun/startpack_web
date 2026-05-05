package com.upmudoum.erp.domain.warehouse.dto;

import com.upmudoum.erp.domain.warehouse.entity.WarehouseSafetyStock;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WarehouseSafetyStockResponse {

    private Long id;
    private Long itemId;
    private String itemCode;
    private Long warehouseId;
    private String warehouseCode;
    private BigDecimal safetyQuantity;
    private boolean active;

    public static WarehouseSafetyStockResponse from(WarehouseSafetyStock safetyStock) {
        WarehouseSafetyStockResponse response = new WarehouseSafetyStockResponse();
        response.id = safetyStock.getId();
        response.itemId = safetyStock.getItem().getId();
        response.itemCode = safetyStock.getItem().getCode().getValue();
        response.warehouseId = safetyStock.getWarehouse().getId();
        response.warehouseCode = safetyStock.getWarehouse().getCode().getValue();
        response.safetyQuantity = safetyStock.getSafetyQuantity().getValue();
        response.active = safetyStock.isActive();
        return response;
    }
}
