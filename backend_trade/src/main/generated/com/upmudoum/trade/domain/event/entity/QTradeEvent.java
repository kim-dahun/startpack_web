package com.upmudoum.trade.domain.event.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTradeEvent is a Querydsl query type for TradeEvent
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTradeEvent extends EntityPathBase<TradeEvent> {

    private static final long serialVersionUID = 57685448L;

    public static final QTradeEvent tradeEvent = new QTradeEvent("tradeEvent");

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    public final StringPath description = createString("description");

    public final DatePath<java.time.LocalDate> eventDate = createDate("eventDate", java.time.LocalDate.class);

    public final EnumPath<com.upmudoum.trade.domain.event.vo.TradeEventType> eventType = createEnum("eventType", com.upmudoum.trade.domain.event.vo.TradeEventType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath itemCode = createString("itemCode");

    public final StringPath rawJson = createString("rawJson");

    public final StringPath title = createString("title");

    public QTradeEvent(String variable) {
        super(TradeEvent.class, forVariable(variable));
    }

    public QTradeEvent(Path<? extends TradeEvent> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTradeEvent(PathMetadata metadata) {
        super(TradeEvent.class, metadata);
    }

}

