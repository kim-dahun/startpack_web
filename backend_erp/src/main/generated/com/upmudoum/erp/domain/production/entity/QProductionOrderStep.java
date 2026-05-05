package com.upmudoum.erp.domain.production.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductionOrderStep is a Querydsl query type for ProductionOrderStep
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductionOrderStep extends EntityPathBase<ProductionOrderStep> {

    private static final long serialVersionUID = 1291967879L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProductionOrderStep productionOrderStep = new QProductionOrderStep("productionOrderStep");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> plannedEndAt = createDateTime("plannedEndAt", java.time.LocalDateTime.class);

    public final com.upmudoum.erp.domain.equipment.entity.QEquipment plannedEquipment;

    public final DateTimePath<java.time.LocalDateTime> plannedStartAt = createDateTime("plannedStartAt", java.time.LocalDateTime.class);

    public final com.upmudoum.erp.domain.process.entity.QErpProcess process;

    public final QProductionOrder productionOrder;

    public final com.upmudoum.erp.domain.route.entity.QRouteStep routeStep;

    public final NumberPath<Integer> sequenceNo = createNumber("sequenceNo", Integer.class);

    public final EnumPath<com.upmudoum.erp.domain.production.vo.ProductionStepStatus> status = createEnum("status", com.upmudoum.erp.domain.production.vo.ProductionStepStatus.class);

    public QProductionOrderStep(String variable) {
        this(ProductionOrderStep.class, forVariable(variable), INITS);
    }

    public QProductionOrderStep(Path<? extends ProductionOrderStep> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProductionOrderStep(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProductionOrderStep(PathMetadata metadata, PathInits inits) {
        this(ProductionOrderStep.class, metadata, inits);
    }

    public QProductionOrderStep(Class<? extends ProductionOrderStep> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.plannedEquipment = inits.isInitialized("plannedEquipment") ? new com.upmudoum.erp.domain.equipment.entity.QEquipment(forProperty("plannedEquipment"), inits.get("plannedEquipment")) : null;
        this.process = inits.isInitialized("process") ? new com.upmudoum.erp.domain.process.entity.QErpProcess(forProperty("process")) : null;
        this.productionOrder = inits.isInitialized("productionOrder") ? new QProductionOrder(forProperty("productionOrder"), inits.get("productionOrder")) : null;
        this.routeStep = inits.isInitialized("routeStep") ? new com.upmudoum.erp.domain.route.entity.QRouteStep(forProperty("routeStep"), inits.get("routeStep")) : null;
    }

}

