package com.upmudoum.trade.domain.analysis.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMarketRankingSnapshot is a Querydsl query type for MarketRankingSnapshot
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMarketRankingSnapshot extends EntityPathBase<MarketRankingSnapshot> {

    private static final long serialVersionUID = 2040693856L;

    public static final QMarketRankingSnapshot marketRankingSnapshot = new QMarketRankingSnapshot("marketRankingSnapshot");

    public final DatePath<java.time.LocalDate> baseDate = createDate("baseDate", java.time.LocalDate.class);

    public final DateTimePath<java.time.Instant> capturedAt = createDateTime("capturedAt", java.time.Instant.class);

    public final NumberPath<java.math.BigDecimal> changeAmount = createNumber("changeAmount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> changeRate = createNumber("changeRate", java.math.BigDecimal.class);

    public final StringPath countryCode = createString("countryCode");

    public final NumberPath<java.math.BigDecimal> currentPrice = createNumber("currentPrice", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> high52WeekPrice = createNumber("high52WeekPrice", java.math.BigDecimal.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath itemCode = createString("itemCode");

    public final StringPath itemName = createString("itemName");

    public final NumberPath<java.math.BigDecimal> low52WeekPrice = createNumber("low52WeekPrice", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> marketCap = createNumber("marketCap", java.math.BigDecimal.class);

    public final StringPath marketCode = createString("marketCode");

    public final EnumPath<com.upmudoum.trade.domain.master.vo.TradeMasterType> masterType = createEnum("masterType", com.upmudoum.trade.domain.master.vo.TradeMasterType.class);

    public final StringPath sectorName = createString("sectorName");

    public final NumberPath<java.math.BigDecimal> turnover = createNumber("turnover", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> volatility = createNumber("volatility", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> volume = createNumber("volume", java.math.BigDecimal.class);

    public QMarketRankingSnapshot(String variable) {
        super(MarketRankingSnapshot.class, forVariable(variable));
    }

    public QMarketRankingSnapshot(Path<? extends MarketRankingSnapshot> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMarketRankingSnapshot(PathMetadata metadata) {
        super(MarketRankingSnapshot.class, metadata);
    }

}

