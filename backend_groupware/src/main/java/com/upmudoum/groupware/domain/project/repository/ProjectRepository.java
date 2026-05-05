package com.upmudoum.groupware.domain.project.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.upmudoum.groupware.domain.project.entity.ProjectItem;

public interface ProjectRepository extends JpaRepository<ProjectItem, UUID> {

    @Query("""
            select distinct p
            from ProjectItem p
            left join p.memberUserIds memberUserId
            left join p.referenceUserIds referenceUserId
            where p.comCd = :comCd
              and (p.ownerUserId = :userId or memberUserId = :userId or referenceUserId = :userId)
              and p.deletedYn = false
            order by p.updatedAt desc
            """)
    List<ProjectItem> findVisibleProjects(@Param("comCd") String comCd, @Param("userId") String userId);

    Optional<ProjectItem> findByIdAndComCdAndOwnerUserIdAndDeletedYnFalse(UUID id, String comCd, String ownerUserId);

    Optional<ProjectItem> findByIdAndComCdAndDeletedYnFalse(UUID id, String comCd);

    List<ProjectItem> findByComCdAndDeletedYnFalse(String comCd);
}
