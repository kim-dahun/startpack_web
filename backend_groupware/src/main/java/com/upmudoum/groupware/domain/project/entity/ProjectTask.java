package com.upmudoum.groupware.domain.project.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.upmudoum.groupware.domain.project.vo.ProjectTaskStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "gw_project_task")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectTask {

    @Id
    private UUID id;
    private String comCd;
    private UUID projectId;
    private String title;
    private String description;
    private String assigneeUserId;
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private ProjectTaskStatus status;

    private String createdBy;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean deletedYn;

    public ProjectTask(UUID id, String comCd, UUID projectId, String title, String description, String assigneeUserId,
            LocalDate dueDate, ProjectTaskStatus status, String createdBy, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.comCd = comCd;
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.assigneeUserId = assigneeUserId;
        this.dueDate = dueDate;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedYn = false;
    }

    public ProjectTask update(String title, String description, String assigneeUserId, LocalDate dueDate,
            ProjectTaskStatus status, Instant updatedAt) {
        ProjectTask task = new ProjectTask(id, comCd, projectId, title, description, assigneeUserId, dueDate, status,
                createdBy, createdAt, updatedAt);
        task.deletedYn = deletedYn;
        return task;
    }

    public ProjectTask delete(Instant updatedAt) {
        ProjectTask task = update(title, description, assigneeUserId, dueDate, status, updatedAt);
        task.deletedYn = true;
        return task;
    }
}
