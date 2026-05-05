package com.upmudoum.groupware.domain.chat.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QChatMessage is a Querydsl query type for ChatMessage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatMessage extends EntityPathBase<ChatMessage> {

    private static final long serialVersionUID = -635017443L;

    public static final QChatMessage chatMessage = new QChatMessage("chatMessage");

    public final StringPath comCd = createString("comCd");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    public final BooleanPath deletedYn = createBoolean("deletedYn");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final EnumPath<com.upmudoum.groupware.domain.chat.vo.ChatMessageType> messageType = createEnum("messageType", com.upmudoum.groupware.domain.chat.vo.ChatMessageType.class);

    public final ComparablePath<java.util.UUID> roomId = createComparable("roomId", java.util.UUID.class);

    public final StringPath senderUserId = createString("senderUserId");

    public QChatMessage(String variable) {
        super(ChatMessage.class, forVariable(variable));
    }

    public QChatMessage(Path<? extends ChatMessage> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChatMessage(PathMetadata metadata) {
        super(ChatMessage.class, metadata);
    }

}

