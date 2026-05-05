package com.upmudoum.groupware.domain.approval.entity;

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
@Table(name = "gw_approval_line_template")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApprovalLineTemplate {

    @Id
    private UUID id;
    private String comCd;
    private String ownerUserId;
    private String templateName;
    private Instant createdAt;
    private boolean deletedYn;

    public ApprovalLineTemplate(UUID id, String comCd, String ownerUserId, String templateName, Instant createdAt) {
        this.id = id;
        this.comCd = comCd;
        this.ownerUserId = ownerUserId;
        this.templateName = templateName;
        this.createdAt = createdAt;
        this.deletedYn = false;
    }

    public ApprovalLineTemplate update(String templateName) {
        ApprovalLineTemplate template = new ApprovalLineTemplate(id, comCd, ownerUserId, templateName, createdAt);
        template.deletedYn = deletedYn;
        return template;
    }

    public ApprovalLineTemplate delete() {
        ApprovalLineTemplate template = update(templateName);
        template.deletedYn = true;
        return template;
    }
}
