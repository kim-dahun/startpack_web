package com.upmudoum.groupware.domain.approval.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.upmudoum.groupware.domain.approval.entity.ApprovalDocument;

public interface ApprovalDocumentRepository extends JpaRepository<ApprovalDocument, UUID> {

    @Query("""
            select distinct d
            from ApprovalDocument d
            left join d.approverUserIds approverUserId
            where d.comCd = :comCd
              and d.deletedYn = false
              and (d.drafterUserId = :userId or approverUserId = :userId)
            order by d.updatedAt desc
            """)
    List<ApprovalDocument> findVisibleDocuments(@Param("comCd") String comCd, @Param("userId") String userId);

    @Query("""
            select distinct d
            from ApprovalDocument d
            left join d.approverUserIds approverUserId
            where d.id = :id
              and d.comCd = :comCd
              and d.deletedYn = false
              and (d.drafterUserId = :userId or approverUserId = :userId)
            """)
    Optional<ApprovalDocument> findVisibleDocument(
            @Param("id") UUID id,
            @Param("comCd") String comCd,
            @Param("userId") String userId);

    Optional<ApprovalDocument> findByIdAndComCdAndDeletedYnFalse(UUID id, String comCd);
}
