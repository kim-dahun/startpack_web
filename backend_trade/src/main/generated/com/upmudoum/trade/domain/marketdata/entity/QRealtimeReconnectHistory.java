package com.upmudoum.trade.domain.marketdata.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRealtimeReconnectHistory is a Querydsl query type for RealtimeReconnectHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRealtimeReconnectHistory extends EntityPathBase<RealtimeReconnectHistory> {

    private static final long serialVersionUID = -506837200L;

    public static final QRealtimeReconnectHistory realtimeReconnectHistory = new QRealtimeReconnectHistory("realtimeReconnectHistory");

    public final DateTimePath<java.time.Instant> attemptedAt = createDateTime("attemptedAt", java.time.Instant.class);

    public final StringPath failureReason = createString("failureReason");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> subscriptionCount = createNumber("subscriptionCount", Integer.class);

    public final BooleanPath success = createBoolean("success");

    public QRealtimeReconnectHistory(String variable) {
        super(RealtimeReconnectHistory.class, forVariable(variable));
    }

    public QRealtimeReconnectHistory(Path<? extends RealtimeReconnectHistory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRealtimeReconnectHistory(PathMetadata metadata) {
        super(RealtimeReconnectHistory.class, metadata);
    }

}

