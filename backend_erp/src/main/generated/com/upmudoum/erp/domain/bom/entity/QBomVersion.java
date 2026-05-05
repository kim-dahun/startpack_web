package com.upmudoum.erp.domain.bom.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBomVersion is a Querydsl query type for BomVersion
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBomVersion extends EntityPathBase<BomVersion> {

    private static final long serialVersionUID = 358743217L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBomVersion bomVersion = new QBomVersion("bomVersion");

    public final QBom bom;

    public final DatePath<java.time.LocalDate> effectiveFrom = createDate("effectiveFrom", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> effectiveTo = createDate("effectiveTo", java.time.LocalDate.class);

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath versionNo = createString("versionNo");

    public QBomVersion(String variable) {
        this(BomVersion.class, forVariable(variable), INITS);
    }

    public QBomVersion(Path<? extends BomVersion> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBomVersion(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBomVersion(PathMetadata metadata, PathInits inits) {
        this(BomVersion.class, metadata, inits);
    }

    public QBomVersion(Class<? extends BomVersion> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.bom = inits.isInitialized("bom") ? new QBom(forProperty("bom"), inits.get("bom")) : null;
    }

}

