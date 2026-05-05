package com.upmudoum.groupware.domain.schedule.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QScheduleRecurrenceRule is a Querydsl query type for ScheduleRecurrenceRule
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QScheduleRecurrenceRule extends EntityPathBase<ScheduleRecurrenceRule> {

    private static final long serialVersionUID = -779092236L;

    public static final QScheduleRecurrenceRule scheduleRecurrenceRule = new QScheduleRecurrenceRule("scheduleRecurrenceRule");

    public final StringPath comCd = createString("comCd");

    public final NumberPath<Integer> countLimit = createNumber("countLimit", Integer.class);

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    public final EnumPath<com.upmudoum.groupware.domain.schedule.vo.RecurrenceFrequency> frequency = createEnum("frequency", com.upmudoum.groupware.domain.schedule.vo.RecurrenceFrequency.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final NumberPath<Integer> intervalValue = createNumber("intervalValue", Integer.class);

    public final ComparablePath<java.util.UUID> scheduleId = createComparable("scheduleId", java.util.UUID.class);

    public final DatePath<java.time.LocalDate> untilDate = createDate("untilDate", java.time.LocalDate.class);

    public QScheduleRecurrenceRule(String variable) {
        super(ScheduleRecurrenceRule.class, forVariable(variable));
    }

    public QScheduleRecurrenceRule(Path<? extends ScheduleRecurrenceRule> path) {
        super(path.getType(), path.getMetadata());
    }

    public QScheduleRecurrenceRule(PathMetadata metadata) {
        super(ScheduleRecurrenceRule.class, metadata);
    }

}

