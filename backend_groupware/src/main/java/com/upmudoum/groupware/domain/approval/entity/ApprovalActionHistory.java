package com.upmudoum.groupware.domain.approval.entity;

import java.time.Instant;
import java.util.UUID;

import com.upmudoum.groupware.domain.approval.vo.ApprovalActionType;

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
@Table(name = "gw_approval_action_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApprovalActionHistory {

    @Id
    private UUID id;
    private String comCd;
    private UUID documentId;
    private UUID approvalLineId;

    @Enumerated(EnumType.STRING)
    private ApprovalActionType actionType;

    private String actorUserId;
    private Instant actedAt;
    private String comment;

    public ApprovalActionHistory(UUID id, String comCd, UUID documentId, UUID approvalLineId, ApprovalActionType actionType,
            String actorUserId, Instant actedAt, String comment) {
        this.id = id;
        this.comCd = comCd;
        this.documentId = documentId;
        this.approvalLineId = approvalLineId;
        this.actionType = actionType;
        this.actorUserId = actorUserId;
        this.actedAt = actedAt;
        this.comment = comment;
    }
}
