package com.upmudoum.groupware.domain.approval.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.upmudoum.groupware.domain.approval.entity.ApprovalLine;
import com.upmudoum.groupware.domain.approval.vo.ApprovalLineStatus;
import com.upmudoum.groupware.domain.approval.vo.ApprovalRoleType;

public interface ApprovalLineRepository extends JpaRepository<ApprovalLine, UUID> {

    List<ApprovalLine> findByDocumentIdAndComCdOrderByLineStageAscLineSeqAsc(UUID documentId, String comCd);

    List<ApprovalLine> findByDocumentIdAndComCdAndApprovalRoleTypeOrderByLineSeqAsc(
            UUID documentId,
            String comCd,
            ApprovalRoleType approvalRoleType);

    Optional<ApprovalLine> findFirstByDocumentIdAndComCdAndApprovalRoleTypeAndStatusOrderByLineSeqAsc(
            UUID documentId,
            String comCd,
            ApprovalRoleType approvalRoleType,
            ApprovalLineStatus status);

    @Transactional
    @Modifying
    void deleteByDocumentIdAndComCdAndApprovalRoleType(UUID documentId, String comCd, ApprovalRoleType approvalRoleType);
}
