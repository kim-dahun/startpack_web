package com.upmudoum.trade.domain.master.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTradeMasterImportLock is a Querydsl query type for TradeMasterImportLock
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTradeMasterImportLock extends EntityPathBase<TradeMasterImportLock> {

    private static final long serialVersionUID = -1721158382L;

    public static final QTradeMasterImportLock tradeMasterImportLock = new QTradeMasterImportLock("tradeMasterImportLock");

    public final NumberPath<Long> historyId = createNumber("historyId", Long.class);

    public final EnumPath<com.upmudoum.trade.domain.master.vo.TradeMasterImportStatus> importStatus = createEnum("importStatus", com.upmudoum.trade.domain.master.vo.TradeMasterImportStatus.class);

    public final DateTimePath<java.time.Instant> lastRequestedAt = createDateTime("lastRequestedAt", java.time.Instant.class);

    public final EnumPath<com.upmudoum.trade.domain.master.vo.TradeMasterType> masterType = createEnum("masterType", com.upmudoum.trade.domain.master.vo.TradeMasterType.class);

    public final DateTimePath<java.time.Instant> updatedAt = createDateTime("updatedAt", java.time.Instant.class);

    public QTradeMasterImportLock(String variable) {
        super(TradeMasterImportLock.class, forVariable(variable));
    }

    public QTradeMasterImportLock(Path<? extends TradeMasterImportLock> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTradeMasterImportLock(PathMetadata metadata) {
        super(TradeMasterImportLock.class, metadata);
    }

}

