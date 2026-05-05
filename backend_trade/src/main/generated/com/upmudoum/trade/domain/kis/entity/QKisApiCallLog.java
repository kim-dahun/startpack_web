package com.upmudoum.trade.domain.kis.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QKisApiCallLog is a Querydsl query type for KisApiCallLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QKisApiCallLog extends EntityPathBase<KisApiCallLog> {

    private static final long serialVersionUID = 2008161716L;

    public static final QKisApiCallLog kisApiCallLog = new QKisApiCallLog("kisApiCallLog");

    public final DateTimePath<java.time.Instant> calledAt = createDateTime("calledAt", java.time.Instant.class);

    public final NumberPath<Long> elapsedMillis = createNumber("elapsedMillis", Long.class);

    public final StringPath endpoint = createString("endpoint");

    public final StringPath errorCode = createString("errorCode");

    public final StringPath errorMessage = createString("errorMessage");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath method = createString("method");

    public final NumberPath<Integer> statusCode = createNumber("statusCode", Integer.class);

    public QKisApiCallLog(String variable) {
        super(KisApiCallLog.class, forVariable(variable));
    }

    public QKisApiCallLog(Path<? extends KisApiCallLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QKisApiCallLog(PathMetadata metadata) {
        super(KisApiCallLog.class, metadata);
    }

}

