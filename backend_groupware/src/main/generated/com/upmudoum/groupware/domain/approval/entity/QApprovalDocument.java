package com.upmudoum.groupware.domain.approval.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QApprovalDocument is a Querydsl query type for ApprovalDocument
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QApprovalDocument extends EntityPathBase<ApprovalDocument> {

    private static final long serialVersionUID = -514697445L;

    public static final QApprovalDocument approvalDocument = new QApprovalDocument("approvalDocument");

    public final ListPath<String, StringPath> approverUserIds = this.<String, StringPath>createList("approverUserIds", String.class, StringPath.class, PathInits.DIRECT2);

    public final StringPath comCd = createString("comCd");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    public final BooleanPath deletedYn = createBoolean("deletedYn");

    public final StringPath documentJson = createString("documentJson");

    public final StringPath documentType = createString("documentType");

    public final StringPath drafterUserId = createString("drafterUserId");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final EnumPath<ApprovalStatus> status = createEnum("status", ApprovalStatus.class);

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.Instant> updatedAt = createDateTime("updatedAt", java.time.Instant.class);

    public QApprovalDocument(String variable) {
        super(ApprovalDocument.class, forVariable(variable));
    }

    public QApprovalDocument(Path<? extends ApprovalDocument> path) {
        super(path.getType(), path.getMetadata());
    }

    public QApprovalDocument(PathMetadata metadata) {
        super(ApprovalDocument.class, metadata);
    }

}

