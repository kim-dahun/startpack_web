package com.upmudoum.trade.domain.master.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTradeMasterImportHistory is a Querydsl query type for TradeMasterImportHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTradeMasterImportHistory extends EntityPathBase<TradeMasterImportHistory> {

    private static final long serialVersionUID = -1121446067L;

    public static final QTradeMasterImportHistory tradeMasterImportHistory = new QTradeMasterImportHistory("tradeMasterImportHistory");

    public final StringPath failureReason = createString("failureReason");

    public final DateTimePath<java.time.Instant> finishedAt = createDateTime("finishedAt", java.time.Instant.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> importedCount = createNumber("importedCount", Integer.class);

    public final EnumPath<com.upmudoum.trade.domain.master.vo.TradeMasterImportStatus> importStatus = createEnum("importStatus", com.upmudoum.trade.domain.master.vo.TradeMasterImportStatus.class);

    public final EnumPath<com.upmudoum.trade.domain.master.vo.TradeMasterType> masterType = createEnum("masterType", com.upmudoum.trade.domain.master.vo.TradeMasterType.class);

    public final StringPath sourceFileName = createString("sourceFileName");

    public final StringPath sourceVersion = createString("sourceVersion");

    public final DateTimePath<java.time.Instant> startedAt = createDateTime("startedAt", java.time.Instant.class);

    public final BooleanPath success = createBoolean("success");

    public QTradeMasterImportHistory(String variable) {
        super(TradeMasterImportHistory.class, forVariable(variable));
    }

    public QTradeMasterImportHistory(Path<? extends TradeMasterImportHistory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTradeMasterImportHistory(PathMetadata metadata) {
        super(TradeMasterImportHistory.class, metadata);
    }

}

