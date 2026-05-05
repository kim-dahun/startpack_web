package com.upmudoum.groupware.domain.approval.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalDocumentRequest {

    @NotBlank
    private String title;

    private String content;
    private String documentType;
    private String documentJson;
    private List<String> approverUserIds;
    private List<ApprovalLineRequest> approvalLines;

    public ApprovalDocumentRequest(String title, String content, List<String> approverUserIds) {
        this.title = title;
        this.content = content;
        this.approverUserIds = approverUserIds;
    }
}
