package com.upmudoum.groupware.domain.cost.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCostItem is a Querydsl query type for CostItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCostItem extends EntityPathBase<CostItem> {

    private static final long serialVersionUID = 367314983L;

    public static final QCostItem costItem = new QCostItem("costItem");

    public final StringPath comCd = createString("comCd");

    public final StringPath costItemName = createString("costItemName");

    public final BooleanPath deletedYn = createBoolean("deletedYn");

    public final BooleanPath enabled = createBoolean("enabled");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public QCostItem(String variable) {
        super(CostItem.class, forVariable(variable));
    }

    public QCostItem(Path<? extends CostItem> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCostItem(PathMetadata metadata) {
        super(CostItem.class, metadata);
    }

}

