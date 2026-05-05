package com.upmudoum.groupware.domain.chat.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QChatRoom is a Querydsl query type for ChatRoom
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatRoom extends EntityPathBase<ChatRoom> {

    private static final long serialVersionUID = 318464389L;

    public static final QChatRoom chatRoom = new QChatRoom("chatRoom");

    public final StringPath comCd = createString("comCd");

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    public final StringPath createdBy = createString("createdBy");

    public final BooleanPath deletedYn = createBoolean("deletedYn");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final DateTimePath<java.time.Instant> lastMessageAt = createDateTime("lastMessageAt", java.time.Instant.class);

    public final StringPath lastMessageContent = createString("lastMessageContent");

    public final ComparablePath<java.util.UUID> lastMessageId = createComparable("lastMessageId", java.util.UUID.class);

    public final StringPath roomName = createString("roomName");

    public final EnumPath<com.upmudoum.groupware.domain.chat.vo.ChatRoomType> roomType = createEnum("roomType", com.upmudoum.groupware.domain.chat.vo.ChatRoomType.class);

    public QChatRoom(String variable) {
        super(ChatRoom.class, forVariable(variable));
    }

    public QChatRoom(Path<? extends ChatRoom> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChatRoom(PathMetadata metadata) {
        super(ChatRoom.class, metadata);
    }

}

