package com.upmudoum.erp.domain.accounting.vo;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUnitPrice is a Querydsl query type for UnitPrice
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QUnitPrice extends BeanPath<UnitPrice> {

    private static final long serialVersionUID = -2058536923L;

    public static final QUnitPrice unitPrice = new QUnitPrice("unitPrice");

    public final NumberPath<java.math.BigDecimal> value = createNumber("value", java.math.BigDecimal.class);

    public QUnitPrice(String variable) {
        super(UnitPrice.class, forVariable(variable));
    }

    public QUnitPrice(Path<? extends UnitPrice> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUnitPrice(PathMetadata metadata) {
        super(UnitPrice.class, metadata);
    }

}

