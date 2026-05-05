package com.upmudoum.erp.domain.accounting.vo;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMoneyAmount is a Querydsl query type for MoneyAmount
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QMoneyAmount extends BeanPath<MoneyAmount> {

    private static final long serialVersionUID = -1465365704L;

    public static final QMoneyAmount moneyAmount = new QMoneyAmount("moneyAmount");

    public final NumberPath<java.math.BigDecimal> value = createNumber("value", java.math.BigDecimal.class);

    public QMoneyAmount(String variable) {
        super(MoneyAmount.class, forVariable(variable));
    }

    public QMoneyAmount(Path<? extends MoneyAmount> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMoneyAmount(PathMetadata metadata) {
        super(MoneyAmount.class, metadata);
    }

}

