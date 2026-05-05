package com.upmudoum.groupware.domain.notification.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upmudoum.groupware.domain.notification.entity.NotificationItem;
import com.upmudoum.groupware.domain.notification.entity.NotificationStatus;

public interface NotificationRepository extends JpaRepository<NotificationItem, UUID> {

    List<NotificationItem> findByComCdAndUserIdAndDeletedYnFalseOrderByCreatedAtDesc(String comCd, String userId);

    List<NotificationItem> findByComCdAndUserIdAndArchivedAtIsNotNullAndDeletedYnFalseOrderByArchivedAtDesc(String comCd, String userId);

    List<NotificationItem> findByComCdAndUserIdAndStatusAndDeletedYnFalseOrderByCreatedAtDesc(
            String comCd,
            String userId,
            NotificationStatus status);

    long countByComCdAndUserIdAndStatusAndDeletedYnFalse(String comCd, String userId, NotificationStatus status);

    Optional<NotificationItem> findByIdAndComCdAndUserIdAndDeletedYnFalse(UUID id, String comCd, String userId);

    long deleteByComCdAndExpiresAtBefore(String comCd, java.time.Instant expiresAt);
}
