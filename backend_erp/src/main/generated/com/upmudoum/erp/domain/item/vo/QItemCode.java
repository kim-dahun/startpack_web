package com.upmudoum.erp.domain.item.vo;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QItemCode is a Querydsl query type for ItemCode
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QItemCode extends BeanPath<ItemCode> {

    private static final long serialVersionUID = -74812386L;

    public static final QItemCode itemCode = new QItemCode("itemCode");

    public final StringPath value = createString("value");

    public QItemCode(String variable) {
        super(ItemCode.class, forVariable(variable));
    }

    public QItemCode(Path<? extends ItemCode> path) {
        super(path.getType(), path.getMetadata());
    }

    public QItemCode(PathMetadata metadata) {
        super(ItemCode.class, metadata);
    }

}

