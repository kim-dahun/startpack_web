package com.upmudoum.erp.domain.item.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QItem is a Querydsl query type for Item
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItem extends EntityPathBase<Item> {

    private static final long serialVersionUID = -912717017L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QItem item = new QItem("item");

    public final BooleanPath active = createBoolean("active");

    public final com.upmudoum.erp.domain.item.vo.QItemCode code;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<com.upmudoum.erp.domain.item.vo.ItemType> itemType = createEnum("itemType", com.upmudoum.erp.domain.item.vo.ItemType.class);

    public final StringPath name = createString("name");

    public final StringPath unit = createString("unit");

    public QItem(String variable) {
        this(Item.class, forVariable(variable), INITS);
    }

    public QItem(Path<? extends Item> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QItem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QItem(PathMetadata metadata, PathInits inits) {
        this(Item.class, metadata, inits);
    }

    public QItem(Class<? extends Item> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.code = inits.isInitialized("code") ? new com.upmudoum.erp.domain.item.vo.QItemCode(forProperty("code")) : null;
    }

}

