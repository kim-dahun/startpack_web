package com.upmudoum.groupware.domain.project.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.upmudoum.groupware.domain.project.vo.ProjectStatus;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "gw_project")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectItem {

    @Id
    private UUID id;
    private String comCd;
    private String ownerUserId;
    private String name;
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "gw_project_member_user_ids", joinColumns = @JoinColumn(name = "project_id"))
    private List<String> memberUserIds = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "gw_project_reference_user_ids", joinColumns = @JoinColumn(name = "project_id"))
    private List<String> referenceUserIds = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;
    private int progressRate;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean deletedYn;

    public ProjectItem(UUID id, String comCd, String ownerUserId, String name, String description,
            List<String> memberUserIds, ProjectStatus status, int progressRate, Instant createdAt, Instant updatedAt) {
        this(id, comCd, ownerUserId, name, description, memberUserIds, List.of(), status, progressRate, createdAt, updatedAt);
    }

    public ProjectItem(UUID id, String comCd, String ownerUserId, String name, String description,
            List<String> memberUserIds, List<String> referenceUserIds, ProjectStatus status, int progressRate,
            Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.comCd = comCd;
        this.ownerUserId = ownerUserId;
        this.name = name;
        this.description = description;
        this.memberUserIds = memberUserIds;
        this.referenceUserIds = referenceUserIds;
        this.status = status;
        this.progressRate = progressRate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedYn = false;
    }

    public ProjectItem delete(Instant updatedAt) {
        ProjectItem item = new ProjectItem(id, comCd, ownerUserId, name, description, memberUserIds, referenceUserIds,
                status, progressRate, createdAt, updatedAt);
        item.deletedYn = true;
        return item;
    }
}
