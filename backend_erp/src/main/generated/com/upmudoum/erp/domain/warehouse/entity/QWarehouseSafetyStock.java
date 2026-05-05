package com.upmudoum.erp.domain.warehouse.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWarehouseSafetyStock is a Querydsl query type for WarehouseSafetyStock
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWarehouseSafetyStock extends EntityPathBase<WarehouseSafetyStock> {

    private static final long serialVersionUID = 1117020317L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWarehouseSafetyStock warehouseSafetyStock = new QWarehouseSafetyStock("warehouseSafetyStock");

    public final BooleanPath active = createBoolean("active");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.upmudoum.erp.domain.item.entity.QItem item;

    public final com.upmudoum.erp.domain.inventory.vo.QQuantity safetyQuantity;

    public final QWarehouse warehouse;

    public QWarehouseSafetyStock(String variable) {
        this(WarehouseSafetyStock.class, forVariable(variable), INITS);
    }

    public QWarehouseSafetyStock(Path<? extends WarehouseSafetyStock> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWarehouseSafetyStock(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWarehouseSafetyStock(PathMetadata metadata, PathInits inits) {
        this(WarehouseSafetyStock.class, metadata, inits);
    }

    public QWarehouseSafetyStock(Class<? extends WarehouseSafetyStock> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.item = inits.isInitialized("item") ? new com.upmudoum.erp.domain.item.entity.QItem(forProperty("item"), inits.get("item")) : null;
        this.safetyQuantity = inits.isInitialized("safetyQuantity") ? new com.upmudoum.erp.domain.inventory.vo.QQuantity(forProperty("safetyQuantity")) : null;
        this.warehouse = inits.isInitialized("warehouse") ? new QWarehouse(forProperty("warehouse"), inits.get("warehouse")) : null;
    }

}

