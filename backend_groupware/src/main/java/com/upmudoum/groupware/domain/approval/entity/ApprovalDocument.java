package com.upmudoum.groupware.domain.approval.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "gw_approval_document")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApprovalDocument {

    @Id
    private UUID id;
    private String comCd;
    private String drafterUserId;
    private String title;
    private String content;
    private String documentType;
    private String documentJson;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "gw_approval_document_approver_user_ids", joinColumns = @JoinColumn(name = "approval_document_id"))
    private List<String> approverUserIds = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;

    private Instant createdAt;
    private Instant updatedAt;
    private boolean deletedYn;

    public ApprovalDocument(UUID id, String comCd, String drafterUserId, String title, String content,
            List<String> approverUserIds, ApprovalStatus status, Instant createdAt, Instant updatedAt) {
        this(id, comCd, drafterUserId, title, content, null, null, approverUserIds, status, createdAt, updatedAt);
    }

    public ApprovalDocument(UUID id, String comCd, String drafterUserId, String title, String content, String documentType, String documentJson,
            List<String> approverUserIds, ApprovalStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.comCd = comCd;
        this.drafterUserId = drafterUserId;
        this.title = title;
        this.content = content;
        this.documentType = documentType;
        this.documentJson = documentJson;
        this.approverUserIds = approverUserIds;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedYn = false;
    }

    public ApprovalDocument withStatus(ApprovalStatus status, Instant updatedAt) {
        return new ApprovalDocument(id, comCd, drafterUserId, title, content, documentType, documentJson, approverUserIds, status, createdAt, updatedAt);
    }

    public ApprovalDocument withApproverUserIds(List<String> approverUserIds, Instant updatedAt) {
        return new ApprovalDocument(id, comCd, drafterUserId, title, content, documentType, documentJson, approverUserIds, status, createdAt, updatedAt);
    }

    public ApprovalDocument updateDraft(String title, String content, String documentType, String documentJson,
            List<String> approverUserIds, Instant updatedAt) {
        ApprovalDocument document = new ApprovalDocument(id, comCd, drafterUserId, title, content, documentType,
                documentJson, approverUserIds, status, createdAt, updatedAt);
        document.deletedYn = deletedYn;
        return document;
    }

    public ApprovalDocument delete(Instant updatedAt) {
        ApprovalDocument document = updateDraft(title, content, documentType, documentJson, approverUserIds, updatedAt);
        document.deletedYn = true;
        return document;
    }
}
