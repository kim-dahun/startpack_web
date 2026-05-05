package com.upmudoum.groupware.domain.project.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.upmudoum.groupware.common.GroupwareErrorCode;
import com.upmudoum.groupware.common.GroupwareException;
import com.upmudoum.groupware.common.vo.TenantKey;
import com.upmudoum.groupware.domain.project.dto.ProjectRequest;
import com.upmudoum.groupware.domain.project.dto.ProjectCommentRequest;
import com.upmudoum.groupware.domain.project.dto.ProjectProgressRateRequest;
import com.upmudoum.groupware.domain.project.dto.ProjectStatusRequest;
import com.upmudoum.groupware.domain.project.dto.ProjectTaskRequest;
import com.upmudoum.groupware.domain.project.entity.ProjectComment;
import com.upmudoum.groupware.domain.project.entity.ProjectItem;
import com.upmudoum.groupware.domain.project.entity.ProjectTask;
import com.upmudoum.groupware.domain.project.repository.ProjectCommentRepository;
import com.upmudoum.groupware.domain.project.repository.ProjectRepository;
import com.upmudoum.groupware.domain.project.repository.ProjectTaskRepository;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectTaskRepository projectTaskRepository;
    private final ProjectCommentRepository projectCommentRepository;

    public ProjectService(ProjectRepository projectRepository, ProjectTaskRepository projectTaskRepository,
            ProjectCommentRepository projectCommentRepository) {
        this.projectRepository = projectRepository;
        this.projectTaskRepository = projectTaskRepository;
        this.projectCommentRepository = projectCommentRepository;
    }

    public ProjectItem create(TenantKey tenant, ProjectRequest request) {
        Instant now = Instant.now();
        ProjectItem item = new ProjectItem(
                UUID.randomUUID(),
                tenant.getComCd(),
                tenant.getUserId(),
                request.getName(),
                request.getDescription(),
                normalizeMembers(request.getMemberUserIds()),
                normalizeMembers(request.getReferenceUserIds()),
                request.getStatus(),
                request.getProgressRate(),
                now,
                now);
        return projectRepository.save(item);
    }

    public List<ProjectItem> list(TenantKey tenant) {
        return projectRepository.findVisibleProjects(tenant.getComCd(), tenant.getUserId());
    }

    public ProjectItem update(TenantKey tenant, UUID projectId, ProjectRequest request) {
        ProjectItem current = findOwned(tenant, projectId);
        ProjectItem updated = new ProjectItem(
                current.getId(),
                current.getComCd(),
                current.getOwnerUserId(),
                request.getName(),
                request.getDescription(),
                normalizeMembers(request.getMemberUserIds()),
                normalizeMembers(request.getReferenceUserIds()),
                request.getStatus(),
                request.getProgressRate(),
                current.getCreatedAt(),
                Instant.now());
        return projectRepository.save(updated);
    }

    public ProjectItem get(TenantKey tenant, UUID projectId) {
        return assertVisibleProject(tenant, projectId);
    }

    public void delete(TenantKey tenant, UUID projectId) {
        ProjectItem current = findOwned(tenant, projectId);
        projectRepository.save(current.delete(Instant.now()));
    }

    public ProjectItem updateStatus(TenantKey tenant, UUID projectId, ProjectStatusRequest request) {
        ProjectItem current = findOwned(tenant, projectId);
        ProjectItem updated = new ProjectItem(current.getId(), current.getComCd(), current.getOwnerUserId(), current.getName(),
                current.getDescription(), current.getMemberUserIds(), current.getReferenceUserIds(), request.getStatus(),
                current.getProgressRate(), current.getCreatedAt(), Instant.now());
        return projectRepository.save(updated);
    }

    public ProjectItem updateProgressRate(TenantKey tenant, UUID projectId, ProjectProgressRateRequest request) {
        ProjectItem current = findOwned(tenant, projectId);
        ProjectItem updated = new ProjectItem(current.getId(), current.getComCd(), current.getOwnerUserId(), current.getName(),
                current.getDescription(), current.getMemberUserIds(), current.getReferenceUserIds(), current.getStatus(),
                request.getProgressRate(), current.getCreatedAt(), Instant.now());
        return projectRepository.save(updated);
    }

    public List<ProjectItem> search(TenantKey tenant, String keyword, com.upmudoum.groupware.domain.project.vo.ProjectStatus status,
            String memberUserId, String referenceUserId, String ownerUserId) {
        return projectRepository.findVisibleProjects(tenant.getComCd(), tenant.getUserId()).stream()
                .filter(project -> keyword == null || keyword.isBlank()
                        || project.getName().toLowerCase().contains(keyword.toLowerCase())
                        || (project.getDescription() != null && project.getDescription().toLowerCase().contains(keyword.toLowerCase())))
                .filter(project -> status == null || project.getStatus() == status)
                .filter(project -> memberUserId == null || project.getMemberUserIds().contains(memberUserId))
                .filter(project -> referenceUserId == null || project.getReferenceUserIds().contains(referenceUserId))
                .filter(project -> ownerUserId == null || ownerUserId.equals(project.getOwnerUserId()))
                .toList();
    }

    private ProjectItem findOwned(TenantKey tenant, UUID projectId) {
        return projectRepository.findByIdAndComCdAndOwnerUserIdAndDeletedYnFalse(projectId, tenant.getComCd(), tenant.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "project not found"));
    }

    private List<String> normalizeMembers(List<String> memberUserIds) {
        if (memberUserIds == null) {
            return List.of();
        }
        return memberUserIds.stream()
                .filter(userId -> userId != null && !userId.isBlank())
                .distinct()
                .toList();
    }

    public ProjectTask createTask(TenantKey tenant, UUID projectId, ProjectTaskRequest request) {
        assertVisibleProject(tenant, projectId);
        Instant now = Instant.now();
        return projectTaskRepository.save(new ProjectTask(
                UUID.randomUUID(),
                tenant.getComCd(),
                projectId,
                request.getTitle(),
                request.getDescription(),
                request.getAssigneeUserId(),
                request.getDueDate(),
                request.getStatus(),
                tenant.getUserId(),
                now,
                now));
    }

    public List<ProjectTask> listTasks(TenantKey tenant, UUID projectId) {
        assertVisibleProject(tenant, projectId);
        return projectTaskRepository.findByComCdAndProjectIdAndDeletedYnFalseOrderByCreatedAtDesc(tenant.getComCd(), projectId);
    }

    public ProjectTask getTask(TenantKey tenant, UUID projectId, UUID taskId) {
        assertVisibleProject(tenant, projectId);
        return projectTaskRepository.findByIdAndComCdAndProjectIdAndDeletedYnFalse(taskId, tenant.getComCd(), projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "project task not found"));
    }

    public ProjectTask updateTask(TenantKey tenant, UUID projectId, UUID taskId, ProjectTaskRequest request) {
        ProjectTask task = getTask(tenant, projectId, taskId);
        return projectTaskRepository.save(task.update(request.getTitle(), request.getDescription(), request.getAssigneeUserId(),
                request.getDueDate(), request.getStatus(), Instant.now()));
    }

    public ProjectTask updateTaskStatus(TenantKey tenant, UUID projectId, UUID taskId, com.upmudoum.groupware.domain.project.vo.ProjectTaskStatus status) {
        ProjectTask task = getTask(tenant, projectId, taskId);
        return projectTaskRepository.save(task.update(task.getTitle(), task.getDescription(), task.getAssigneeUserId(),
                task.getDueDate(), status, Instant.now()));
    }

    public void deleteTask(TenantKey tenant, UUID projectId, UUID taskId) {
        ProjectTask task = getTask(tenant, projectId, taskId);
        projectTaskRepository.save(task.delete(Instant.now()));
    }

    public ProjectComment createComment(TenantKey tenant, UUID projectId, ProjectCommentRequest request) {
        assertVisibleProject(tenant, projectId);
        if (request.getTaskId() != null) {
            projectTaskRepository.findByIdAndComCdAndProjectIdAndDeletedYnFalse(request.getTaskId(), tenant.getComCd(), projectId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "project task not found"));
        }
        return projectCommentRepository.save(new ProjectComment(
                UUID.randomUUID(),
                tenant.getComCd(),
                projectId,
                request.getTaskId(),
                request.getContent(),
                tenant.getUserId(),
                Instant.now(),
                false));
    }

    public List<ProjectComment> listComments(TenantKey tenant, UUID projectId, UUID taskId) {
        assertVisibleProject(tenant, projectId);
        if (taskId != null) {
            return projectCommentRepository.findByComCdAndTaskIdAndDeletedYnFalseOrderByCreatedAtAsc(tenant.getComCd(), taskId);
        }
        return projectCommentRepository.findByComCdAndProjectIdAndDeletedYnFalseOrderByCreatedAtAsc(tenant.getComCd(), projectId);
    }

    public ProjectComment getComment(TenantKey tenant, UUID projectId, UUID commentId) {
        assertVisibleProject(tenant, projectId);
        return projectCommentRepository.findByIdAndComCdAndProjectIdAndDeletedYnFalse(commentId, tenant.getComCd(), projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "project comment not found"));
    }

    public ProjectComment updateComment(TenantKey tenant, UUID projectId, UUID commentId, ProjectCommentRequest request) {
        ProjectComment comment = getComment(tenant, projectId, commentId);
        if (!comment.getCreatedBy().equals(tenant.getUserId())) {
            findOwned(tenant, projectId);
        }
        return projectCommentRepository.save(comment.update(request.getContent(), Instant.now()));
    }

    public void deleteComment(TenantKey tenant, UUID projectId, UUID commentId) {
        ProjectComment comment = getComment(tenant, projectId, commentId);
        if (!comment.getCreatedBy().equals(tenant.getUserId())) {
            findOwned(tenant, projectId);
        }
        projectCommentRepository.save(comment.delete(Instant.now()));
    }

    private ProjectItem assertVisibleProject(TenantKey tenant, UUID projectId) {
        ProjectItem project = projectRepository.findByIdAndComCdAndDeletedYnFalse(projectId, tenant.getComCd())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "project not found"));
        if (!project.getOwnerUserId().equals(tenant.getUserId())
                && !project.getMemberUserIds().contains(tenant.getUserId())
                && !project.getReferenceUserIds().contains(tenant.getUserId())) {
            throw new GroupwareException(GroupwareErrorCode.PROJECT_NOT_VISIBLE);
        }
        return project;
    }
}
