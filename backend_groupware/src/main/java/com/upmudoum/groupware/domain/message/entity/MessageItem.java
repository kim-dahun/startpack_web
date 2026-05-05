package com.upmudoum.groupware.domain.message.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "gw_message")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageItem {

    @Id
    private UUID id;
    private String comCd;
    private String senderUserId;
    private String receiverUserId;
    private String content;
    private Instant sentAt;
    private Instant readAt;

    public MessageItem(UUID id, String comCd, String senderUserId, String receiverUserId, String content, Instant sentAt) {
        this(id, comCd, senderUserId, receiverUserId, content, sentAt, null);
    }

    public MessageItem(UUID id, String comCd, String senderUserId, String receiverUserId, String content, Instant sentAt, Instant readAt) {
        this.id = id;
        this.comCd = comCd;
        this.senderUserId = senderUserId;
        this.receiverUserId = receiverUserId;
        this.content = content;
        this.sentAt = sentAt;
        this.readAt = readAt;
    }

    public MessageItem markRead(Instant readAt) {
        return new MessageItem(id, comCd, senderUserId, receiverUserId, content, sentAt, readAt);
    }
}
