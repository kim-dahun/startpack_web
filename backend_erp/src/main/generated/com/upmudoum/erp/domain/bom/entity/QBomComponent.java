package com.upmudoum.erp.domain.bom.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBomComponent is a Querydsl query type for BomComponent
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBomComponent extends EntityPathBase<BomComponent> {

    private static final long serialVersionUID = 1162046102L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBomComponent bomComponent = new QBomComponent("bomComponent");

    public final QBomVersion bomVersion;

    public final com.upmudoum.erp.domain.item.entity.QItem componentItem;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<java.math.BigDecimal> lossRate = createNumber("lossRate", java.math.BigDecimal.class);

    public final com.upmudoum.erp.domain.inventory.vo.QQuantity requiredQuantity;

    public QBomComponent(String variable) {
        this(BomComponent.class, forVariable(variable), INITS);
    }

    public QBomComponent(Path<? extends BomComponent> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBomComponent(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBomComponent(PathMetadata metadata, PathInits inits) {
        this(BomComponent.class, metadata, inits);
    }

    public QBomComponent(Class<? extends BomComponent> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.bomVersion = inits.isInitialized("bomVersion") ? new QBomVersion(forProperty("bomVersion"), inits.get("bomVersion")) : null;
        this.componentItem = inits.isInitialized("componentItem") ? new com.upmudoum.erp.domain.item.entity.QItem(forProperty("componentItem"), inits.get("componentItem")) : null;
        this.requiredQuantity = inits.isInitialized("requiredQuantity") ? new com.upmudoum.erp.domain.inventory.vo.QQuantity(forProperty("requiredQuantity")) : null;
    }

}

