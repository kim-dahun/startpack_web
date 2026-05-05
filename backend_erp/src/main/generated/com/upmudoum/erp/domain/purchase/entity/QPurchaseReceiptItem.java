package com.upmudoum.erp.domain.purchase.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPurchaseReceiptItem is a Querydsl query type for PurchaseReceiptItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPurchaseReceiptItem extends EntityPathBase<PurchaseReceiptItem> {

    private static final long serialVersionUID = 219321960L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPurchaseReceiptItem purchaseReceiptItem = new QPurchaseReceiptItem("purchaseReceiptItem");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.upmudoum.erp.domain.inventory.entity.QInventoryMovement inventoryMovement;

    public final com.upmudoum.erp.domain.item.entity.QItem item;

    public final com.upmudoum.erp.domain.lot.entity.QLot lot;

    public final QPurchaseReceipt purchaseReceipt;

    public final com.upmudoum.erp.domain.inventory.vo.QQuantity quantity;

    public final com.upmudoum.erp.domain.accounting.vo.QMoneyAmount supplyAmount;

    public final com.upmudoum.erp.domain.accounting.vo.QUnitPrice unitPrice;

    public final com.upmudoum.erp.domain.warehouse.entity.QWarehouse warehouse;

    public QPurchaseReceiptItem(String variable) {
        this(PurchaseReceiptItem.class, forVariable(variable), INITS);
    }

    public QPurchaseReceiptItem(Path<? extends PurchaseReceiptItem> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPurchaseReceiptItem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPurchaseReceiptItem(PathMetadata metadata, PathInits inits) {
        this(PurchaseReceiptItem.class, metadata, inits);
    }

    public QPurchaseReceiptItem(Class<? extends PurchaseReceiptItem> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.inventoryMovement = inits.isInitialized("inventoryMovement") ? new com.upmudoum.erp.domain.inventory.entity.QInventoryMovement(forProperty("inventoryMovement"), inits.get("inventoryMovement")) : null;
        this.item = inits.isInitialized("item") ? new com.upmudoum.erp.domain.item.entity.QItem(forProperty("item"), inits.get("item")) : null;
        this.lot = inits.isInitialized("lot") ? new com.upmudoum.erp.domain.lot.entity.QLot(forProperty("lot"), inits.get("lot")) : null;
        this.purchaseReceipt = inits.isInitialized("purchaseReceipt") ? new QPurchaseReceipt(forProperty("purchaseReceipt"), inits.get("purchaseReceipt")) : null;
        this.quantity = inits.isInitialized("quantity") ? new com.upmudoum.erp.domain.inventory.vo.QQuantity(forProperty("quantity")) : null;
        this.supplyAmount = inits.isInitialized("supplyAmount") ? new com.upmudoum.erp.domain.accounting.vo.QMoneyAmount(forProperty("supplyAmount")) : null;
        this.unitPrice = inits.isInitialized("unitPrice") ? new com.upmudoum.erp.domain.accounting.vo.QUnitPrice(forProperty("unitPrice")) : null;
        this.warehouse = inits.isInitialized("warehouse") ? new com.upmudoum.erp.domain.warehouse.entity.QWarehouse(forProperty("warehouse"), inits.get("warehouse")) : null;
    }

}

