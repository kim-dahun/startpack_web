package com.upmudoum.groupware.domain.approval.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QApprovalLine is a Querydsl query type for ApprovalLine
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QApprovalLine extends EntityPathBase<ApprovalLine> {

    private static final long serialVersionUID = 1641756148L;

    public static final QApprovalLine approvalLine = new QApprovalLine("approvalLine");

    public final EnumPath<com.upmudoum.groupware.domain.approval.vo.ApprovalRoleType> approvalRoleType = createEnum("approvalRoleType", com.upmudoum.groupware.domain.approval.vo.ApprovalRoleType.class);

    public final StringPath comCd = createString("comCd");

    public final EnumPath<com.upmudoum.groupware.domain.approval.vo.ApprovalDecisionMode> decisionMode = createEnum("decisionMode", com.upmudoum.groupware.domain.approval.vo.ApprovalDecisionMode.class);

    public final ComparablePath<java.util.UUID> documentId = createComparable("documentId", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final NumberPath<Integer> lineSeq = createNumber("lineSeq", Integer.class);

    public final EnumPath<com.upmudoum.groupware.domain.approval.vo.ApprovalLineStage> lineStage = createEnum("lineStage", com.upmudoum.groupware.domain.approval.vo.ApprovalLineStage.class);

    public final DateTimePath<java.time.Instant> signedAt = createDateTime("signedAt", java.time.Instant.class);

    public final StringPath signedUserId = createString("signedUserId");

    public final EnumPath<com.upmudoum.groupware.domain.approval.vo.ApprovalLineStatus> status = createEnum("status", com.upmudoum.groupware.domain.approval.vo.ApprovalLineStatus.class);

    public final StringPath targetDepartmentId = createString("targetDepartmentId");

    public final StringPath targetPositionId = createString("targetPositionId");

    public final EnumPath<com.upmudoum.groupware.domain.approval.vo.ApprovalTargetType> targetType = createEnum("targetType", com.upmudoum.groupware.domain.approval.vo.ApprovalTargetType.class);

    public final StringPath targetUserId = createString("targetUserId");

    public QApprovalLine(String variable) {
        super(ApprovalLine.class, forVariable(variable));
    }

    public QApprovalLine(Path<? extends ApprovalLine> path) {
        super(path.getType(), path.getMetadata());
    }

    public QApprovalLine(PathMetadata metadata) {
        super(ApprovalLine.class, metadata);
    }

}

