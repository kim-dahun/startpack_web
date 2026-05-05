package com.upmudoum.groupware.domain.chat.entity;

import java.time.Instant;
import java.util.UUID;

import com.upmudoum.groupware.domain.chat.vo.ChatRoomRole;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "gw_chat_room_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomMember {

    @Id
    private UUID id;
    private UUID roomId;
    private String comCd;
    private String userId;
    private Instant joinedAt;
    private Instant leftAt;

    @Enumerated(EnumType.STRING)
    private ChatRoomRole roomRole;

    private UUID lastReadMessageId;

    public ChatRoomMember(UUID id, UUID roomId, String comCd, String userId, Instant joinedAt, ChatRoomRole roomRole) {
        this.id = id;
        this.roomId = roomId;
        this.comCd = comCd;
        this.userId = userId;
        this.joinedAt = joinedAt;
        this.roomRole = roomRole;
    }

    public void leave(Instant leftAt) {
        this.leftAt = leftAt;
    }

    public void updateLastReadMessage(UUID lastReadMessageId) {
        this.lastReadMessageId = lastReadMessageId;
    }
}
