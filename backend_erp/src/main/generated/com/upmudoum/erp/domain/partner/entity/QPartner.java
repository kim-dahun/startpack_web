package com.upmudoum.erp.domain.partner.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPartner is a Querydsl query type for Partner
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPartner extends EntityPathBase<Partner> {

    private static final long serialVersionUID = -162226265L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPartner partner = new QPartner("partner");

    public final StringPath businessNumber = createString("businessNumber");

    public final com.upmudoum.erp.domain.partner.vo.QPartnerCode code;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath partnerType = createString("partnerType");

    public final EnumPath<com.upmudoum.erp.domain.partner.vo.PartnerStatus> status = createEnum("status", com.upmudoum.erp.domain.partner.vo.PartnerStatus.class);

    public QPartner(String variable) {
        this(Partner.class, forVariable(variable), INITS);
    }

    public QPartner(Path<? extends Partner> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPartner(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPartner(PathMetadata metadata, PathInits inits) {
        this(Partner.class, metadata, inits);
    }

    public QPartner(Class<? extends Partner> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.code = inits.isInitialized("code") ? new com.upmudoum.erp.domain.partner.vo.QPartnerCode(forProperty("code")) : null;
    }

}

