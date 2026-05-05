package com.upmudoum.erp.domain.inventory.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInventoryLotBalance is a Querydsl query type for InventoryLotBalance
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInventoryLotBalance extends EntityPathBase<InventoryLotBalance> {

    private static final long serialVersionUID = 1453269778L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInventoryLotBalance inventoryLotBalance = new QInventoryLotBalance("inventoryLotBalance");

    public final DateTimePath<java.time.LocalDateTime> firstReceivedAt = createDateTime("firstReceivedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.upmudoum.erp.domain.item.entity.QItem item;

    public final com.upmudoum.erp.domain.lot.entity.QLot lot;

    public final com.upmudoum.erp.domain.inventory.vo.QQuantity quantity;

    public final com.upmudoum.erp.domain.warehouse.entity.QWarehouse warehouse;

    public QInventoryLotBalance(String variable) {
        this(InventoryLotBalance.class, forVariable(variable), INITS);
    }

    public QInventoryLotBalance(Path<? extends InventoryLotBalance> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInventoryLotBalance(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInventoryLotBalance(PathMetadata metadata, PathInits inits) {
        this(InventoryLotBalance.class, metadata, inits);
    }

    public QInventoryLotBalance(Class<? extends InventoryLotBalance> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.item = inits.isInitialized("item") ? new com.upmudoum.erp.domain.item.entity.QItem(forProperty("item"), inits.get("item")) : null;
        this.lot = inits.isInitialized("lot") ? new com.upmudoum.erp.domain.lot.entity.QLot(forProperty("lot"), inits.get("lot")) : null;
        this.quantity = inits.isInitialized("quantity") ? new com.upmudoum.erp.domain.inventory.vo.QQuantity(forProperty("quantity")) : null;
        this.warehouse = inits.isInitialized("warehouse") ? new com.upmudoum.erp.domain.warehouse.entity.QWarehouse(forProperty("warehouse"), inits.get("warehouse")) : null;
    }

}

