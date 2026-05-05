package com.upmudoum.user.domain.group;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserGroupMember is a Querydsl query type for UserGroupMember
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserGroupMember extends EntityPathBase<UserGroupMember> {

    private static final long serialVersionUID = -1990665155L;

    public static final QUserGroupMember userGroupMember = new QUserGroupMember("userGroupMember");

    public final com.upmudoum.user.domain.common.QBaseAuditEntity _super = new com.upmudoum.user.domain.common.QBaseAuditEntity(this);

    public final StringPath comCd = createString("comCd");

    //inherited
    public final DateTimePath<java.time.Instant> createdAt = _super.createdAt;

    public final StringPath groupId = createString("groupId");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath serviceId = createString("serviceId");

    //inherited
    public final DateTimePath<java.time.Instant> updatedAt = _super.updatedAt;

    public final StringPath userId = createString("userId");

    public QUserGroupMember(String variable) {
        super(UserGroupMember.class, forVariable(variable));
    }

    public QUserGroupMember(Path<? extends UserGroupMember> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserGroupMember(PathMetadata metadata) {
        super(UserGroupMember.class, metadata);
    }

}

