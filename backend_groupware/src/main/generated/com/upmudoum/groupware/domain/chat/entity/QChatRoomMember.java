package com.upmudoum.groupware.domain.chat.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QChatRoomMember is a Querydsl query type for ChatRoomMember
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatRoomMember extends EntityPathBase<ChatRoomMember> {

    private static final long serialVersionUID = -336404353L;

    public static final QChatRoomMember chatRoomMember = new QChatRoomMember("chatRoomMember");

    public final StringPath comCd = createString("comCd");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final DateTimePath<java.time.Instant> joinedAt = createDateTime("joinedAt", java.time.Instant.class);

    public final ComparablePath<java.util.UUID> lastReadMessageId = createComparable("lastReadMessageId", java.util.UUID.class);

    public final DateTimePath<java.time.Instant> leftAt = createDateTime("leftAt", java.time.Instant.class);

    public final ComparablePath<java.util.UUID> roomId = createComparable("roomId", java.util.UUID.class);

    public final EnumPath<com.upmudoum.groupware.domain.chat.vo.ChatRoomRole> roomRole = createEnum("roomRole", com.upmudoum.groupware.domain.chat.vo.ChatRoomRole.class);

    public final StringPath userId = createString("userId");

    public QChatRoomMember(String variable) {
        super(ChatRoomMember.class, forVariable(variable));
    }

    public QChatRoomMember(Path<? extends ChatRoomMember> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChatRoomMember(PathMetadata metadata) {
        super(ChatRoomMember.class, metadata);
    }

}

