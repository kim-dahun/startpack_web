package com.upmudoum.erp.domain.inventory.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInventoryMovement is a Querydsl query type for InventoryMovement
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInventoryMovement extends EntityPathBase<InventoryMovement> {

    private static final long serialVersionUID = 1695707958L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInventoryMovement inventoryMovement = new QInventoryMovement("inventoryMovement");

    public final com.upmudoum.erp.domain.inventory.vo.QQuantity balanceAfter;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.upmudoum.erp.domain.item.entity.QItem item;

    public final StringPath memo = createString("memo");

    public final EnumPath<com.upmudoum.erp.domain.inventory.vo.InventoryMovementType> movementType = createEnum("movementType", com.upmudoum.erp.domain.inventory.vo.InventoryMovementType.class);

    public final DateTimePath<java.time.LocalDateTime> occurredAt = createDateTime("occurredAt", java.time.LocalDateTime.class);

    public final com.upmudoum.erp.domain.inventory.vo.QQuantity quantity;

    public final NumberPath<Long> referenceId = createNumber("referenceId", Long.class);

    public final EnumPath<com.upmudoum.erp.domain.inventory.vo.InventoryReferenceType> referenceType = createEnum("referenceType", com.upmudoum.erp.domain.inventory.vo.InventoryReferenceType.class);

    public final com.upmudoum.erp.domain.accounting.vo.QMoneyAmount supplyAmount;

    public final com.upmudoum.erp.domain.accounting.vo.QUnitPrice unitCost;

    public final com.upmudoum.erp.domain.warehouse.entity.QWarehouse warehouse;

    public QInventoryMovement(String variable) {
        this(InventoryMovement.class, forVariable(variable), INITS);
    }

    public QInventoryMovement(Path<? extends InventoryMovement> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInventoryMovement(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInventoryMovement(PathMetadata metadata, PathInits inits) {
        this(InventoryMovement.class, metadata, inits);
    }

    public QInventoryMovement(Class<? extends InventoryMovement> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.balanceAfter = inits.isInitialized("balanceAfter") ? new com.upmudoum.erp.domain.inventory.vo.QQuantity(forProperty("balanceAfter")) : null;
        this.item = inits.isInitialized("item") ? new com.upmudoum.erp.domain.item.entity.QItem(forProperty("item"), inits.get("item")) : null;
        this.quantity = inits.isInitialized("quantity") ? new com.upmudoum.erp.domain.inventory.vo.QQuantity(forProperty("quantity")) : null;
        this.supplyAmount = inits.isInitialized("supplyAmount") ? new com.upmudoum.erp.domain.accounting.vo.QMoneyAmount(forProperty("supplyAmount")) : null;
        this.unitCost = inits.isInitialized("unitCost") ? new com.upmudoum.erp.domain.accounting.vo.QUnitPrice(forProperty("unitCost")) : null;
        this.warehouse = inits.isInitialized("warehouse") ? new com.upmudoum.erp.domain.warehouse.entity.QWarehouse(forProperty("warehouse"), inits.get("warehouse")) : null;
    }

}

