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
@Table(name = "gw_message_attachment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageAttachment {

    @Id
    private UUID id;
    private String comCd;
    private UUID messageId;
    private String fileName;
    private String contentType;
    private long fileSize;
    private String storagePath;
    private String uploadedBy;
    private Instant uploadedAt;
    private boolean deletedYn;

    public MessageAttachment(UUID id, String comCd, UUID messageId, String fileName, String contentType,
            long fileSize, String storagePath, String uploadedBy, Instant uploadedAt, boolean deletedYn) {
        this.id = id;
        this.comCd = comCd;
        this.messageId = messageId;
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.storagePath = storagePath;
        this.uploadedBy = uploadedBy;
        this.uploadedAt = uploadedAt;
        this.deletedYn = deletedYn;
    }

    public MessageAttachment delete() {
        return new MessageAttachment(id, comCd, messageId, fileName, contentType, fileSize, storagePath, uploadedBy,
                uploadedAt, true);
    }
}
