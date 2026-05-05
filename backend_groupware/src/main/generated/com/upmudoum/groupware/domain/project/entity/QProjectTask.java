package com.upmudoum.groupware.domain.project.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QProjectTask is a Querydsl query type for ProjectTask
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProjectTask extends EntityPathBase<ProjectTask> {

    private static final long serialVersionUID = 12241861L;

    public static final QProjectTask projectTask = new QProjectTask("projectTask");

    public final StringPath assigneeUserId = createString("assigneeUserId");

    public final StringPath comCd = createString("comCd");

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    public final StringPath createdBy = createString("createdBy");

    public final BooleanPath deletedYn = createBoolean("deletedYn");

    public final StringPath description = createString("description");

    public final DatePath<java.time.LocalDate> dueDate = createDate("dueDate", java.time.LocalDate.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> projectId = createComparable("projectId", java.util.UUID.class);

    public final EnumPath<com.upmudoum.groupware.domain.project.vo.ProjectTaskStatus> status = createEnum("status", com.upmudoum.groupware.domain.project.vo.ProjectTaskStatus.class);

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.Instant> updatedAt = createDateTime("updatedAt", java.time.Instant.class);

    public QProjectTask(String variable) {
        super(ProjectTask.class, forVariable(variable));
    }

    public QProjectTask(Path<? extends ProjectTask> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProjectTask(PathMetadata metadata) {
        super(ProjectTask.class, metadata);
    }

}

