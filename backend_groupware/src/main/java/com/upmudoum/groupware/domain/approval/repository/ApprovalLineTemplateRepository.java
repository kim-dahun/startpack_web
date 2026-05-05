package com.upmudoum.groupware.domain.approval.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upmudoum.groupware.domain.approval.entity.ApprovalLineTemplate;

public interface ApprovalLineTemplateRepository extends JpaRepository<ApprovalLineTemplate, UUID> {

    List<ApprovalLineTemplate> findByComCdAndOwnerUserIdAndDeletedYnFalseOrderByCreatedAtDesc(String comCd, String ownerUserId);

    Optional<ApprovalLineTemplate> findByIdAndComCdAndOwnerUserIdAndDeletedYnFalse(UUID id, String comCd, String ownerUserId);
}
