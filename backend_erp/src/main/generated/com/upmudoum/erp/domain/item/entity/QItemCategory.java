package com.upmudoum.erp.domain.item.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QItemCategory is a Querydsl query type for ItemCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItemCategory extends EntityPathBase<ItemCategory> {

    private static final long serialVersionUID = -1364716219L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QItemCategory itemCategory = new QItemCategory("itemCategory");

    public final BooleanPath active = createBoolean("active");

    public final StringPath code = createString("code");

    public final NumberPath<Integer> depth = createNumber("depth", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final QItemCategory parentCategory;

    public QItemCategory(String variable) {
        this(ItemCategory.class, forVariable(variable), INITS);
    }

    public QItemCategory(Path<? extends ItemCategory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QItemCategory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QItemCategory(PathMetadata metadata, PathInits inits) {
        this(ItemCategory.class, metadata, inits);
    }

    public QItemCategory(Class<? extends ItemCategory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.parentCategory = inits.isInitialized("parentCategory") ? new QItemCategory(forProperty("parentCategory"), inits.get("parentCategory")) : null;
    }

}

