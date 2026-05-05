package com.upmudoum.trade.domain.marketdata.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRealtimeReceiveLog is a Querydsl query type for RealtimeReceiveLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRealtimeReceiveLog extends EntityPathBase<RealtimeReceiveLog> {

    private static final long serialVersionUID = 1462329748L;

    public static final QRealtimeReceiveLog realtimeReceiveLog = new QRealtimeReceiveLog("realtimeReceiveLog");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath itemCode = createString("itemCode");

    public final DateTimePath<java.time.Instant> occurredAt = createDateTime("occurredAt", java.time.Instant.class);

    public final StringPath payloadJson = createString("payloadJson");

    public final DateTimePath<java.time.Instant> receivedAt = createDateTime("receivedAt", java.time.Instant.class);

    public final EnumPath<com.upmudoum.trade.domain.marketdata.vo.TradeRealtimeEventType> type = createEnum("type", com.upmudoum.trade.domain.marketdata.vo.TradeRealtimeEventType.class);

    public QRealtimeReceiveLog(String variable) {
        super(RealtimeReceiveLog.class, forVariable(variable));
    }

    public QRealtimeReceiveLog(Path<? extends RealtimeReceiveLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRealtimeReceiveLog(PathMetadata metadata) {
        super(RealtimeReceiveLog.class, metadata);
    }

}

