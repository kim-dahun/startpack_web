package com.upmudoum.groupware.domain.schedule.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QScheduleOccurrenceExclusion is a Querydsl query type for ScheduleOccurrenceExclusion
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QScheduleOccurrenceExclusion extends EntityPathBase<ScheduleOccurrenceExclusion> {

    private static final long serialVersionUID = -744183979L;

    public static final QScheduleOccurrenceExclusion scheduleOccurrenceExclusion = new QScheduleOccurrenceExclusion("scheduleOccurrenceExclusion");

    public final StringPath comCd = createString("comCd");

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final DatePath<java.time.LocalDate> occurrenceDate = createDate("occurrenceDate", java.time.LocalDate.class);

    public final StringPath reason = createString("reason");

    public final ComparablePath<java.util.UUID> scheduleId = createComparable("scheduleId", java.util.UUID.class);

    public QScheduleOccurrenceExclusion(String variable) {
        super(ScheduleOccurrenceExclusion.class, forVariable(variable));
    }

    public QScheduleOccurrenceExclusion(Path<? extends ScheduleOccurrenceExclusion> path) {
        super(path.getType(), path.getMetadata());
    }

    public QScheduleOccurrenceExclusion(PathMetadata metadata) {
        super(ScheduleOccurrenceExclusion.class, metadata);
    }

}

