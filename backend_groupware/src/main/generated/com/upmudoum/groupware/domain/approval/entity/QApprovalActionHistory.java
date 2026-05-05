package com.upmudoum.groupware.domain.approval.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QApprovalActionHistory is a Querydsl query type for ApprovalActionHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QApprovalActionHistory extends EntityPathBase<ApprovalActionHistory> {

    private static final long serialVersionUID = -158535810L;

    public static final QApprovalActionHistory approvalActionHistory = new QApprovalActionHistory("approvalActionHistory");

    public final DateTimePath<java.time.Instant> actedAt = createDateTime("actedAt", java.time.Instant.class);

    public final EnumPath<com.upmudoum.groupware.domain.approval.vo.ApprovalActionType> actionType = createEnum("actionType", com.upmudoum.groupware.domain.approval.vo.ApprovalActionType.class);

    public final StringPath actorUserId = createString("actorUserId");

    public final ComparablePath<java.util.UUID> approvalLineId = createComparable("approvalLineId", java.util.UUID.class);

    public final StringPath comCd = createString("comCd");

    public final StringPath comment = createString("comment");

    public final ComparablePath<java.util.UUID> documentId = createComparable("documentId", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public QApprovalActionHistory(String variable) {
        super(ApprovalActionHistory.class, forVariable(variable));
    }

    public QApprovalActionHistory(Path<? extends ApprovalActionHistory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QApprovalActionHistory(PathMetadata metadata) {
        super(ApprovalActionHistory.class, metadata);
    }

}

