package com.upmudoum.erp.domain.purchase.dto;

import com.upmudoum.erp.domain.purchase.entity.PurchaseReceiptItem;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PurchaseReceiptItemResponse {

    private Long id;
    private Long purchaseReceiptId;
    private Long partnerId;
    private Long itemId;
    private String itemCode;
    private Long warehouseId;
    private String warehouseCode;
    private Long lotId;
    private String lotNo;
    private Long inventoryMovementId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal supplyAmount;

    public static PurchaseReceiptItemResponse from(PurchaseReceiptItem item) {
        PurchaseReceiptItemResponse response = new PurchaseReceiptItemResponse();
        response.id = item.getId();
        response.purchaseReceiptId = item.getPurchaseReceipt().getId();
        response.partnerId = item.getPurchaseReceipt().getPartner().getId();
        response.itemId = item.getItem().getId();
        response.itemCode = item.getItem().getCode().getValue();
        response.warehouseId = item.getWarehouse().getId();
        response.warehouseCode = item.getWarehouse().getCode().getValue();
        response.lotId = item.getLot() == null ? null : item.getLot().getId();
        response.lotNo = item.getLot() == null ? null : item.getLot().getLotNo();
        response.inventoryMovementId = item.getInventoryMovement() == null ? null : item.getInventoryMovement().getId();
        response.quantity = item.getQuantity().getValue();
        response.unitPrice = item.getUnitPrice().getValue();
        response.supplyAmount = item.getSupplyAmount().getValue();
        return response;
    }
}
