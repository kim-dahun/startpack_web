package com.upmudoum.user.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserAccount is a Querydsl query type for UserAccount
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserAccount extends EntityPathBase<UserAccount> {

    private static final long serialVersionUID = -361982271L;

    public static final QUserAccount userAccount = new QUserAccount("userAccount");

    public final com.upmudoum.user.domain.common.QBaseAuditEntity _super = new com.upmudoum.user.domain.common.QBaseAuditEntity(this);

    public final StringPath address = createString("address");

    public final StringPath comCd = createString("comCd");

    //inherited
    public final DateTimePath<java.time.Instant> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath jobGradeId = createString("jobGradeId");

    public final DateTimePath<java.time.Instant> lastLoginAt = createDateTime("lastLoginAt", java.time.Instant.class);

    public final StringPath passwordHash = createString("passwordHash");

    public final StringPath phone = createString("phone");

    public final EnumPath<UserStatus> status = createEnum("status", UserStatus.class);

    //inherited
    public final DateTimePath<java.time.Instant> updatedAt = _super.updatedAt;

    public final StringPath userId = createString("userId");

    public final StringPath userName = createString("userName");

    public QUserAccount(String variable) {
        super(UserAccount.class, forVariable(variable));
    }

    public QUserAccount(Path<? extends UserAccount> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserAccount(PathMetadata metadata) {
        super(UserAccount.class, metadata);
    }

}

