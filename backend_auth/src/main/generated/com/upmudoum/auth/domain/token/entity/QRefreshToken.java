package com.upmudoum.auth.domain.token.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRefreshToken is a Querydsl query type for RefreshToken
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRefreshToken extends EntityPathBase<RefreshToken> {

    private static final long serialVersionUID = -934580203L;

    public static final QRefreshToken refreshToken = new QRefreshToken("refreshToken");

    public final DateTimePath<java.time.Instant> expiresAt = createDateTime("expiresAt", java.time.Instant.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath replacedByTokenId = createString("replacedByTokenId");

    public final DateTimePath<java.time.Instant> revokedAt = createDateTime("revokedAt", java.time.Instant.class);

    public final StringPath revokeReason = createString("revokeReason");

    public final StringPath subject = createString("subject");

    public final StringPath tokenId = createString("tokenId");

    public final EnumPath<com.upmudoum.auth.domain.token.vo.TokenType> tokenType = createEnum("tokenType", com.upmudoum.auth.domain.token.vo.TokenType.class);

    public QRefreshToken(String variable) {
        super(RefreshToken.class, forVariable(variable));
    }

    public QRefreshToken(Path<? extends RefreshToken> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRefreshToken(PathMetadata metadata) {
        super(RefreshToken.class, metadata);
    }

}

