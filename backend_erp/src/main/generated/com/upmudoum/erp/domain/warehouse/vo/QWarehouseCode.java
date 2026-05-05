package com.upmudoum.erp.domain.warehouse.vo;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QWarehouseCode is a Querydsl query type for WarehouseCode
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QWarehouseCode extends BeanPath<WarehouseCode> {

    private static final long serialVersionUID = -1039846742L;

    public static final QWarehouseCode warehouseCode = new QWarehouseCode("warehouseCode");

    public final StringPath value = createString("value");

    public QWarehouseCode(String variable) {
        super(WarehouseCode.class, forVariable(variable));
    }

    public QWarehouseCode(Path<? extends WarehouseCode> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWarehouseCode(PathMetadata metadata) {
        super(WarehouseCode.class, metadata);
    }

}

