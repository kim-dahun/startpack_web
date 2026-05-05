package com.upmudoum.groupware.domain.notification.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QNotificationItem is a Querydsl query type for NotificationItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotificationItem extends EntityPathBase<NotificationItem> {

    private static final long serialVersionUID = -207102621L;

    public static final QNotificationItem notificationItem = new QNotificationItem("notificationItem");

    public final DateTimePath<java.time.Instant> archivedAt = createDateTime("archivedAt", java.time.Instant.class);

    public final StringPath comCd = createString("comCd");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    public final BooleanPath deletedYn = createBoolean("deletedYn");

    public final DateTimePath<java.time.Instant> expiresAt = createDateTime("expiresAt", java.time.Instant.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final DateTimePath<java.time.Instant> readAt = createDateTime("readAt", java.time.Instant.class);

    public final StringPath referenceId = createString("referenceId");

    public final StringPath referenceType = createString("referenceType");

    public final EnumPath<NotificationStatus> status = createEnum("status", NotificationStatus.class);

    public final StringPath title = createString("title");

    public final StringPath userId = createString("userId");

    public QNotificationItem(String variable) {
        super(NotificationItem.class, forVariable(variable));
    }

    public QNotificationItem(Path<? extends NotificationItem> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNotificationItem(PathMetadata metadata) {
        super(NotificationItem.class, metadata);
    }

}

