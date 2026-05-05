package com.upmudoum.erp.domain.production.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductionResult is a Querydsl query type for ProductionResult
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductionResult extends EntityPathBase<ProductionResult> {

    private static final long serialVersionUID = -1608941328L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProductionResult productionResult = new QProductionResult("productionResult");

    public final DateTimePath<java.time.LocalDateTime> completedAt = createDateTime("completedAt", java.time.LocalDateTime.class);

    public final com.upmudoum.erp.domain.inventory.vo.QQuantity defectQuantity;

    public final com.upmudoum.erp.domain.equipment.entity.QEquipment equipment;

    public final com.upmudoum.erp.domain.inventory.vo.QQuantity goodQuantity;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.upmudoum.erp.domain.process.entity.QErpProcess process;

    public final QProductionOrder productionOrder;

    public final com.upmudoum.erp.domain.route.entity.QRoute route;

    public final com.upmudoum.erp.domain.route.entity.QRouteStep routeStep;

    public final EnumPath<com.upmudoum.erp.domain.production.vo.ProductionResultStatus> status = createEnum("status", com.upmudoum.erp.domain.production.vo.ProductionResultStatus.class);

    public final DateTimePath<java.time.LocalDateTime> workEndedAt = createDateTime("workEndedAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> workStartedAt = createDateTime("workStartedAt", java.time.LocalDateTime.class);

    public QProductionResult(String variable) {
        this(ProductionResult.class, forVariable(variable), INITS);
    }

    public QProductionResult(Path<? extends ProductionResult> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProductionResult(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProductionResult(PathMetadata metadata, PathInits inits) {
        this(ProductionResult.class, metadata, inits);
    }

    public QProductionResult(Class<? extends ProductionResult> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.defectQuantity = inits.isInitialized("defectQuantity") ? new com.upmudoum.erp.domain.inventory.vo.QQuantity(forProperty("defectQuantity")) : null;
        this.equipment = inits.isInitialized("equipment") ? new com.upmudoum.erp.domain.equipment.entity.QEquipment(forProperty("equipment"), inits.get("equipment")) : null;
        this.goodQuantity = inits.isInitialized("goodQuantity") ? new com.upmudoum.erp.domain.inventory.vo.QQuantity(forProperty("goodQuantity")) : null;
        this.process = inits.isInitialized("process") ? new com.upmudoum.erp.domain.process.entity.QErpProcess(forProperty("process")) : null;
        this.productionOrder = inits.isInitialized("productionOrder") ? new QProductionOrder(forProperty("productionOrder"), inits.get("productionOrder")) : null;
        this.route = inits.isInitialized("route") ? new com.upmudoum.erp.domain.route.entity.QRoute(forProperty("route"), inits.get("route")) : null;
        this.routeStep = inits.isInitialized("routeStep") ? new com.upmudoum.erp.domain.route.entity.QRouteStep(forProperty("routeStep"), inits.get("routeStep")) : null;
    }

}

