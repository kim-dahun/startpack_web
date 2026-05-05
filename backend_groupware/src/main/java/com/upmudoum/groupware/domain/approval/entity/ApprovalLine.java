package com.upmudoum.groupware.domain.approval.entity;

import java.time.Instant;
import java.util.UUID;

import com.upmudoum.groupware.domain.approval.vo.ApprovalDecisionMode;
import com.upmudoum.groupware.domain.approval.vo.ApprovalLineStage;
import com.upmudoum.groupware.domain.approval.vo.ApprovalLineStatus;
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
@Table(name = "gw_approval_line")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApprovalLine {

    @Id
    private UUID id;
    private String comCd;
    private UUID documentId;

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
    private String signedUserId;
    private Instant signedAt;

    @Enumerated(EnumType.STRING)
    private ApprovalLineStatus status;

    public ApprovalLine(
            UUID id,
            String comCd,
            UUID documentId,
            ApprovalLineStage lineStage,
            int lineSeq,
            ApprovalRoleType approvalRoleType,
            ApprovalDecisionMode decisionMode,
            ApprovalTargetType targetType,
            String targetUserId,
            String targetDepartmentId,
            String targetPositionId,
            ApprovalLineStatus status) {
        this(id, comCd, documentId, lineStage, lineSeq, approvalRoleType, decisionMode, targetType, targetUserId,
                targetDepartmentId, targetPositionId, status, null, null);
    }

    public ApprovalLine(
            UUID id,
            String comCd,
            UUID documentId,
            ApprovalLineStage lineStage,
            int lineSeq,
            ApprovalRoleType approvalRoleType,
            ApprovalDecisionMode decisionMode,
            ApprovalTargetType targetType,
            String targetUserId,
            String targetDepartmentId,
            String targetPositionId,
            ApprovalLineStatus status,
            String signedUserId,
            Instant signedAt) {
        this.id = id;
        this.comCd = comCd;
        this.documentId = documentId;
        this.lineStage = lineStage;
        this.lineSeq = lineSeq;
        this.approvalRoleType = approvalRoleType;
        this.decisionMode = decisionMode;
        this.targetType = targetType;
        this.targetUserId = targetUserId;
        this.targetDepartmentId = targetDepartmentId;
        this.targetPositionId = targetPositionId;
        this.status = status;
        this.signedUserId = signedUserId;
        this.signedAt = signedAt;
    }

    public ApprovalLine resolveTargetUser(String targetUserId) {
        return new ApprovalLine(id, comCd, documentId, lineStage, lineSeq, approvalRoleType, decisionMode, targetType,
                targetUserId, targetDepartmentId, targetPositionId, status, signedUserId, signedAt);
    }

    public ApprovalLine approveBy(String signedUserId, Instant signedAt) {
        return new ApprovalLine(id, comCd, documentId, lineStage, lineSeq, approvalRoleType, decisionMode, targetType,
                targetUserId, targetDepartmentId, targetPositionId, ApprovalLineStatus.APPROVED, signedUserId, signedAt);
    }
}
