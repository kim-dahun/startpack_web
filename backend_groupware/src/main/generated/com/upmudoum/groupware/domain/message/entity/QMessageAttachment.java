package com.upmudoum.groupware.domain.message.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMessageAttachment is a Querydsl query type for MessageAttachment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMessageAttachment extends EntityPathBase<MessageAttachment> {

    private static final long serialVersionUID = -1437780573L;

    public static final QMessageAttachment messageAttachment = new QMessageAttachment("messageAttachment");

    public final StringPath comCd = createString("comCd");

    public final StringPath contentType = createString("contentType");

    public final BooleanPath deletedYn = createBoolean("deletedYn");

    public final StringPath fileName = createString("fileName");

    public final NumberPath<Long> fileSize = createNumber("fileSize", Long.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> messageId = createComparable("messageId", java.util.UUID.class);

    public final StringPath storagePath = createString("storagePath");

    public final DateTimePath<java.time.Instant> uploadedAt = createDateTime("uploadedAt", java.time.Instant.class);

    public final StringPath uploadedBy = createString("uploadedBy");

    public QMessageAttachment(String variable) {
        super(MessageAttachment.class, forVariable(variable));
    }

    public QMessageAttachment(Path<? extends MessageAttachment> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMessageAttachment(PathMetadata metadata) {
        super(MessageAttachment.class, metadata);
    }

}

