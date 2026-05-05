package com.upmudoum.erp.domain.cost.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QItemActualCostHistory is a Querydsl query type for ItemActualCostHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItemActualCostHistory extends EntityPathBase<ItemActualCostHistory> {

    private static final long serialVersionUID = -1312294216L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QItemActualCostHistory itemActualCostHistory = new QItemActualCostHistory("itemActualCostHistory");

    public final DateTimePath<java.time.LocalDateTime> appliedAt = createDateTime("appliedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.upmudoum.erp.domain.item.entity.QItem item;

    public final com.upmudoum.erp.domain.inventory.vo.QQuantity quantity;

    public final NumberPath<Long> referenceId = createNumber("referenceId", Long.class);

    public final EnumPath<com.upmudoum.erp.domain.cost.vo.ActualCostReferenceType> referenceType = createEnum("referenceType", com.upmudoum.erp.domain.cost.vo.ActualCostReferenceType.class);

    public final com.upmudoum.erp.domain.accounting.vo.QUnitPrice unitCost;

    public QItemActualCostHistory(String variable) {
        this(ItemActualCostHistory.class, forVariable(variable), INITS);
    }

    public QItemActualCostHistory(Path<? extends ItemActualCostHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QItemActualCostHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QItemActualCostHistory(PathMetadata metadata, PathInits inits) {
        this(ItemActualCostHistory.class, metadata, inits);
    }

    public QItemActualCostHistory(Class<? extends ItemActualCostHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.item = inits.isInitialized("item") ? new com.upmudoum.erp.domain.item.entity.QItem(forProperty("item"), inits.get("item")) : null;
        this.quantity = inits.isInitialized("quantity") ? new com.upmudoum.erp.domain.inventory.vo.QQuantity(forProperty("quantity")) : null;
        this.unitCost = inits.isInitialized("unitCost") ? new com.upmudoum.erp.domain.accounting.vo.QUnitPrice(forProperty("unitCost")) : null;
    }

}

