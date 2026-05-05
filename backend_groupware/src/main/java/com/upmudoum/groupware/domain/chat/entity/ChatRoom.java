package com.upmudoum.groupware.domain.chat.entity;

import java.time.Instant;
import java.util.UUID;

import com.upmudoum.groupware.domain.chat.vo.ChatRoomType;

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
@Table(name = "gw_chat_room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id
    private UUID id;
    private String comCd;

    @Enumerated(EnumType.STRING)
    private ChatRoomType roomType;

    private String roomName;
    private String createdBy;
    private Instant createdAt;
    private boolean deletedYn;
    private UUID lastMessageId;
    private String lastMessageContent;
    private Instant lastMessageAt;

    public ChatRoom(UUID id, String comCd, ChatRoomType roomType, String roomName, String createdBy, Instant createdAt) {
        this.id = id;
        this.comCd = comCd;
        this.roomType = roomType;
        this.roomName = roomName;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.deletedYn = false;
    }

    public ChatRoom markDeleted() {
        this.deletedYn = true;
        return this;
    }

    public ChatRoom updateName(String roomName) {
        this.roomName = roomName;
        return this;
    }

    public void updateLastMessage(ChatMessage message) {
        this.lastMessageId = message.getId();
        this.lastMessageContent = message.getContent();
        this.lastMessageAt = message.getCreatedAt();
    }
}
