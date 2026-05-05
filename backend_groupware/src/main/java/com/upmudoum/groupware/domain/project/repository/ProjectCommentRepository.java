package com.upmudoum.groupware.domain.project.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upmudoum.groupware.domain.project.entity.ProjectComment;

public interface ProjectCommentRepository extends JpaRepository<ProjectComment, UUID> {

    List<ProjectComment> findByComCdAndProjectIdAndDeletedYnFalseOrderByCreatedAtAsc(String comCd, UUID projectId);

    List<ProjectComment> findByComCdAndTaskIdAndDeletedYnFalseOrderByCreatedAtAsc(String comCd, UUID taskId);

    Optional<ProjectComment> findByIdAndComCdAndProjectIdAndDeletedYnFalse(UUID id, String comCd, UUID projectId);
}
