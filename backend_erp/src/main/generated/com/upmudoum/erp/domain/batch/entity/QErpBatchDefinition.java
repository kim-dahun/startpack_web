package com.upmudoum.erp.domain.batch.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QErpBatchDefinition is a Querydsl query type for ErpBatchDefinition
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QErpBatchDefinition extends EntityPathBase<ErpBatchDefinition> {

    private static final long serialVersionUID = 1067722493L;

    public static final QErpBatchDefinition erpBatchDefinition = new QErpBatchDefinition("erpBatchDefinition");

    public final StringPath code = createString("code");

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath required = createBoolean("required");

    public final StringPath triggerPolicy = createString("triggerPolicy");

    public QErpBatchDefinition(String variable) {
        super(ErpBatchDefinition.class, forVariable(variable));
    }

    public QErpBatchDefinition(Path<? extends ErpBatchDefinition> path) {
        super(path.getType(), path.getMetadata());
    }

    public QErpBatchDefinition(PathMetadata metadata) {
        super(ErpBatchDefinition.class, metadata);
    }

}

