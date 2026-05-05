package com.upmudoum.groupware.domain.approval.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QApprovalLineTemplateItem is a Querydsl query type for ApprovalLineTemplateItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QApprovalLineTemplateItem extends EntityPathBase<ApprovalLineTemplateItem> {

    private static final long serialVersionUID = -1238357311L;

    public static final QApprovalLineTemplateItem approvalLineTemplateItem = new QApprovalLineTemplateItem("approvalLineTemplateItem");

    public final EnumPath<com.upmudoum.groupware.domain.approval.vo.ApprovalRoleType> approvalRoleType = createEnum("approvalRoleType", com.upmudoum.groupware.domain.approval.vo.ApprovalRoleType.class);

    public final StringPath comCd = createString("comCd");

    public final EnumPath<com.upmudoum.groupware.domain.approval.vo.ApprovalDecisionMode> decisionMode = createEnum("decisionMode", com.upmudoum.groupware.domain.approval.vo.ApprovalDecisionMode.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final NumberPath<Integer> lineSeq = createNumber("lineSeq", Integer.class);

    public final EnumPath<com.upmudoum.groupware.domain.approval.vo.ApprovalLineStage> lineStage = createEnum("lineStage", com.upmudoum.groupware.domain.approval.vo.ApprovalLineStage.class);

    public final StringPath targetDepartmentId = createString("targetDepartmentId");

    public final StringPath targetPositionId = createString("targetPositionId");

    public final EnumPath<com.upmudoum.groupware.domain.approval.vo.ApprovalTargetType> targetType = createEnum("targetType", com.upmudoum.groupware.domain.approval.vo.ApprovalTargetType.class);

    public final StringPath targetUserId = createString("targetUserId");

    public final ComparablePath<java.util.UUID> templateId = createComparable("templateId", java.util.UUID.class);

    public QApprovalLineTemplateItem(String variable) {
        super(ApprovalLineTemplateItem.class, forVariable(variable));
    }

    public QApprovalLineTemplateItem(Path<? extends ApprovalLineTemplateItem> path) {
        super(path.getType(), path.getMetadata());
    }

    public QApprovalLineTemplateItem(PathMetadata metadata) {
        super(ApprovalLineTemplateItem.class, metadata);
    }

}

