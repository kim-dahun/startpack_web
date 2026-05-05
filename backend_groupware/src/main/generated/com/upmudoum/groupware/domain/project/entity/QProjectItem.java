package com.upmudoum.groupware.domain.project.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProjectItem is a Querydsl query type for ProjectItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProjectItem extends EntityPathBase<ProjectItem> {

    private static final long serialVersionUID = 11931987L;

    public static final QProjectItem projectItem = new QProjectItem("projectItem");

    public final StringPath comCd = createString("comCd");

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    public final BooleanPath deletedYn = createBoolean("deletedYn");

    public final StringPath description = createString("description");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final ListPath<String, StringPath> memberUserIds = this.<String, StringPath>createList("memberUserIds", String.class, StringPath.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final StringPath ownerUserId = createString("ownerUserId");

    public final NumberPath<Integer> progressRate = createNumber("progressRate", Integer.class);

    public final ListPath<String, StringPath> referenceUserIds = this.<String, StringPath>createList("referenceUserIds", String.class, StringPath.class, PathInits.DIRECT2);

    public final EnumPath<com.upmudoum.groupware.domain.project.vo.ProjectStatus> status = createEnum("status", com.upmudoum.groupware.domain.project.vo.ProjectStatus.class);

    public final DateTimePath<java.time.Instant> updatedAt = createDateTime("updatedAt", java.time.Instant.class);

    public QProjectItem(String variable) {
        super(ProjectItem.class, forVariable(variable));
    }

    public QProjectItem(Path<? extends ProjectItem> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProjectItem(PathMetadata metadata) {
        super(ProjectItem.class, metadata);
    }

}

