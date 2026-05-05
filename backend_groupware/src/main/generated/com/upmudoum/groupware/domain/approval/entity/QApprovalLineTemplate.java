package com.upmudoum.groupware.domain.approval.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QApprovalLineTemplate is a Querydsl query type for ApprovalLineTemplate
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QApprovalLineTemplate extends EntityPathBase<ApprovalLineTemplate> {

    private static final long serialVersionUID = -2010828146L;

    public static final QApprovalLineTemplate approvalLineTemplate = new QApprovalLineTemplate("approvalLineTemplate");

    public final StringPath comCd = createString("comCd");

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    public final BooleanPath deletedYn = createBoolean("deletedYn");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath ownerUserId = createString("ownerUserId");

    public final StringPath templateName = createString("templateName");

    public QApprovalLineTemplate(String variable) {
        super(ApprovalLineTemplate.class, forVariable(variable));
    }

    public QApprovalLineTemplate(Path<? extends ApprovalLineTemplate> path) {
        super(path.getType(), path.getMetadata());
    }

    public QApprovalLineTemplate(PathMetadata metadata) {
        super(ApprovalLineTemplate.class, metadata);
    }

}

