package com.upmudoum.erp.domain.warehouse.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWarehouse is a Querydsl query type for Warehouse
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWarehouse extends EntityPathBase<Warehouse> {

    private static final long serialVersionUID = -523599801L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWarehouse warehouse = new QWarehouse("warehouse");

    public final com.upmudoum.erp.domain.warehouse.vo.QWarehouseCode code;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath location = createString("location");

    public final StringPath name = createString("name");

    public final EnumPath<com.upmudoum.erp.domain.warehouse.vo.WarehouseStatus> status = createEnum("status", com.upmudoum.erp.domain.warehouse.vo.WarehouseStatus.class);

    public QWarehouse(String variable) {
        this(Warehouse.class, forVariable(variable), INITS);
    }

    public QWarehouse(Path<? extends Warehouse> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWarehouse(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWarehouse(PathMetadata metadata, PathInits inits) {
        this(Warehouse.class, metadata, inits);
    }

    public QWarehouse(Class<? extends Warehouse> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.code = inits.isInitialized("code") ? new com.upmudoum.erp.domain.warehouse.vo.QWarehouseCode(forProperty("code")) : null;
    }

}

