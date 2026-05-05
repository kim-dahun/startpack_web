package com.upmudoum.groupware.domain.schedule.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QScheduleItem is a Querydsl query type for ScheduleItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QScheduleItem extends EntityPathBase<ScheduleItem> {

    private static final long serialVersionUID = 728276411L;

    public static final QScheduleItem scheduleItem = new QScheduleItem("scheduleItem");

    public final BooleanPath allDay = createBoolean("allDay");

    public final StringPath comCd = createString("comCd");

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    public final BooleanPath deletedYn = createBoolean("deletedYn");

    public final DateTimePath<java.time.LocalDateTime> endAt = createDateTime("endAt", java.time.LocalDateTime.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath memo = createString("memo");

    public final StringPath ownerUserId = createString("ownerUserId");

    public final StringPath projectCode = createString("projectCode");

    public final ComparablePath<java.util.UUID> projectId = createComparable("projectId", java.util.UUID.class);

    public final EnumPath<com.upmudoum.groupware.domain.schedule.vo.ScheduleScope> scope = createEnum("scope", com.upmudoum.groupware.domain.schedule.vo.ScheduleScope.class);

    public final DateTimePath<java.time.LocalDateTime> startAt = createDateTime("startAt", java.time.LocalDateTime.class);

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.Instant> updatedAt = createDateTime("updatedAt", java.time.Instant.class);

    public QScheduleItem(String variable) {
        super(ScheduleItem.class, forVariable(variable));
    }

    public QScheduleItem(Path<? extends ScheduleItem> path) {
        super(path.getType(), path.getMetadata());
    }

    public QScheduleItem(PathMetadata metadata) {
        super(ScheduleItem.class, metadata);
    }

}

