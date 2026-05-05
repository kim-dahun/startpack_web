package com.upmudoum.trade.domain.account.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAccountPositionSnapshot is a Querydsl query type for AccountPositionSnapshot
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAccountPositionSnapshot extends EntityPathBase<AccountPositionSnapshot> {

    private static final long serialVersionUID = -728573387L;

    public static final QAccountPositionSnapshot accountPositionSnapshot = new QAccountPositionSnapshot("accountPositionSnapshot");

    public final StringPath accountNo = createString("accountNo");

    public final NumberPath<java.math.BigDecimal> averagePrice = createNumber("averagePrice", java.math.BigDecimal.class);

    public final DateTimePath<java.time.Instant> capturedAt = createDateTime("capturedAt", java.time.Instant.class);

    public final NumberPath<java.math.BigDecimal> currentPrice = createNumber("currentPrice", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> evaluationAmount = createNumber("evaluationAmount", java.math.BigDecimal.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath itemCode = createString("itemCode");

    public final StringPath itemName = createString("itemName");

    public final NumberPath<Long> orderableQuantity = createNumber("orderableQuantity", Long.class);

    public final NumberPath<java.math.BigDecimal> profitLossAmount = createNumber("profitLossAmount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> profitLossRate = createNumber("profitLossRate", java.math.BigDecimal.class);

    public final NumberPath<Long> quantity = createNumber("quantity", Long.class);

    public QAccountPositionSnapshot(String variable) {
        super(AccountPositionSnapshot.class, forVariable(variable));
    }

    public QAccountPositionSnapshot(Path<? extends AccountPositionSnapshot> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAccountPositionSnapshot(PathMetadata metadata) {
        super(AccountPositionSnapshot.class, metadata);
    }

}

