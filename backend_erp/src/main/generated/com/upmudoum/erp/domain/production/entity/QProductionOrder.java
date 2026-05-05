package com.upmudoum.erp.domain.production.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductionOrder is a Querydsl query type for ProductionOrder
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductionOrder extends EntityPathBase<ProductionOrder> {

    private static final long serialVersionUID = 915531803L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProductionOrder productionOrder = new QProductionOrder("productionOrder");

    public final com.upmudoum.erp.domain.bom.entity.QBomVersion bomVersion;

    public final DatePath<java.time.LocalDate> dueDate = createDate("dueDate", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.upmudoum.erp.domain.item.entity.QItem item;

    public final StringPath orderNo = createString("orderNo");

    public final com.upmudoum.erp.domain.equipment.entity.QEquipment plannedEquipment;

    public final com.upmudoum.erp.domain.process.entity.QErpProcess plannedProcess;

    public final com.upmudoum.erp.domain.inventory.vo.QQuantity plannedQuantity;

    public final com.upmudoum.erp.domain.route.entity.QRoute route;

    public final EnumPath<com.upmudoum.erp.domain.production.vo.ProductionOrderStatus> status = createEnum("status", com.upmudoum.erp.domain.production.vo.ProductionOrderStatus.class);

    public QProductionOrder(String variable) {
        this(ProductionOrder.class, forVariable(variable), INITS);
    }

    public QProductionOrder(Path<? extends ProductionOrder> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProductionOrder(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProductionOrder(PathMetadata metadata, PathInits inits) {
        this(ProductionOrder.class, metadata, inits);
    }

    public QProductionOrder(Class<? extends ProductionOrder> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.bomVersion = inits.isInitialized("bomVersion") ? new com.upmudoum.erp.domain.bom.entity.QBomVersion(forProperty("bomVersion"), inits.get("bomVersion")) : null;
        this.item = inits.isInitialized("item") ? new com.upmudoum.erp.domain.item.entity.QItem(forProperty("item"), inits.get("item")) : null;
        this.plannedEquipment = inits.isInitialized("plannedEquipment") ? new com.upmudoum.erp.domain.equipment.entity.QEquipment(forProperty("plannedEquipment"), inits.get("plannedEquipment")) : null;
        this.plannedProcess = inits.isInitialized("plannedProcess") ? new com.upmudoum.erp.domain.process.entity.QErpProcess(forProperty("plannedProcess")) : null;
        this.plannedQuantity = inits.isInitialized("plannedQuantity") ? new com.upmudoum.erp.domain.inventory.vo.QQuantity(forProperty("plannedQuantity")) : null;
        this.route = inits.isInitialized("route") ? new com.upmudoum.erp.domain.route.entity.QRoute(forProperty("route"), inits.get("route")) : null;
    }

}

