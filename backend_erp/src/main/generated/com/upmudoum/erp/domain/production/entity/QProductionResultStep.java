package com.upmudoum.erp.domain.production.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductionResultStep is a Querydsl query type for ProductionResultStep
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductionResultStep extends EntityPathBase<ProductionResultStep> {

    private static final long serialVersionUID = 79102940L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProductionResultStep productionResultStep = new QProductionResultStep("productionResultStep");

    public final com.upmudoum.erp.domain.equipment.entity.QEquipment equipment;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.upmudoum.erp.domain.process.entity.QErpProcess process;

    public final QProductionOrderStep productionOrderStep;

    public final QProductionResult productionResult;

    public final NumberPath<Integer> sequenceNo = createNumber("sequenceNo", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> workEndedAt = createDateTime("workEndedAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> workStartedAt = createDateTime("workStartedAt", java.time.LocalDateTime.class);

    public QProductionResultStep(String variable) {
        this(ProductionResultStep.class, forVariable(variable), INITS);
    }

    public QProductionResultStep(Path<? extends ProductionResultStep> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProductionResultStep(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProductionResultStep(PathMetadata metadata, PathInits inits) {
        this(ProductionResultStep.class, metadata, inits);
    }

    public QProductionResultStep(Class<? extends ProductionResultStep> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.equipment = inits.isInitialized("equipment") ? new com.upmudoum.erp.domain.equipment.entity.QEquipment(forProperty("equipment"), inits.get("equipment")) : null;
        this.process = inits.isInitialized("process") ? new com.upmudoum.erp.domain.process.entity.QErpProcess(forProperty("process")) : null;
        this.productionOrderStep = inits.isInitialized("productionOrderStep") ? new QProductionOrderStep(forProperty("productionOrderStep"), inits.get("productionOrderStep")) : null;
        this.productionResult = inits.isInitialized("productionResult") ? new QProductionResult(forProperty("productionResult"), inits.get("productionResult")) : null;
    }

}

