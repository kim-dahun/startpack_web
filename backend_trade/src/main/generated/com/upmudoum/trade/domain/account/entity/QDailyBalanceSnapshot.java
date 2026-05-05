package com.upmudoum.trade.domain.account.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDailyBalanceSnapshot is a Querydsl query type for DailyBalanceSnapshot
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDailyBalanceSnapshot extends EntityPathBase<DailyBalanceSnapshot> {

    private static final long serialVersionUID = 386886508L;

    public static final QDailyBalanceSnapshot dailyBalanceSnapshot = new QDailyBalanceSnapshot("dailyBalanceSnapshot");

    public final StringPath accountNo = createString("accountNo");

    public final DatePath<java.time.LocalDate> baseDate = createDate("baseDate", java.time.LocalDate.class);

    public final DateTimePath<java.time.Instant> capturedAt = createDateTime("capturedAt", java.time.Instant.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<java.math.BigDecimal> profitLossAmount = createNumber("profitLossAmount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> totalAssetAmount = createNumber("totalAssetAmount", java.math.BigDecimal.class);

    public QDailyBalanceSnapshot(String variable) {
        super(DailyBalanceSnapshot.class, forVariable(variable));
    }

    public QDailyBalanceSnapshot(Path<? extends DailyBalanceSnapshot> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDailyBalanceSnapshot(PathMetadata metadata) {
        super(DailyBalanceSnapshot.class, metadata);
    }

}

