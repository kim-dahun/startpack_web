package com.upmudoum.groupware.domain.approval.controller;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.upmudoum.groupware.common.RequestContextResolver;
import com.upmudoum.groupware.common.vo.TenantKey;
import com.upmudoum.groupware.domain.approval.dto.ApprovalDocumentRequest;
import com.upmudoum.groupware.domain.approval.dto.ApprovalLineRequest;
import com.upmudoum.groupware.domain.approval.dto.ApprovalLineTemplateRequest;
import com.upmudoum.groupware.domain.approval.entity.ApprovalDocument;
import com.upmudoum.groupware.domain.approval.entity.ApprovalActionHistory;
import com.upmudoum.groupware.domain.approval.entity.ApprovalLine;
import com.upmudoum.groupware.domain.approval.entity.ApprovalLineTemplate;
import com.upmudoum.groupware.domain.approval.entity.ApprovalLineTemplateItem;
import com.upmudoum.groupware.domain.approval.service.ApprovalService;

@RestController
@RequestMapping("/api/groupware/approvals")
public class ApprovalController {

    private final RequestContextResolver contextResolver;
    private final ApprovalService approvalService;

    public ApprovalController(RequestContextResolver contextResolver, ApprovalService approvalService) {
        this.contextResolver = contextResolver;
        this.approvalService = approvalService;
    }

    @GetMapping
    public List<ApprovalDocument> list(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId) {
        return approvalService.list(contextResolver.resolve(comCd, userId));
    }

    @PostMapping
    public ApprovalDocument createDraft(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @Valid @RequestBody ApprovalDocumentRequest request) {
        return approvalService.createDraft(contextResolver.resolve(comCd, userId), request);
    }

    @GetMapping("/{documentId}")
    public ApprovalDocument get(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID documentId) {
        return approvalService.get(contextResolver.resolve(comCd, userId), documentId);
    }

    @PatchMapping("/{documentId}")
    public ApprovalDocument updateDraft(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID documentId,
            @Valid @RequestBody ApprovalDocumentRequest request) {
        return approvalService.updateDraft(contextResolver.resolve(comCd, userId), documentId, request);
    }

    @DeleteMapping("/{documentId}")
    public void deleteDraft(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID documentId) {
        approvalService.deleteDraft(contextResolver.resolve(comCd, userId), documentId);
    }

    @GetMapping("/search")
    public List<ApprovalDocument> search(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) com.upmudoum.groupware.domain.approval.entity.ApprovalStatus status,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String documentType,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String writerUserId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String keyword) {
        return approvalService.search(contextResolver.resolve(comCd, userId), status, documentType, writerUserId, keyword);
    }

    @PatchMapping("/{documentId}/submit")
    public ApprovalDocument submit(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID documentId) {
        TenantKey tenant = contextResolver.resolve(comCd, userId);
        return approvalService.submit(tenant, documentId);
    }

    @PatchMapping("/{documentId}/approve")
    public ApprovalDocument approve(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID documentId) {
        return approvalService.approve(contextResolver.resolve(comCd, userId), documentId);
    }

    @PatchMapping("/{documentId}/reject")
    public ApprovalDocument reject(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID documentId) {
        return approvalService.reject(contextResolver.resolve(comCd, userId), documentId);
    }

    @GetMapping("/{documentId}/lines")
    public List<ApprovalLine> listLines(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID documentId) {
        return approvalService.listLines(contextResolver.resolve(comCd, userId), documentId);
    }

    @GetMapping("/{documentId}/actions")
    public List<ApprovalActionHistory> listActions(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID documentId) {
        return approvalService.listActions(contextResolver.resolve(comCd, userId), documentId);
    }

    @PostMapping("/line-templates")
    public ApprovalLineTemplate createTemplate(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @Valid @RequestBody ApprovalLineTemplateRequest request) {
        return approvalService.createTemplate(contextResolver.resolve(comCd, userId), request);
    }

    @GetMapping("/line-templates")
    public List<ApprovalLineTemplate> listTemplates(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId) {
        return approvalService.listTemplates(contextResolver.resolve(comCd, userId));
    }

    @GetMapping("/line-templates/{templateId}/items")
    public List<ApprovalLineTemplateItem> listTemplateItems(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID templateId) {
        return approvalService.listTemplateItems(contextResolver.resolve(comCd, userId), templateId);
    }

    @PatchMapping("/line-templates/{templateId}")
    public ApprovalLineTemplate updateTemplate(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID templateId,
            @Valid @RequestBody ApprovalLineTemplateRequest request) {
        return approvalService.updateTemplate(contextResolver.resolve(comCd, userId), templateId, request);
    }

    @DeleteMapping("/line-templates/{templateId}")
    public void deleteTemplate(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID templateId) {
        approvalService.deleteTemplate(contextResolver.resolve(comCd, userId), templateId);
    }

    @PostMapping("/{documentId}/line-templates/{templateId}/apply")
    public List<ApprovalLine> applyTemplate(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID documentId,
            @PathVariable UUID templateId) {
        return approvalService.applyTemplate(contextResolver.resolve(comCd, userId), documentId, templateId);
    }

    @PostMapping("/{documentId}/consult-lines/reset")
    public List<ApprovalLine> resetConsultLines(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID documentId,
            @Valid @RequestBody List<ApprovalLineRequest> request) {
        return approvalService.resetConsultLines(contextResolver.resolve(comCd, userId), documentId, request);
    }
}
