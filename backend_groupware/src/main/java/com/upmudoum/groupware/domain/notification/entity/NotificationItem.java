package com.upmudoum.groupware.domain.notification.entity;

import java.time.Instant;
import java.util.UUID;

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
@Table(name = "gw_notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationItem {

    @Id
    private UUID id;
    private String comCd;
    private String userId;
    private String title;
    private String content;
    private String referenceType;
    private String referenceId;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private Instant createdAt;
    private Instant readAt;
    private Instant archivedAt;
    private Instant expiresAt;
    private boolean deletedYn;

    public NotificationItem(
            UUID id,
            String comCd,
            String userId,
            String title,
            String content,
            String referenceType,
            String referenceId,
            NotificationStatus status,
            Instant createdAt,
            Instant readAt) {
        this(id, comCd, userId, title, content, referenceType, referenceId, status, createdAt, readAt, null, null);
    }

    public NotificationItem(
            UUID id,
            String comCd,
            String userId,
            String title,
            String content,
            String referenceType,
            String referenceId,
            NotificationStatus status,
            Instant createdAt,
            Instant readAt,
            Instant archivedAt,
            Instant expiresAt) {
        this.id = id;
        this.comCd = comCd;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.status = status;
        this.createdAt = createdAt;
        this.readAt = readAt;
        this.archivedAt = archivedAt;
        this.expiresAt = expiresAt;
        this.deletedYn = false;
    }

    public NotificationItem markRead(Instant readAt) {
        return new NotificationItem(id, comCd, userId, title, content, referenceType, referenceId,
                NotificationStatus.READ, createdAt, readAt, archivedAt, expiresAt);
    }

    public NotificationItem archive(Instant archivedAt, Instant expiresAt) {
        return new NotificationItem(id, comCd, userId, title, content, referenceType, referenceId,
                status, createdAt, readAt, archivedAt, expiresAt);
    }

    public NotificationItem delete() {
        NotificationItem item = new NotificationItem(id, comCd, userId, title, content, referenceType, referenceId,
                status, createdAt, readAt, archivedAt, expiresAt);
        item.deletedYn = true;
        return item;
    }
}
