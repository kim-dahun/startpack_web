package com.upmudoum.groupware.domain.project.controller;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.upmudoum.groupware.common.RequestContextResolver;
import com.upmudoum.groupware.domain.project.dto.ProjectCommentRequest;
import com.upmudoum.groupware.domain.project.dto.ProjectProgressRateRequest;
import com.upmudoum.groupware.domain.project.dto.ProjectRequest;
import com.upmudoum.groupware.domain.project.dto.ProjectStatusRequest;
import com.upmudoum.groupware.domain.project.dto.ProjectTaskRequest;
import com.upmudoum.groupware.domain.project.dto.ProjectTaskStatusRequest;
import com.upmudoum.groupware.domain.project.entity.ProjectComment;
import com.upmudoum.groupware.domain.project.entity.ProjectItem;
import com.upmudoum.groupware.domain.project.entity.ProjectTask;
import com.upmudoum.groupware.domain.project.service.ProjectService;

@RestController
@RequestMapping("/api/groupware/projects")
public class ProjectController {

    private final RequestContextResolver contextResolver;
    private final ProjectService projectService;

    public ProjectController(RequestContextResolver contextResolver, ProjectService projectService) {
        this.contextResolver = contextResolver;
        this.projectService = projectService;
    }

    @GetMapping
    public List<ProjectItem> list(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId) {
        return projectService.list(contextResolver.resolve(comCd, userId));
    }

    @PostMapping
    public ProjectItem create(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @Valid @RequestBody ProjectRequest request) {
        return projectService.create(contextResolver.resolve(comCd, userId), request);
    }

    @GetMapping("/{projectId}")
    public ProjectItem get(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID projectId) {
        return projectService.get(contextResolver.resolve(comCd, userId), projectId);
    }

    @PatchMapping("/{projectId}")
    public ProjectItem update(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID projectId,
            @Valid @RequestBody ProjectRequest request) {
        return projectService.update(contextResolver.resolve(comCd, userId), projectId, request);
    }

    @DeleteMapping("/{projectId}")
    public void delete(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID projectId) {
        projectService.delete(contextResolver.resolve(comCd, userId), projectId);
    }

    @PatchMapping("/{projectId}/status")
    public ProjectItem updateStatus(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID projectId,
            @Valid @RequestBody ProjectStatusRequest request) {
        return projectService.updateStatus(contextResolver.resolve(comCd, userId), projectId, request);
    }

    @PatchMapping("/{projectId}/progress-rate")
    public ProjectItem updateProgressRate(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID projectId,
            @Valid @RequestBody ProjectProgressRateRequest request) {
        return projectService.updateProgressRate(contextResolver.resolve(comCd, userId), projectId, request);
    }

    @GetMapping("/search")
    public List<ProjectItem> search(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String keyword,
            @org.springframework.web.bind.annotation.RequestParam(required = false) com.upmudoum.groupware.domain.project.vo.ProjectStatus status,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String memberUserId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String referenceUserId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String ownerUserId) {
        return projectService.search(contextResolver.resolve(comCd, userId), keyword, status, memberUserId, referenceUserId, ownerUserId);
    }

    @PostMapping("/{projectId}/tasks")
    public ProjectTask createTask(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID projectId,
            @Valid @RequestBody ProjectTaskRequest request) {
        return projectService.createTask(contextResolver.resolve(comCd, userId), projectId, request);
    }

    @GetMapping("/{projectId}/tasks")
    public List<ProjectTask> listTasks(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID projectId) {
        return projectService.listTasks(contextResolver.resolve(comCd, userId), projectId);
    }

    @GetMapping("/{projectId}/tasks/{taskId}")
    public ProjectTask getTask(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId) {
        return projectService.getTask(contextResolver.resolve(comCd, userId), projectId, taskId);
    }

    @PatchMapping("/{projectId}/tasks/{taskId}")
    public ProjectTask updateTask(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody ProjectTaskRequest request) {
        return projectService.updateTask(contextResolver.resolve(comCd, userId), projectId, taskId, request);
    }

    @PatchMapping("/{projectId}/tasks/{taskId}/status")
    public ProjectTask updateTaskStatus(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody ProjectTaskStatusRequest request) {
        return projectService.updateTaskStatus(contextResolver.resolve(comCd, userId), projectId, taskId, request.getStatus());
    }

    @DeleteMapping("/{projectId}/tasks/{taskId}")
    public void deleteTask(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId) {
        projectService.deleteTask(contextResolver.resolve(comCd, userId), projectId, taskId);
    }

    @PostMapping("/{projectId}/comments")
    public ProjectComment createComment(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID projectId,
            @Valid @RequestBody ProjectCommentRequest request) {
        return projectService.createComment(contextResolver.resolve(comCd, userId), projectId, request);
    }

    @GetMapping("/{projectId}/comments")
    public List<ProjectComment> listComments(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID projectId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) UUID taskId) {
        return projectService.listComments(contextResolver.resolve(comCd, userId), projectId, taskId);
    }

    @GetMapping("/{projectId}/comments/{commentId}")
    public ProjectComment getComment(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID projectId,
            @PathVariable UUID commentId) {
        return projectService.getComment(contextResolver.resolve(comCd, userId), projectId, commentId);
    }

    @PatchMapping("/{projectId}/comments/{commentId}")
    public ProjectComment updateComment(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID projectId,
            @PathVariable UUID commentId,
            @Valid @RequestBody ProjectCommentRequest request) {
        return projectService.updateComment(contextResolver.resolve(comCd, userId), projectId, commentId, request);
    }

    @DeleteMapping("/{projectId}/comments/{commentId}")
    public void deleteComment(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID projectId,
            @PathVariable UUID commentId) {
        projectService.deleteComment(contextResolver.resolve(comCd, userId), projectId, commentId);
    }

    @GetMapping("/{projectId}/tasks/{taskId}/comments")
    public List<ProjectComment> listTaskComments(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId) {
        return projectService.listComments(contextResolver.resolve(comCd, userId), projectId, taskId);
    }
}
