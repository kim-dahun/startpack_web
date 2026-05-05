package com.upmudoum.groupware.domain.approval.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upmudoum.groupware.domain.approval.entity.ApprovalLineTemplateItem;

public interface ApprovalLineTemplateItemRepository extends JpaRepository<ApprovalLineTemplateItem, UUID> {

    List<ApprovalLineTemplateItem> findByComCdAndTemplateIdOrderByLineStageAscLineSeqAsc(String comCd, UUID templateId);

    void deleteByComCdAndTemplateId(String comCd, UUID templateId);
}
