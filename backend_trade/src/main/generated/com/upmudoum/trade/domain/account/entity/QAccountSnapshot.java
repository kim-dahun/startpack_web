package com.upmudoum.trade.domain.account.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAccountSnapshot is a Querydsl query type for AccountSnapshot
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAccountSnapshot extends EntityPathBase<AccountSnapshot> {

    private static final long serialVersionUID = 1435483628L;

    public static final QAccountSnapshot accountSnapshot = new QAccountSnapshot("accountSnapshot");

    public final StringPath accountName = createString("accountName");

    public final StringPath accountNo = createString("accountNo");

    public final DateTimePath<java.time.Instant> capturedAt = createDateTime("capturedAt", java.time.Instant.class);

    public final NumberPath<java.math.BigDecimal> cashAmount = createNumber("cashAmount", java.math.BigDecimal.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<java.math.BigDecimal> totalAssetAmount = createNumber("totalAssetAmount", java.math.BigDecimal.class);

    public QAccountSnapshot(String variable) {
        super(AccountSnapshot.class, forVariable(variable));
    }

    public QAccountSnapshot(Path<? extends AccountSnapshot> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAccountSnapshot(PathMetadata metadata) {
        super(AccountSnapshot.class, metadata);
    }

}

