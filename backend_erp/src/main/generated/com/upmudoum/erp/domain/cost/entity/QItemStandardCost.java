package com.upmudoum.erp.domain.cost.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QItemStandardCost is a Querydsl query type for ItemStandardCost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItemStandardCost extends EntityPathBase<ItemStandardCost> {

    private static final long serialVersionUID = -1176461397L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QItemStandardCost itemStandardCost = new QItemStandardCost("itemStandardCost");

    public final StringPath currencyCode = createString("currencyCode");

    public final DatePath<java.time.LocalDate> effectiveFrom = createDate("effectiveFrom", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> effectiveTo = createDate("effectiveTo", java.time.LocalDate.class);

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.upmudoum.erp.domain.item.entity.QItem item;

    public final com.upmudoum.erp.domain.accounting.vo.QUnitPrice standardCost;

    public QItemStandardCost(String variable) {
        this(ItemStandardCost.class, forVariable(variable), INITS);
    }

    public QItemStandardCost(Path<? extends ItemStandardCost> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QItemStandardCost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QItemStandardCost(PathMetadata metadata, PathInits inits) {
        this(ItemStandardCost.class, metadata, inits);
    }

    public QItemStandardCost(Class<? extends ItemStandardCost> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.item = inits.isInitialized("item") ? new com.upmudoum.erp.domain.item.entity.QItem(forProperty("item"), inits.get("item")) : null;
        this.standardCost = inits.isInitialized("standardCost") ? new com.upmudoum.erp.domain.accounting.vo.QUnitPrice(forProperty("standardCost")) : null;
    }

}

