package com.upmudoum.groupware.domain.project.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "gw_project_comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectComment {

    @Id
    private UUID id;
    private String comCd;
    private UUID projectId;
    private UUID taskId;
    private String content;
    private String createdBy;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean deletedYn;

    public ProjectComment(UUID id, String comCd, UUID projectId, UUID taskId, String content, String createdBy,
            Instant createdAt, boolean deletedYn) {
        this.id = id;
        this.comCd = comCd;
        this.projectId = projectId;
        this.taskId = taskId;
        this.content = content;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
        this.deletedYn = deletedYn;
    }

    public ProjectComment update(String content, Instant updatedAt) {
        ProjectComment comment = new ProjectComment(id, comCd, projectId, taskId, content, createdBy, createdAt, deletedYn);
        comment.updatedAt = updatedAt;
        return comment;
    }

    public ProjectComment delete(Instant updatedAt) {
        ProjectComment comment = update(content, updatedAt);
        comment.deletedYn = true;
        return comment;
    }
}
