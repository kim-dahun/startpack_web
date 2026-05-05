package com.upmudoum.erp.domain.inventory.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInventoryTransfer is a Querydsl query type for InventoryTransfer
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInventoryTransfer extends EntityPathBase<InventoryTransfer> {

    private static final long serialVersionUID = -1214698894L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInventoryTransfer inventoryTransfer = new QInventoryTransfer("inventoryTransfer");

    public final com.upmudoum.erp.domain.warehouse.entity.QWarehouse fromWarehouse;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QInventoryMovement inMovement;

    public final com.upmudoum.erp.domain.item.entity.QItem item;

    public final StringPath memo = createString("memo");

    public final QInventoryMovement outMovement;

    public final com.upmudoum.erp.domain.inventory.vo.QQuantity quantity;

    public final com.upmudoum.erp.domain.warehouse.entity.QWarehouse toWarehouse;

    public final StringPath transferNo = createString("transferNo");

    public final DateTimePath<java.time.LocalDateTime> transferredAt = createDateTime("transferredAt", java.time.LocalDateTime.class);

    public QInventoryTransfer(String variable) {
        this(InventoryTransfer.class, forVariable(variable), INITS);
    }

    public QInventoryTransfer(Path<? extends InventoryTransfer> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInventoryTransfer(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInventoryTransfer(PathMetadata metadata, PathInits inits) {
        this(InventoryTransfer.class, metadata, inits);
    }

    public QInventoryTransfer(Class<? extends InventoryTransfer> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.fromWarehouse = inits.isInitialized("fromWarehouse") ? new com.upmudoum.erp.domain.warehouse.entity.QWarehouse(forProperty("fromWarehouse"), inits.get("fromWarehouse")) : null;
        this.inMovement = inits.isInitialized("inMovement") ? new QInventoryMovement(forProperty("inMovement"), inits.get("inMovement")) : null;
        this.item = inits.isInitialized("item") ? new com.upmudoum.erp.domain.item.entity.QItem(forProperty("item"), inits.get("item")) : null;
        this.outMovement = inits.isInitialized("outMovement") ? new QInventoryMovement(forProperty("outMovement"), inits.get("outMovement")) : null;
        this.quantity = inits.isInitialized("quantity") ? new com.upmudoum.erp.domain.inventory.vo.QQuantity(forProperty("quantity")) : null;
        this.toWarehouse = inits.isInitialized("toWarehouse") ? new com.upmudoum.erp.domain.warehouse.entity.QWarehouse(forProperty("toWarehouse"), inits.get("toWarehouse")) : null;
    }

}

