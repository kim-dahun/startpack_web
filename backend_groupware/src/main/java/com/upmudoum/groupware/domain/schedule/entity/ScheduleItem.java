package com.upmudoum.groupware.domain.schedule.entity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import com.upmudoum.groupware.domain.schedule.vo.ScheduleScope;

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
@Table(name = "gw_schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleItem {

    @Id
    private UUID id;
    private String comCd;
    private String ownerUserId;
    private UUID projectId;
    private String projectCode;

    @Enumerated(EnumType.STRING)
    private ScheduleScope scope;

    private String title;
    private String memo;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private boolean allDay;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean deletedYn;

    public ScheduleItem(UUID id, String comCd, String ownerUserId, ScheduleScope scope, String title, String memo,
            LocalDateTime startAt, LocalDateTime endAt, boolean allDay, Instant createdAt, Instant updatedAt) {
        this(id, comCd, ownerUserId, null, null, scope, title, memo, startAt, endAt, allDay, createdAt, updatedAt);
    }

    public ScheduleItem(UUID id, String comCd, String ownerUserId, UUID projectId, String projectCode, ScheduleScope scope, String title, String memo,
            LocalDateTime startAt, LocalDateTime endAt, boolean allDay, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.comCd = comCd;
        this.ownerUserId = ownerUserId;
        this.projectId = projectId;
        this.projectCode = projectCode;
        this.scope = scope;
        this.title = title;
        this.memo = memo;
        this.startAt = startAt;
        this.endAt = endAt;
        this.allDay = allDay;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedYn = false;
    }

    public ScheduleItem delete(Instant updatedAt) {
        ScheduleItem item = new ScheduleItem(id, comCd, ownerUserId, projectId, projectCode, scope, title, memo,
                startAt, endAt, allDay, createdAt, updatedAt);
        item.deletedYn = true;
        return item;
    }
}
