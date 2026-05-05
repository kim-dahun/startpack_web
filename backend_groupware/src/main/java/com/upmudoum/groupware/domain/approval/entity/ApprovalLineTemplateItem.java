package com.upmudoum.groupware.domain.approval.entity;

import java.util.UUID;

import com.upmudoum.groupware.domain.approval.vo.ApprovalDecisionMode;
import com.upmudoum.groupware.domain.approval.vo.ApprovalLineStage;
import com.upmudoum.groupware.domain.approval.vo.ApprovalRoleType;
import com.upmudoum.groupware.domain.approval.vo.ApprovalTargetType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "gw_approval_line_template_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApprovalLineTemplateItem {

    @Id
    private UUID id;
    private String comCd;
    private UUID templateId;

    @Enumerated(EnumType.STRING)
    private ApprovalLineStage lineStage;
    private int lineSeq;

    @Enumerated(EnumType.STRING)
    private ApprovalRoleType approvalRoleType;

    @Enumerated(EnumType.STRING)
    private ApprovalDecisionMode decisionMode;

    @Enumerated(EnumType.STRING)
    private ApprovalTargetType targetType;

    private String targetUserId;
    private String targetDepartmentId;
    private String targetPositionId;

    public ApprovalLineTemplateItem(UUID id, String comCd, UUID templateId, ApprovalLineStage lineStage, int lineSeq,
            ApprovalRoleType approvalRoleType, ApprovalDecisionMode decisionMode, ApprovalTargetType targetType,
            String targetUserId, String targetDepartmentId, String targetPositionId) {
        this.id = id;
        this.comCd = comCd;
        this.templateId = templateId;
        this.lineStage = lineStage;
        this.lineSeq = lineSeq;
        this.approvalRoleType = approvalRoleType;
        this.decisionMode = decisionMode;
        this.targetType = targetType;
        this.targetUserId = targetUserId;
        this.targetDepartmentId = targetDepartmentId;
        this.targetPositionId = targetPositionId;
    }
}
