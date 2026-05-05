package com.upmudoum.erp.domain.production.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductionConsumption is a Querydsl query type for ProductionConsumption
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductionConsumption extends EntityPathBase<ProductionConsumption> {

    private static final long serialVersionUID = -2142779736L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProductionConsumption productionConsumption = new QProductionConsumption("productionConsumption");

    public final com.upmudoum.erp.domain.inventory.vo.QQuantity actualQuantity;

    public final EnumPath<com.upmudoum.erp.domain.production.vo.ProductionConsumptionAdjustType> adjustType = createEnum("adjustType", com.upmudoum.erp.domain.production.vo.ProductionConsumptionAdjustType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.upmudoum.erp.domain.item.entity.QItem item;

    public final com.upmudoum.erp.domain.inventory.vo.QQuantity plannedQuantity;

    public final QProductionResult productionResult;

    public QProductionConsumption(String variable) {
        this(ProductionConsumption.class, forVariable(variable), INITS);
    }

    public QProductionConsumption(Path<? extends ProductionConsumption> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProductionConsumption(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProductionConsumption(PathMetadata metadata, PathInits inits) {
        this(ProductionConsumption.class, metadata, inits);
    }

    public QProductionConsumption(Class<? extends ProductionConsumption> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.actualQuantity = inits.isInitialized("actualQuantity") ? new com.upmudoum.erp.domain.inventory.vo.QQuantity(forProperty("actualQuantity")) : null;
        this.item = inits.isInitialized("item") ? new com.upmudoum.erp.domain.item.entity.QItem(forProperty("item"), inits.get("item")) : null;
        this.plannedQuantity = inits.isInitialized("plannedQuantity") ? new com.upmudoum.erp.domain.inventory.vo.QQuantity(forProperty("plannedQuantity")) : null;
        this.productionResult = inits.isInitialized("productionResult") ? new QProductionResult(forProperty("productionResult"), inits.get("productionResult")) : null;
    }

}

