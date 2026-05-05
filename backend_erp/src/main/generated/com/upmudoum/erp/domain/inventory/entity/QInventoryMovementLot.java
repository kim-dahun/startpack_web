package com.upmudoum.erp.domain.inventory.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInventoryMovementLot is a Querydsl query type for InventoryMovementLot
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInventoryMovementLot extends EntityPathBase<InventoryMovementLot> {

    private static final long serialVersionUID = -569482181L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInventoryMovementLot inventoryMovementLot = new QInventoryMovementLot("inventoryMovementLot");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.upmudoum.erp.domain.lot.entity.QLot lot;

    public final QInventoryMovement movement;

    public final com.upmudoum.erp.domain.inventory.vo.QQuantity quantity;

    public QInventoryMovementLot(String variable) {
        this(InventoryMovementLot.class, forVariable(variable), INITS);
    }

    public QInventoryMovementLot(Path<? extends InventoryMovementLot> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInventoryMovementLot(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInventoryMovementLot(PathMetadata metadata, PathInits inits) {
        this(InventoryMovementLot.class, metadata, inits);
    }

    public QInventoryMovementLot(Class<? extends InventoryMovementLot> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.lot = inits.isInitialized("lot") ? new com.upmudoum.erp.domain.lot.entity.QLot(forProperty("lot"), inits.get("lot")) : null;
        this.movement = inits.isInitialized("movement") ? new QInventoryMovement(forProperty("movement"), inits.get("movement")) : null;
        this.quantity = inits.isInitialized("quantity") ? new com.upmudoum.erp.domain.inventory.vo.QQuantity(forProperty("quantity")) : null;
    }

}

