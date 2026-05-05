package com.upmudoum.trade.domain.kis.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QKisOpenApiToken is a Querydsl query type for KisOpenApiToken
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QKisOpenApiToken extends EntityPathBase<KisOpenApiToken> {

    private static final long serialVersionUID = -1468593263L;

    public static final QKisOpenApiToken kisOpenApiToken = new QKisOpenApiToken("kisOpenApiToken");

    public final StringPath accessToken = createString("accessToken");

    public final DateTimePath<java.time.Instant> expiresAt = createDateTime("expiresAt", java.time.Instant.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.Instant> issuedAt = createDateTime("issuedAt", java.time.Instant.class);

    public final EnumPath<com.upmudoum.trade.domain.kis.vo.KisTradeMode> tradeMode = createEnum("tradeMode", com.upmudoum.trade.domain.kis.vo.KisTradeMode.class);

    public final DateTimePath<java.time.Instant> updatedAt = createDateTime("updatedAt", java.time.Instant.class);

    public QKisOpenApiToken(String variable) {
        super(KisOpenApiToken.class, forVariable(variable));
    }

    public QKisOpenApiToken(Path<? extends KisOpenApiToken> path) {
        super(path.getType(), path.getMetadata());
    }

    public QKisOpenApiToken(PathMetadata metadata) {
        super(KisOpenApiToken.class, metadata);
    }

}

