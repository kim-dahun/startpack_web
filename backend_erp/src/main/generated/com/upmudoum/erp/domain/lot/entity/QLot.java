package com.upmudoum.erp.domain.lot.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLot is a Querydsl query type for Lot
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLot extends EntityPathBase<Lot> {

    private static final long serialVersionUID = -820291193L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLot lot = new QLot("lot");

    public final BooleanPath enabled = createBoolean("enabled");

    public final DatePath<java.time.LocalDate> expiredDate = createDate("expiredDate", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.upmudoum.erp.domain.item.entity.QItem item;

    public final StringPath lotNo = createString("lotNo");

    public final DatePath<java.time.LocalDate> manufacturedDate = createDate("manufacturedDate", java.time.LocalDate.class);

    public QLot(String variable) {
        this(Lot.class, forVariable(variable), INITS);
    }

    public QLot(Path<? extends Lot> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLot(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLot(PathMetadata metadata, PathInits inits) {
        this(Lot.class, metadata, inits);
    }

    public QLot(Class<? extends Lot> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.item = inits.isInitialized("item") ? new com.upmudoum.erp.domain.item.entity.QItem(forProperty("item"), inits.get("item")) : null;
    }

}

