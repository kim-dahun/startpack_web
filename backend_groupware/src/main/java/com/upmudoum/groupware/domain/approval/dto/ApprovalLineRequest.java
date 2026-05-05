package com.upmudoum.groupware.domain.approval.dto;

import com.upmudoum.groupware.domain.approval.vo.ApprovalDecisionMode;
import com.upmudoum.groupware.domain.approval.vo.ApprovalLineStage;
import com.upmudoum.groupware.domain.approval.vo.ApprovalRoleType;
import com.upmudoum.groupware.domain.approval.vo.ApprovalTargetType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalLineRequest {

    @NotNull
    private ApprovalLineStage lineStage;
    private int lineSeq;

    @NotNull
    private ApprovalRoleType approvalRoleType;

    @NotNull
    private ApprovalDecisionMode decisionMode;

    @NotNull
    private ApprovalTargetType targetType;

    private String targetUserId;
    private String targetDepartmentId;
    private String targetPositionId;
}
