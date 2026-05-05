package com.upmudoum.erp.domain.item.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QItemCategoryAssignment is a Querydsl query type for ItemCategoryAssignment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItemCategoryAssignment extends EntityPathBase<ItemCategoryAssignment> {

    private static final long serialVersionUID = 351495666L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QItemCategoryAssignment itemCategoryAssignment = new QItemCategoryAssignment("itemCategoryAssignment");

    public final QItemCategory category;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QItem item;

    public QItemCategoryAssignment(String variable) {
        this(ItemCategoryAssignment.class, forVariable(variable), INITS);
    }

    public QItemCategoryAssignment(Path<? extends ItemCategoryAssignment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QItemCategoryAssignment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QItemCategoryAssignment(PathMetadata metadata, PathInits inits) {
        this(ItemCategoryAssignment.class, metadata, inits);
    }

    public QItemCategoryAssignment(Class<? extends ItemCategoryAssignment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new QItemCategory(forProperty("category"), inits.get("category")) : null;
        this.item = inits.isInitialized("item") ? new QItem(forProperty("item"), inits.get("item")) : null;
    }

}

