package com.upmudoum.user.domain.userposition;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserPosition is a Querydsl query type for UserPosition
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserPosition extends EntityPathBase<UserPosition> {

    private static final long serialVersionUID = 795282828L;

    public static final QUserPosition userPosition = new QUserPosition("userPosition");

    public final com.upmudoum.user.domain.common.QBaseAuditEntity _super = new com.upmudoum.user.domain.common.QBaseAuditEntity(this);

    public final StringPath comCd = createString("comCd");

    //inherited
    public final DateTimePath<java.time.Instant> createdAt = _super.createdAt;

    public final StringPath departmentId = createString("departmentId");

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath positionId = createString("positionId");

    public final BooleanPath primaryYn = createBoolean("primaryYn");

    //inherited
    public final DateTimePath<java.time.Instant> updatedAt = _super.updatedAt;

    public final StringPath userId = createString("userId");

    public final StringPath userPositionId = createString("userPositionId");

    public QUserPosition(String variable) {
        super(UserPosition.class, forVariable(variable));
    }

    public QUserPosition(Path<? extends UserPosition> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserPosition(PathMetadata metadata) {
        super(UserPosition.class, metadata);
    }

}

