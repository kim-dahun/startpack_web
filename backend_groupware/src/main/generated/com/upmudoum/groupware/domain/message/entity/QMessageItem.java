package com.upmudoum.groupware.domain.message.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMessageItem is a Querydsl query type for MessageItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMessageItem extends EntityPathBase<MessageItem> {

    private static final long serialVersionUID = 1514583955L;

    public static final QMessageItem messageItem = new QMessageItem("messageItem");

    public final StringPath comCd = createString("comCd");

    public final StringPath content = createString("content");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final DateTimePath<java.time.Instant> readAt = createDateTime("readAt", java.time.Instant.class);

    public final StringPath receiverUserId = createString("receiverUserId");

    public final StringPath senderUserId = createString("senderUserId");

    public final DateTimePath<java.time.Instant> sentAt = createDateTime("sentAt", java.time.Instant.class);

    public QMessageItem(String variable) {
        super(MessageItem.class, forVariable(variable));
    }

    public QMessageItem(Path<? extends MessageItem> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMessageItem(PathMetadata metadata) {
        super(MessageItem.class, metadata);
    }

}

