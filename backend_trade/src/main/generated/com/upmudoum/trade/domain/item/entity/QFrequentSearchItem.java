package com.upmudoum.trade.domain.item.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFrequentSearchItem is a Querydsl query type for FrequentSearchItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFrequentSearchItem extends EntityPathBase<FrequentSearchItem> {

    private static final long serialVersionUID = 1108809054L;

    public static final QFrequentSearchItem frequentSearchItem = new QFrequentSearchItem("frequentSearchItem");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath itemCode = createString("itemCode");

    public final StringPath itemName = createString("itemName");

    public final DateTimePath<java.time.Instant> lastSearchedAt = createDateTime("lastSearchedAt", java.time.Instant.class);

    public final StringPath marketCode = createString("marketCode");

    public final NumberPath<Long> searchCount = createNumber("searchCount", Long.class);

    public final StringPath userId = createString("userId");

    public QFrequentSearchItem(String variable) {
        super(FrequentSearchItem.class, forVariable(variable));
    }

    public QFrequentSearchItem(Path<? extends FrequentSearchItem> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFrequentSearchItem(PathMetadata metadata) {
        super(FrequentSearchItem.class, metadata);
    }

}

