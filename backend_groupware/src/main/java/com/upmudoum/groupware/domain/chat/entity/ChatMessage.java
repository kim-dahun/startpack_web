package com.upmudoum.groupware.domain.chat.entity;

import java.time.Instant;
import java.util.UUID;

import com.upmudoum.groupware.domain.chat.vo.ChatMessageType;

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
@Table(name = "gw_chat_message")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    private UUID id;
    private UUID roomId;
    private String comCd;
    private String senderUserId;

    @Enumerated(EnumType.STRING)
    private ChatMessageType messageType;

    private String content;
    private Instant createdAt;
    private boolean deletedYn;

    public ChatMessage(UUID id, UUID roomId, String comCd, String senderUserId, ChatMessageType messageType, String content, Instant createdAt) {
        this.id = id;
        this.roomId = roomId;
        this.comCd = comCd;
        this.senderUserId = senderUserId;
        this.messageType = messageType;
        this.content = content;
        this.createdAt = createdAt;
        this.deletedYn = false;
    }

    public ChatMessage updateContent(String content) {
        this.content = content;
        return this;
    }

    public ChatMessage delete() {
        this.deletedYn = true;
        this.content = "";
        return this;
    }
}
