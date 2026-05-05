package com.upmudoum.erp.domain.bom.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBom is a Querydsl query type for Bom
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBom extends EntityPathBase<Bom> {

    private static final long serialVersionUID = -963260889L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBom bom = new QBom("bom");

    public final QBomVersion defaultBomVersion;

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.upmudoum.erp.domain.item.entity.QItem parentItem;

    public QBom(String variable) {
        this(Bom.class, forVariable(variable), INITS);
    }

    public QBom(Path<? extends Bom> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBom(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBom(PathMetadata metadata, PathInits inits) {
        this(Bom.class, metadata, inits);
    }

    public QBom(Class<? extends Bom> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.defaultBomVersion = inits.isInitialized("defaultBomVersion") ? new QBomVersion(forProperty("defaultBomVersion"), inits.get("defaultBomVersion")) : null;
        this.parentItem = inits.isInitialized("parentItem") ? new com.upmudoum.erp.domain.item.entity.QItem(forProperty("parentItem"), inits.get("parentItem")) : null;
    }

}

