package com.upmudoum.erp.domain.inventory.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInventoryBalance is a Querydsl query type for InventoryBalance
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInventoryBalance extends EntityPathBase<InventoryBalance> {

    private static final long serialVersionUID = -419688587L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInventoryBalance inventoryBalance = new QInventoryBalance("inventoryBalance");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.upmudoum.erp.domain.item.entity.QItem item;

    public final com.upmudoum.erp.domain.inventory.vo.QQuantity quantity;

    public final com.upmudoum.erp.domain.warehouse.entity.QWarehouse warehouse;

    public QInventoryBalance(String variable) {
        this(InventoryBalance.class, forVariable(variable), INITS);
    }

    public QInventoryBalance(Path<? extends InventoryBalance> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInventoryBalance(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInventoryBalance(PathMetadata metadata, PathInits inits) {
        this(InventoryBalance.class, metadata, inits);
    }

    public QInventoryBalance(Class<? extends InventoryBalance> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.item = inits.isInitialized("item") ? new com.upmudoum.erp.domain.item.entity.QItem(forProperty("item"), inits.get("item")) : null;
        this.quantity = inits.isInitialized("quantity") ? new com.upmudoum.erp.domain.inventory.vo.QQuantity(forProperty("quantity")) : null;
        this.warehouse = inits.isInitialized("warehouse") ? new com.upmudoum.erp.domain.warehouse.entity.QWarehouse(forProperty("warehouse"), inits.get("warehouse")) : null;
    }

}

