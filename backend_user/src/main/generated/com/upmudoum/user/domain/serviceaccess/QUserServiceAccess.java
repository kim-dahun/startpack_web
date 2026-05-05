package com.upmudoum.user.domain.serviceaccess;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserServiceAccess is a Querydsl query type for UserServiceAccess
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserServiceAccess extends EntityPathBase<UserServiceAccess> {

    private static final long serialVersionUID = 1784200983L;

    public static final QUserServiceAccess userServiceAccess = new QUserServiceAccess("userServiceAccess");

    public final com.upmudoum.user.domain.common.QBaseAuditEntity _super = new com.upmudoum.user.domain.common.QBaseAuditEntity(this);

    public final BooleanPath accessible = createBoolean("accessible");

    public final StringPath comCd = createString("comCd");

    //inherited
    public final DateTimePath<java.time.Instant> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<ServiceId> serviceId = createEnum("serviceId", ServiceId.class);

    //inherited
    public final DateTimePath<java.time.Instant> updatedAt = _super.updatedAt;

    public final StringPath userId = createString("userId");

    public QUserServiceAccess(String variable) {
        super(UserServiceAccess.class, forVariable(variable));
    }

    public QUserServiceAccess(Path<? extends UserServiceAccess> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserServiceAccess(PathMetadata metadata) {
        super(UserServiceAccess.class, metadata);
    }

}

