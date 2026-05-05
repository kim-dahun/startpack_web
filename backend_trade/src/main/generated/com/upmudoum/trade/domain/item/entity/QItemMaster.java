package com.upmudoum.trade.domain.item.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QItemMaster is a Querydsl query type for ItemMaster
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItemMaster extends EntityPathBase<ItemMaster> {

    private static final long serialVersionUID = -222598294L;

    public static final QItemMaster itemMaster = new QItemMaster("itemMaster");

    public final NumberPath<java.math.BigDecimal> bps = createNumber("bps", java.math.BigDecimal.class);

    public final StringPath countryCode = createString("countryCode");

    public final NumberPath<java.math.BigDecimal> eps = createNumber("eps", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> high52WeekPrice = createNumber("high52WeekPrice", java.math.BigDecimal.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath itemCode = createString("itemCode");

    public final StringPath itemName = createString("itemName");

    public final NumberPath<java.math.BigDecimal> low52WeekPrice = createNumber("low52WeekPrice", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> marketCap = createNumber("marketCap", java.math.BigDecimal.class);

    public final StringPath marketCode = createString("marketCode");

    public final EnumPath<com.upmudoum.trade.domain.master.vo.TradeMasterType> masterType = createEnum("masterType", com.upmudoum.trade.domain.master.vo.TradeMasterType.class);

    public final NumberPath<java.math.BigDecimal> operatingProfit = createNumber("operatingProfit", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> pbr = createNumber("pbr", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> per = createNumber("per", java.math.BigDecimal.class);

    public final StringPath rawJson = createString("rawJson");

    public final NumberPath<java.math.BigDecimal> salesAmount = createNumber("salesAmount", java.math.BigDecimal.class);

    public final StringPath sectorName = createString("sectorName");

    public final DateTimePath<java.time.Instant> sourceDownloadedAt = createDateTime("sourceDownloadedAt", java.time.Instant.class);

    public final StringPath sourceFileName = createString("sourceFileName");

    public final StringPath sourceVersion = createString("sourceVersion");

    public final DateTimePath<java.time.Instant> syncedAt = createDateTime("syncedAt", java.time.Instant.class);

    public QItemMaster(String variable) {
        super(ItemMaster.class, forVariable(variable));
    }

    public QItemMaster(Path<? extends ItemMaster> path) {
        super(path.getType(), path.getMetadata());
    }

    public QItemMaster(PathMetadata metadata) {
        super(ItemMaster.class, metadata);
    }

}

