package com.upmudoum.groupware.domain.project.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QProjectComment is a Querydsl query type for ProjectComment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProjectComment extends EntityPathBase<ProjectComment> {

    private static final long serialVersionUID = 2112794655L;

    public static final QProjectComment projectComment = new QProjectComment("projectComment");

    public final StringPath comCd = createString("comCd");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    public final StringPath createdBy = createString("createdBy");

    public final BooleanPath deletedYn = createBoolean("deletedYn");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> projectId = createComparable("projectId", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> taskId = createComparable("taskId", java.util.UUID.class);

    public final DateTimePath<java.time.Instant> updatedAt = createDateTime("updatedAt", java.time.Instant.class);

    public QProjectComment(String variable) {
        super(ProjectComment.class, forVariable(variable));
    }

    public QProjectComment(Path<? extends ProjectComment> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProjectComment(PathMetadata metadata) {
        super(ProjectComment.class, metadata);
    }

}

