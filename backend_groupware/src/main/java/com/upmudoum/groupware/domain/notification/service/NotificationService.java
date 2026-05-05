package com.upmudoum.groupware.domain.notification.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.upmudoum.groupware.common.infra.GroupwareEventPublisher;
import com.upmudoum.groupware.common.vo.TenantKey;
import com.upmudoum.groupware.domain.notification.dto.CreateNotificationRequest;
import com.upmudoum.groupware.domain.notification.entity.NotificationItem;
import com.upmudoum.groupware.domain.notification.entity.NotificationStatus;
import com.upmudoum.groupware.domain.notification.repository.NotificationRepository;

@Service
public class NotificationService {

    public static final int DEFAULT_RETENTION_DAYS = 30;

    private final NotificationRepository notificationRepository;
    private final GroupwareEventPublisher eventPublisher;

    public NotificationService(NotificationRepository notificationRepository, GroupwareEventPublisher eventPublisher) {
        this.notificationRepository = notificationRepository;
        this.eventPublisher = eventPublisher;
    }

    public NotificationItem create(TenantKey actor, CreateNotificationRequest request) {
        Instant now = Instant.now();
        NotificationItem item = new NotificationItem(
                UUID.randomUUID(),
                actor.getComCd(),
                request.getTargetUserId(),
                request.getTitle(),
                request.getContent(),
                request.getReferenceType(),
                request.getReferenceId(),
                NotificationStatus.UNREAD,
                now,
                null,
                null,
                now.plus(DEFAULT_RETENTION_DAYS, ChronoUnit.DAYS));
        NotificationItem saved = notificationRepository.save(item);
        eventPublisher.publishToUser(
                new TenantKey(actor.getComCd(), request.getTargetUserId()),
                "notifications",
                "notification.created",
                Map.of("notificationId", saved.getId().toString(), "title", saved.getTitle()));
        return saved;
    }

    public List<NotificationItem> list(TenantKey tenant, NotificationStatus status) {
        if (status == null) {
            return notificationRepository.findByComCdAndUserIdAndDeletedYnFalseOrderByCreatedAtDesc(tenant.getComCd(), tenant.getUserId());
        }
        return notificationRepository.findByComCdAndUserIdAndStatusAndDeletedYnFalseOrderByCreatedAtDesc(
                tenant.getComCd(),
                tenant.getUserId(),
                status);
    }

    public long countUnread(TenantKey tenant) {
        return notificationRepository.countByComCdAndUserIdAndStatusAndDeletedYnFalse(
                tenant.getComCd(),
                tenant.getUserId(),
                NotificationStatus.UNREAD);
    }

    public NotificationItem markRead(TenantKey tenant, UUID notificationId) {
        NotificationItem current = notificationRepository.findByIdAndComCdAndUserIdAndDeletedYnFalse(
                        notificationId,
                        tenant.getComCd(),
                        tenant.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notification not found"));
        if (current.getStatus() == NotificationStatus.READ) {
            return current;
        }
        return notificationRepository.save(current.markRead(Instant.now()));
    }

    public NotificationItem archive(TenantKey tenant, UUID notificationId, int retentionDays) {
        NotificationItem current = notificationRepository.findByIdAndComCdAndUserIdAndDeletedYnFalse(
                        notificationId,
                        tenant.getComCd(),
                        tenant.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notification not found"));
        Instant archivedAt = Instant.now();
        int safeRetentionDays = retentionDays <= 0 ? DEFAULT_RETENTION_DAYS : retentionDays;
        return notificationRepository.save(current.archive(archivedAt, archivedAt.plus(safeRetentionDays, ChronoUnit.DAYS)));
    }

    public List<NotificationItem> listArchived(TenantKey tenant) {
        return notificationRepository.findByComCdAndUserIdAndArchivedAtIsNotNullAndDeletedYnFalseOrderByArchivedAtDesc(
                tenant.getComCd(),
                tenant.getUserId());
    }

    @Transactional
    public Map<String, Long> purgeExpired(TenantKey tenant, Instant now) {
        return Map.of("deletedCount", notificationRepository.deleteByComCdAndExpiresAtBefore(tenant.getComCd(), now));
    }

    public void delete(TenantKey tenant, UUID notificationId) {
        NotificationItem current = notificationRepository.findByIdAndComCdAndUserIdAndDeletedYnFalse(
                        notificationId,
                        tenant.getComCd(),
                        tenant.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notification not found"));
        notificationRepository.save(current.delete());
    }

    public Map<String, Long> readAll(TenantKey tenant) {
        List<NotificationItem> unread = list(tenant, NotificationStatus.UNREAD);
        Instant now = Instant.now();
        notificationRepository.saveAll(unread.stream().map(item -> item.markRead(now)).toList());
        return Map.of("updatedCount", (long) unread.size());
    }

    public Map<String, Long> archiveAll(TenantKey tenant, int retentionDays) {
        List<NotificationItem> items = list(tenant, null).stream()
                .filter(item -> item.getArchivedAt() == null)
                .toList();
        Instant now = Instant.now();
        int safeRetentionDays = retentionDays <= 0 ? DEFAULT_RETENTION_DAYS : retentionDays;
        notificationRepository.saveAll(items.stream()
                .map(item -> item.archive(now, now.plus(safeRetentionDays, ChronoUnit.DAYS)))
                .toList());
        return Map.of("updatedCount", (long) items.size());
    }

    public List<NotificationItem> search(TenantKey tenant, NotificationStatus status, String referenceType, Instant from, Instant to) {
        return list(tenant, status).stream()
                .filter(item -> referenceType == null || referenceType.equals(item.getReferenceType()))
                .filter(item -> from == null || !item.getCreatedAt().isBefore(from))
                .filter(item -> to == null || !item.getCreatedAt().isAfter(to))
                .toList();
    }
}
