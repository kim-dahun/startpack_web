package com.upmudoum.erp.domain.route.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRouteStep is a Querydsl query type for RouteStep
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRouteStep extends EntityPathBase<RouteStep> {

    private static final long serialVersionUID = -741892749L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRouteStep routeStep = new QRouteStep("routeStep");

    public final com.upmudoum.erp.domain.equipment.entity.QEquipment defaultEquipment;

    public final StringPath description = createString("description");

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.upmudoum.erp.domain.process.entity.QErpProcess process;

    public final QRoute route;

    public final NumberPath<Integer> sequenceNo = createNumber("sequenceNo", Integer.class);

    public final NumberPath<Integer> standardLeadTimeMinutes = createNumber("standardLeadTimeMinutes", Integer.class);

    public QRouteStep(String variable) {
        this(RouteStep.class, forVariable(variable), INITS);
    }

    public QRouteStep(Path<? extends RouteStep> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRouteStep(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRouteStep(PathMetadata metadata, PathInits inits) {
        this(RouteStep.class, metadata, inits);
    }

    public QRouteStep(Class<? extends RouteStep> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.defaultEquipment = inits.isInitialized("defaultEquipment") ? new com.upmudoum.erp.domain.equipment.entity.QEquipment(forProperty("defaultEquipment"), inits.get("defaultEquipment")) : null;
        this.process = inits.isInitialized("process") ? new com.upmudoum.erp.domain.process.entity.QErpProcess(forProperty("process")) : null;
        this.route = inits.isInitialized("route") ? new QRoute(forProperty("route"), inits.get("route")) : null;
    }

}

