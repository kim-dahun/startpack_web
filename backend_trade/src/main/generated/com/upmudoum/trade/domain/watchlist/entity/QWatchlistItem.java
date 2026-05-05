package com.upmudoum.trade.domain.watchlist.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QWatchlistItem is a Querydsl query type for WatchlistItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWatchlistItem extends EntityPathBase<WatchlistItem> {

    private static final long serialVersionUID = -1161380037L;

    public static final QWatchlistItem watchlistItem = new QWatchlistItem("watchlistItem");

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    public final NumberPath<Long> groupId = createNumber("groupId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath itemCode = createString("itemCode");

    public final StringPath itemName = createString("itemName");

    public final StringPath memo = createString("memo");

    public final StringPath tags = createString("tags");

    public final StringPath userId = createString("userId");

    public QWatchlistItem(String variable) {
        super(WatchlistItem.class, forVariable(variable));
    }

    public QWatchlistItem(Path<? extends WatchlistItem> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWatchlistItem(PathMetadata metadata) {
        super(WatchlistItem.class, metadata);
    }

}

