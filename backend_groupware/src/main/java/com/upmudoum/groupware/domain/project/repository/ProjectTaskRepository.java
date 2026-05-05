package com.upmudoum.groupware.domain.project.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upmudoum.groupware.domain.project.entity.ProjectTask;

public interface ProjectTaskRepository extends JpaRepository<ProjectTask, UUID> {

    List<ProjectTask> findByComCdAndProjectIdAndDeletedYnFalseOrderByCreatedAtDesc(String comCd, UUID projectId);

    Optional<ProjectTask> findByIdAndComCdAndProjectIdAndDeletedYnFalse(UUID id, String comCd, UUID projectId);
}
