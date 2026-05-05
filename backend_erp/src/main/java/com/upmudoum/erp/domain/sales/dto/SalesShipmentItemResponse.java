package com.upmudoum.erp.domain.sales.dto;

import com.upmudoum.erp.domain.sales.entity.SalesShipmentItem;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SalesShipmentItemResponse {

    private Long id;
    private Long salesShipmentId;
    private Long partnerId;
    private Long itemId;
    private String itemCode;
    private Long warehouseId;
    private String warehouseCode;
    private Long selectedLotId;
    private String selectedLotNo;
    private Long inventoryMovementId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal supplyAmount;

    public static SalesShipmentItemResponse from(SalesShipmentItem item) {
        SalesShipmentItemResponse response = new SalesShipmentItemResponse();
        response.id = item.getId();
        response.salesShipmentId = item.getSalesShipment().getId();
        response.partnerId = item.getSalesShipment().getPartner().getId();
        response.itemId = item.getItem().getId();
        response.itemCode = item.getItem().getCode().getValue();
        response.warehouseId = item.getWarehouse().getId();
        response.warehouseCode = item.getWarehouse().getCode().getValue();
        response.selectedLotId = item.getLot() == null ? null : item.getLot().getId();
        response.selectedLotNo = item.getLot() == null ? null : item.getLot().getLotNo();
        response.inventoryMovementId = item.getInventoryMovement() == null ? null : item.getInventoryMovement().getId();
        response.quantity = item.getQuantity().getValue();
        response.unitPrice = item.getUnitPrice().getValue();
        response.supplyAmount = item.getSupplyAmount().getValue();
        return response;
    }
}
