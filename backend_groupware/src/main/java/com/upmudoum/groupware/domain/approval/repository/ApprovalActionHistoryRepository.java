package com.upmudoum.groupware.domain.approval.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upmudoum.groupware.domain.approval.entity.ApprovalActionHistory;

public interface ApprovalActionHistoryRepository extends JpaRepository<ApprovalActionHistory, UUID> {

    List<ApprovalActionHistory> findByDocumentIdAndComCdOrderByActedAtAsc(UUID documentId, String comCd);
}
