package com.upmudoum.trade.domain.trade.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTradeHistory is a Querydsl query type for TradeHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTradeHistory extends EntityPathBase<TradeHistory> {

    private static final long serialVersionUID = 425576972L;

    public static final QTradeHistory tradeHistory = new QTradeHistory("tradeHistory");

    public final StringPath accountNo = createString("accountNo");

    public final NumberPath<java.math.BigDecimal> amount = createNumber("amount", java.math.BigDecimal.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath idempotencyKey = createString("idempotencyKey");

    public final StringPath itemCode = createString("itemCode");

    public final StringPath itemName = createString("itemName");

    public final NumberPath<java.math.BigDecimal> price = createNumber("price", java.math.BigDecimal.class);

    public final NumberPath<Long> quantity = createNumber("quantity", Long.class);

    public final EnumPath<com.upmudoum.trade.domain.trade.vo.TradeSide> side = createEnum("side", com.upmudoum.trade.domain.trade.vo.TradeSide.class);

    public final DateTimePath<java.time.Instant> tradedAt = createDateTime("tradedAt", java.time.Instant.class);

    public QTradeHistory(String variable) {
        super(TradeHistory.class, forVariable(variable));
    }

    public QTradeHistory(Path<? extends TradeHistory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTradeHistory(PathMetadata metadata) {
        super(TradeHistory.class, metadata);
    }

}

