package com.upmudoum.groupware.domain.notification.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.Instant;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.upmudoum.groupware.common.RequestContextResolver;
import com.upmudoum.groupware.common.vo.TenantKey;
import com.upmudoum.groupware.domain.notification.dto.CreateNotificationRequest;
import com.upmudoum.groupware.domain.notification.entity.NotificationItem;
import com.upmudoum.groupware.domain.notification.entity.NotificationStatus;
import com.upmudoum.groupware.domain.notification.service.NotificationService;

@RestController
@RequestMapping("/api/groupware/notifications")
public class NotificationController {

    private final RequestContextResolver contextResolver;
    private final NotificationService notificationService;

    public NotificationController(RequestContextResolver contextResolver, NotificationService notificationService) {
        this.contextResolver = contextResolver;
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<NotificationItem> list(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @RequestParam(required = false) NotificationStatus status) {
        return notificationService.list(contextResolver.resolve(comCd, userId), status);
    }

    @GetMapping("/unread-count")
    public Map<String, Long> unreadCount(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId) {
        return Map.of("count", notificationService.countUnread(contextResolver.resolve(comCd, userId)));
    }

    @PatchMapping("/{notificationId}/read")
    public NotificationItem markRead(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID notificationId) {
        return notificationService.markRead(contextResolver.resolve(comCd, userId), notificationId);
    }

    @DeleteMapping("/{notificationId}")
    public void delete(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID notificationId) {
        notificationService.delete(contextResolver.resolve(comCd, userId), notificationId);
    }

    @PatchMapping("/read-all")
    public Map<String, Long> readAll(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId) {
        return notificationService.readAll(contextResolver.resolve(comCd, userId));
    }

    @PatchMapping("/archive-all")
    public Map<String, Long> archiveAll(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @RequestParam(defaultValue = "30") int retentionDays) {
        return notificationService.archiveAll(contextResolver.resolve(comCd, userId), retentionDays);
    }

    @GetMapping("/search")
    public List<NotificationItem> search(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @RequestParam(required = false) NotificationStatus status,
            @RequestParam(required = false) String referenceType,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to) {
        return notificationService.search(contextResolver.resolve(comCd, userId), status, referenceType, from, to);
    }

    @GetMapping("/archive")
    public List<NotificationItem> listArchived(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId) {
        return notificationService.listArchived(contextResolver.resolve(comCd, userId));
    }

    @PatchMapping("/{notificationId}/archive")
    public NotificationItem archive(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID notificationId,
            @RequestParam(defaultValue = "30") int retentionDays) {
        return notificationService.archive(contextResolver.resolve(comCd, userId), notificationId, retentionDays);
    }

    @PostMapping("/archive/purge-expired")
    public Map<String, Long> purgeExpired(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId) {
        return notificationService.purgeExpired(contextResolver.resolve(comCd, userId), java.time.Instant.now());
    }

    @PostMapping
    public NotificationItem create(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @Valid @RequestBody CreateNotificationRequest request) {
        TenantKey actor = contextResolver.resolve(comCd, userId);
        return notificationService.create(actor, request);
    }
}
