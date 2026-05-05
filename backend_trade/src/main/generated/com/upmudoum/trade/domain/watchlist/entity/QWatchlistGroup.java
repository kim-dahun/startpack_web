package com.upmudoum.trade.domain.watchlist.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QWatchlistGroup is a Querydsl query type for WatchlistGroup
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWatchlistGroup extends EntityPathBase<WatchlistGroup> {

    private static final long serialVersionUID = -1644939433L;

    public static final QWatchlistGroup watchlistGroup = new QWatchlistGroup("watchlistGroup");

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    public final StringPath groupName = createString("groupName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath userId = createString("userId");

    public QWatchlistGroup(String variable) {
        super(WatchlistGroup.class, forVariable(variable));
    }

    public QWatchlistGroup(Path<? extends WatchlistGroup> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWatchlistGroup(PathMetadata metadata) {
        super(WatchlistGroup.class, metadata);
    }

}

