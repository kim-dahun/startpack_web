package com.upmudoum.groupware.domain.approval.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.upmudoum.groupware.common.GroupwareErrorCode;
import com.upmudoum.groupware.common.GroupwareException;
import com.upmudoum.groupware.common.vo.TenantKey;
import com.upmudoum.groupware.domain.approval.dto.ApprovalLineRequest;
import com.upmudoum.groupware.domain.approval.dto.ApprovalDocumentRequest;
import com.upmudoum.groupware.domain.approval.dto.ApprovalLineTemplateRequest;
import com.upmudoum.groupware.domain.approval.entity.ApprovalActionHistory;
import com.upmudoum.groupware.domain.approval.entity.ApprovalDocument;
import com.upmudoum.groupware.domain.approval.entity.ApprovalLine;
import com.upmudoum.groupware.domain.approval.entity.ApprovalLineTemplate;
import com.upmudoum.groupware.domain.approval.entity.ApprovalLineTemplateItem;
import com.upmudoum.groupware.domain.approval.entity.ApprovalStatus;
import com.upmudoum.groupware.domain.approval.repository.ApprovalActionHistoryRepository;
import com.upmudoum.groupware.domain.approval.repository.ApprovalDocumentRepository;
import com.upmudoum.groupware.domain.approval.repository.ApprovalLineRepository;
import com.upmudoum.groupware.domain.approval.repository.ApprovalLineTemplateItemRepository;
import com.upmudoum.groupware.domain.approval.repository.ApprovalLineTemplateRepository;
import com.upmudoum.groupware.domain.approval.vo.ApprovalActionType;
import com.upmudoum.groupware.domain.approval.vo.ApprovalLineStatus;
import com.upmudoum.groupware.domain.approval.vo.ApprovalRoleType;
import com.upmudoum.groupware.domain.approval.vo.ApprovalTargetType;
import com.upmudoum.groupware.domain.directory.infra.BackendUserDirectoryClient;
import com.upmudoum.groupware.domain.directory.infra.BackendUserOrganizationUserResponse;
import com.upmudoum.groupware.domain.notification.dto.CreateNotificationRequest;
import com.upmudoum.groupware.domain.notification.service.NotificationService;

@Service
public class ApprovalService {

    private final ApprovalDocumentRepository approvalDocumentRepository;
    private final ApprovalLineRepository approvalLineRepository;
    private final ApprovalActionHistoryRepository approvalActionHistoryRepository;
    private final ApprovalLineTemplateRepository approvalLineTemplateRepository;
    private final ApprovalLineTemplateItemRepository approvalLineTemplateItemRepository;
    private final NotificationService notificationService;
    private final BackendUserDirectoryClient backendUserDirectoryClient;

    public ApprovalService(
            ApprovalDocumentRepository approvalDocumentRepository,
            ApprovalLineRepository approvalLineRepository,
            ApprovalActionHistoryRepository approvalActionHistoryRepository,
            ApprovalLineTemplateRepository approvalLineTemplateRepository,
            ApprovalLineTemplateItemRepository approvalLineTemplateItemRepository,
            NotificationService notificationService,
            BackendUserDirectoryClient backendUserDirectoryClient) {
        this.approvalDocumentRepository = approvalDocumentRepository;
        this.approvalLineRepository = approvalLineRepository;
        this.approvalActionHistoryRepository = approvalActionHistoryRepository;
        this.approvalLineTemplateRepository = approvalLineTemplateRepository;
        this.approvalLineTemplateItemRepository = approvalLineTemplateItemRepository;
        this.notificationService = notificationService;
        this.backendUserDirectoryClient = backendUserDirectoryClient;
    }

    public ApprovalDocument createDraft(TenantKey tenant, ApprovalDocumentRequest request) {
        Instant now = Instant.now();
        ApprovalDocument document = new ApprovalDocument(
                UUID.randomUUID(),
                tenant.getComCd(),
                tenant.getUserId(),
                request.getTitle(),
                request.getContent(),
                request.getDocumentType(),
                request.getDocumentJson(),
                normalizeApprovers(request),
                ApprovalStatus.DRAFT,
                now,
                now);
        ApprovalDocument saved = approvalDocumentRepository.save(document);
        saveApprovalLines(tenant, saved.getId(), request.getApprovalLines());
        return saved;
    }

    public List<ApprovalDocument> list(TenantKey tenant) {
        return approvalDocumentRepository.findVisibleDocuments(tenant.getComCd(), tenant.getUserId());
    }

    public ApprovalDocument get(TenantKey tenant, UUID documentId) {
        return findVisible(tenant, documentId);
    }

    public ApprovalDocument updateDraft(TenantKey tenant, UUID documentId, ApprovalDocumentRequest request) {
        ApprovalDocument document = findVisible(tenant, documentId);
        if (!document.getDrafterUserId().equals(tenant.getUserId()) || document.getStatus() != ApprovalStatus.DRAFT) {
            throw new GroupwareException(GroupwareErrorCode.APPROVAL_INVALID_STATE, "only drafter can update draft document");
        }
        ApprovalDocument updated = document.updateDraft(request.getTitle(), request.getContent(), request.getDocumentType(),
                request.getDocumentJson(), normalizeApprovers(request), Instant.now());
        approvalLineRepository.deleteByDocumentIdAndComCdAndApprovalRoleType(documentId, tenant.getComCd(), ApprovalRoleType.APPROVAL);
        approvalLineRepository.deleteByDocumentIdAndComCdAndApprovalRoleType(documentId, tenant.getComCd(), ApprovalRoleType.CONSULT);
        approvalLineRepository.deleteByDocumentIdAndComCdAndApprovalRoleType(documentId, tenant.getComCd(), ApprovalRoleType.REFERENCE);
        saveApprovalLines(tenant, documentId, request.getApprovalLines());
        return approvalDocumentRepository.save(updated);
    }

    public void deleteDraft(TenantKey tenant, UUID documentId) {
        ApprovalDocument document = findVisible(tenant, documentId);
        if (!document.getDrafterUserId().equals(tenant.getUserId()) || document.getStatus() != ApprovalStatus.DRAFT) {
            throw new GroupwareException(GroupwareErrorCode.APPROVAL_INVALID_STATE, "only drafter can delete draft document");
        }
        approvalDocumentRepository.save(document.delete(Instant.now()));
    }

    public List<ApprovalDocument> search(TenantKey tenant, ApprovalStatus status, String documentType, String writerUserId,
            String keyword) {
        return list(tenant).stream()
                .filter(document -> status == null || document.getStatus() == status)
                .filter(document -> documentType == null || documentType.equals(document.getDocumentType()))
                .filter(document -> writerUserId == null || writerUserId.equals(document.getDrafterUserId()))
                .filter(document -> keyword == null || keyword.isBlank()
                        || document.getTitle().toLowerCase().contains(keyword.toLowerCase())
                        || (document.getContent() != null && document.getContent().toLowerCase().contains(keyword.toLowerCase())))
                .toList();
    }

    public ApprovalDocument submit(TenantKey tenant, UUID documentId) {
        ApprovalDocument current = findVisible(tenant, documentId);
        if (!current.getDrafterUserId().equals(tenant.getUserId())) {
            throw new GroupwareException(GroupwareErrorCode.APPROVAL_DRAFTER_ONLY);
        }
        if (current.getStatus() != ApprovalStatus.DRAFT) {
            throw new GroupwareException(GroupwareErrorCode.APPROVAL_INVALID_STATE, "only draft document can be submitted");
        }
        ApprovalDocument updated = withStatus(current, ApprovalStatus.SUBMITTED);
        updated = approvalDocumentRepository.save(updated);
        approvalActionHistoryRepository.save(new ApprovalActionHistory(
                UUID.randomUUID(),
                tenant.getComCd(),
                updated.getId(),
                null,
                ApprovalActionType.SUBMIT,
                tenant.getUserId(),
                Instant.now(),
                null));
        notifyConsultReceivers(tenant, updated);
        updated = notifyNextApprover(tenant, updated);
        return updated;
    }

    public ApprovalDocument approve(TenantKey tenant, UUID documentId) {
        ApprovalDocument current = findActionableDocument(tenant, documentId);
        if (current.getStatus() != ApprovalStatus.SUBMITTED) {
            throw new GroupwareException(GroupwareErrorCode.APPROVAL_INVALID_STATE, "only submitted document can be approved");
        }
        ApprovalLine line = findActionableApprovalLine(tenant, documentId);
        approvalLineRepository.save(line.approveBy(tenant.getUserId(), Instant.now()));
        boolean remaining = approvalLineRepository.findFirstByDocumentIdAndComCdAndApprovalRoleTypeAndStatusOrderByLineSeqAsc(
                documentId,
                tenant.getComCd(),
                ApprovalRoleType.APPROVAL,
                ApprovalLineStatus.WAITING).isPresent();
        ApprovalDocument updated = remaining ? current : withStatus(current, ApprovalStatus.APPROVED);
        updated = approvalDocumentRepository.save(updated);
        approvalActionHistoryRepository.save(new ApprovalActionHistory(
                UUID.randomUUID(),
                tenant.getComCd(),
                updated.getId(),
                null,
                ApprovalActionType.APPROVE,
                tenant.getUserId(),
                Instant.now(),
                null));
        notificationService.create(tenant, new CreateNotificationRequest(
                updated.getDrafterUserId(),
                remaining ? "Approval line approved" : "Approval approved",
                updated.getTitle(),
                "APPROVAL",
                updated.getId().toString()));
        if (remaining) {
            notifyNextApprover(tenant, updated);
        }
        return updated;
    }

    public ApprovalDocument reject(TenantKey tenant, UUID documentId) {
        ApprovalDocument current = findActionableDocument(tenant, documentId);
        if (current.getStatus() != ApprovalStatus.SUBMITTED) {
            throw new GroupwareException(GroupwareErrorCode.APPROVAL_INVALID_STATE, "only submitted document can be rejected");
        }
        findActionableApprovalLine(tenant, documentId);
        ApprovalDocument updated = withStatus(current, ApprovalStatus.REJECTED);
        updated = approvalDocumentRepository.save(updated);
        approvalActionHistoryRepository.save(new ApprovalActionHistory(
                UUID.randomUUID(),
                tenant.getComCd(),
                updated.getId(),
                null,
                ApprovalActionType.REJECT,
                tenant.getUserId(),
                Instant.now(),
                null));
        notificationService.create(tenant, new CreateNotificationRequest(
                updated.getDrafterUserId(),
                "Approval rejected",
                updated.getTitle(),
                "APPROVAL",
                updated.getId().toString()));
        return updated;
    }

    @Transactional
    public List<ApprovalLine> resetConsultLines(TenantKey tenant, UUID documentId, List<ApprovalLineRequest> consultLines) {
        ApprovalDocument document = approvalDocumentRepository.findByIdAndComCdAndDeletedYnFalse(documentId, tenant.getComCd())
                .orElseThrow(() -> new GroupwareException(GroupwareErrorCode.APPROVAL_DOCUMENT_NOT_FOUND));
        if (document.getStatus() != ApprovalStatus.SUBMITTED) {
            throw new GroupwareException(GroupwareErrorCode.APPROVAL_INVALID_STATE, "only submitted document consult lines can be reset");
        }
        boolean receiver = approvalLineRepository.findByDocumentIdAndComCdAndApprovalRoleTypeOrderByLineSeqAsc(
                        documentId,
                        tenant.getComCd(),
                        ApprovalRoleType.CONSULT)
                .stream()
                .anyMatch(line -> tenant.getUserId().equals(line.getTargetUserId()) && line.getStatus() == ApprovalLineStatus.WAITING);
        if (!receiver) {
            throw new GroupwareException(GroupwareErrorCode.APPROVAL_CONSULT_RECEIVER_ONLY);
        }
        approvalLineRepository.deleteByDocumentIdAndComCdAndApprovalRoleType(documentId, tenant.getComCd(), ApprovalRoleType.CONSULT);
        List<ApprovalLineRequest> normalized = consultLines == null ? List.of() : consultLines.stream()
                .filter(line -> line.getApprovalRoleType() == ApprovalRoleType.CONSULT)
                .toList();
        saveApprovalLines(tenant, documentId, normalized);
        notifyConsultReceivers(tenant, document);
        return approvalLineRepository.findByDocumentIdAndComCdAndApprovalRoleTypeOrderByLineSeqAsc(
                documentId,
                tenant.getComCd(),
                ApprovalRoleType.CONSULT);
    }

    private ApprovalDocument findVisible(TenantKey tenant, UUID documentId) {
        return approvalDocumentRepository.findVisibleDocument(documentId, tenant.getComCd(), tenant.getUserId())
                .orElseThrow(() -> new GroupwareException(GroupwareErrorCode.APPROVAL_DOCUMENT_NOT_FOUND));
    }

    private ApprovalDocument findActionableDocument(TenantKey tenant, UUID documentId) {
        return approvalDocumentRepository.findByIdAndComCdAndDeletedYnFalse(documentId, tenant.getComCd())
                .orElseThrow(() -> new GroupwareException(GroupwareErrorCode.APPROVAL_DOCUMENT_NOT_FOUND));
    }

    private ApprovalDocument withStatus(ApprovalDocument current, ApprovalStatus status) {
        return current.withStatus(status, Instant.now());
    }

    public List<ApprovalLine> listLines(TenantKey tenant, UUID documentId) {
        findVisible(tenant, documentId);
        return approvalLineRepository.findByDocumentIdAndComCdOrderByLineStageAscLineSeqAsc(documentId, tenant.getComCd());
    }

    private List<String> normalizeApprovers(ApprovalDocumentRequest request) {
        List<String> requestedApprovers = request.getApproverUserIds();
        if ((requestedApprovers == null || requestedApprovers.isEmpty()) && request.getApprovalLines() != null) {
            requestedApprovers = request.getApprovalLines().stream()
                    .filter(line -> line.getApprovalRoleType() == ApprovalRoleType.APPROVAL)
                    .map(ApprovalLineRequest::getTargetUserId)
                    .toList();
        }
        if (requestedApprovers == null) {
            return List.of();
        }
        return requestedApprovers.stream()
                .filter(userId -> userId != null && !userId.isBlank())
                .distinct()
                .toList();
    }

    private ApprovalLine findActionableApprovalLine(TenantKey tenant, UUID documentId) {
        return approvalLineRepository.findByDocumentIdAndComCdAndApprovalRoleTypeOrderByLineSeqAsc(
                        documentId,
                        tenant.getComCd(),
                        ApprovalRoleType.APPROVAL)
                .stream()
                .filter(line -> line.getStatus() == ApprovalLineStatus.WAITING)
                .filter(line -> tenant.getUserId().equals(line.getTargetUserId()))
                .findFirst()
                .orElseThrow(() -> new GroupwareException(GroupwareErrorCode.APPROVAL_CURRENT_APPROVER_ONLY));
    }

    private void notifyConsultReceivers(TenantKey tenant, ApprovalDocument document) {
        List<ApprovalLine> consultLines = approvalLineRepository.findByDocumentIdAndComCdAndApprovalRoleTypeOrderByLineSeqAsc(
                document.getId(),
                tenant.getComCd(),
                ApprovalRoleType.CONSULT);
        for (ApprovalLine line : consultLines) {
            String receiverUserId = resolveTargetUserId(tenant.getComCd(), line);
            if (receiverUserId == null) {
                continue;
            }
            ApprovalLine resolvedLine = line.getTargetUserId() == null ? approvalLineRepository.save(line.resolveTargetUser(receiverUserId)) : line;
            notificationService.create(tenant, new CreateNotificationRequest(
                    resolvedLine.getTargetUserId(),
                    "Approval consult requested",
                    document.getTitle(),
                    "APPROVAL",
                    document.getId().toString()));
        }
    }

    private ApprovalDocument notifyNextApprover(TenantKey tenant, ApprovalDocument document) {
        return approvalLineRepository.findFirstByDocumentIdAndComCdAndApprovalRoleTypeAndStatusOrderByLineSeqAsc(
                        document.getId(),
                        tenant.getComCd(),
                        ApprovalRoleType.APPROVAL,
                        ApprovalLineStatus.WAITING)
                .map(line -> {
                    String approverUserId = resolveTargetUserId(tenant.getComCd(), line);
                    if (approverUserId == null) {
                        throw new GroupwareException(GroupwareErrorCode.APPROVAL_TARGET_NOT_FOUND);
                    }
                    ApprovalLine resolvedLine = line.getTargetUserId() == null
                            ? approvalLineRepository.save(line.resolveTargetUser(approverUserId))
                            : line;
                    ApprovalDocument updatedDocument = ensureApproverUserId(document, resolvedLine.getTargetUserId());
                    notificationService.create(tenant, new CreateNotificationRequest(
                            resolvedLine.getTargetUserId(),
                            "Approval submitted",
                            document.getTitle(),
                            "APPROVAL",
                            document.getId().toString()));
                    return updatedDocument;
                })
                .orElse(document);
    }

    private ApprovalDocument ensureApproverUserId(ApprovalDocument document, String approverUserId) {
        if (document.getApproverUserIds().contains(approverUserId)) {
            return document;
        }
        List<String> approvers = new java.util.ArrayList<>(document.getApproverUserIds());
        approvers.add(approverUserId);
        return approvalDocumentRepository.save(document.withApproverUserIds(approvers, Instant.now()));
    }

    private String resolveTargetUserId(String comCd, ApprovalLine line) {
        if (line.getTargetType() == ApprovalTargetType.USER) {
            return line.getTargetUserId();
        }
        if (line.getTargetType() == ApprovalTargetType.DEPARTMENT
                || line.getTargetType() == ApprovalTargetType.DEPARTMENT_POSITION) {
            return backendUserDirectoryClient.searchOrganizationUsersByDepartment(comCd, line.getTargetDepartmentId())
                    .stream()
                    .flatMap(List::stream)
                    .filter(user -> matchesPosition(line, user))
                    .map(BackendUserOrganizationUserResponse::getUserId)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private boolean matchesPosition(ApprovalLine line, BackendUserOrganizationUserResponse user) {
        if (line.getTargetType() == ApprovalTargetType.DEPARTMENT) {
            return true;
        }
        if (user.getAffiliations() == null) {
            return false;
        }
        return user.getAffiliations().stream()
                .anyMatch(affiliation -> line.getTargetPositionId() != null
                        && line.getTargetPositionId().equals(affiliation.getPositionId()));
    }

    private void saveApprovalLines(TenantKey tenant, UUID documentId, List<ApprovalLineRequest> approvalLines) {
        if (approvalLines == null) {
            return;
        }
        for (ApprovalLineRequest line : approvalLines) {
            approvalLineRepository.save(new ApprovalLine(
                    UUID.randomUUID(),
                    tenant.getComCd(),
                    documentId,
                    line.getLineStage(),
                    line.getLineSeq(),
                    line.getApprovalRoleType(),
                    line.getDecisionMode(),
                    line.getTargetType(),
                    line.getTargetUserId(),
                    line.getTargetDepartmentId(),
                    line.getTargetPositionId(),
                    ApprovalLineStatus.WAITING));
        }
    }

    public ApprovalLineTemplate createTemplate(TenantKey tenant, ApprovalLineTemplateRequest request) {
        ApprovalLineTemplate template = approvalLineTemplateRepository.save(new ApprovalLineTemplate(
                UUID.randomUUID(),
                tenant.getComCd(),
                tenant.getUserId(),
                request.getTemplateName(),
                Instant.now()));
        saveTemplateItems(tenant, template.getId(), request.getApprovalLines());
        return template;
    }

    public List<ApprovalLineTemplate> listTemplates(TenantKey tenant) {
        return approvalLineTemplateRepository.findByComCdAndOwnerUserIdAndDeletedYnFalseOrderByCreatedAtDesc(
                tenant.getComCd(),
                tenant.getUserId());
    }

    public List<ApprovalLineTemplateItem> listTemplateItems(TenantKey tenant, UUID templateId) {
        approvalLineTemplateRepository.findByIdAndComCdAndOwnerUserIdAndDeletedYnFalse(templateId, tenant.getComCd(), tenant.getUserId())
                .orElseThrow(() -> new GroupwareException(GroupwareErrorCode.APPROVAL_LINE_TEMPLATE_NOT_FOUND));
        return approvalLineTemplateItemRepository.findByComCdAndTemplateIdOrderByLineStageAscLineSeqAsc(
                tenant.getComCd(),
                templateId);
    }

    public ApprovalLineTemplate updateTemplate(TenantKey tenant, UUID templateId, ApprovalLineTemplateRequest request) {
        ApprovalLineTemplate template = approvalLineTemplateRepository
                .findByIdAndComCdAndOwnerUserIdAndDeletedYnFalse(templateId, tenant.getComCd(), tenant.getUserId())
                .orElseThrow(() -> new GroupwareException(GroupwareErrorCode.APPROVAL_LINE_TEMPLATE_NOT_FOUND));
        approvalLineTemplateItemRepository.deleteByComCdAndTemplateId(tenant.getComCd(), templateId);
        saveTemplateItems(tenant, templateId, request.getApprovalLines());
        return approvalLineTemplateRepository.save(template.update(request.getTemplateName()));
    }

    public void deleteTemplate(TenantKey tenant, UUID templateId) {
        ApprovalLineTemplate template = approvalLineTemplateRepository
                .findByIdAndComCdAndOwnerUserIdAndDeletedYnFalse(templateId, tenant.getComCd(), tenant.getUserId())
                .orElseThrow(() -> new GroupwareException(GroupwareErrorCode.APPROVAL_LINE_TEMPLATE_NOT_FOUND));
        approvalLineTemplateRepository.save(template.delete());
    }

    public List<ApprovalActionHistory> listActions(TenantKey tenant, UUID documentId) {
        findVisible(tenant, documentId);
        return approvalActionHistoryRepository.findByDocumentIdAndComCdOrderByActedAtAsc(documentId, tenant.getComCd());
    }

    public List<ApprovalLine> applyTemplate(TenantKey tenant, UUID documentId, UUID templateId) {
        ApprovalDocument document = findVisible(tenant, documentId);
        if (!document.getDrafterUserId().equals(tenant.getUserId()) || document.getStatus() != ApprovalStatus.DRAFT) {
            throw new GroupwareException(GroupwareErrorCode.APPROVAL_INVALID_STATE, "only drafter can apply template to draft document");
        }
        List<ApprovalLineTemplateItem> items = listTemplateItems(tenant, templateId);
        for (ApprovalLineTemplateItem item : items) {
            approvalLineRepository.save(new ApprovalLine(
                    UUID.randomUUID(),
                    tenant.getComCd(),
                    documentId,
                    item.getLineStage(),
                    item.getLineSeq(),
                    item.getApprovalRoleType(),
                    item.getDecisionMode(),
                    item.getTargetType(),
                    item.getTargetUserId(),
                    item.getTargetDepartmentId(),
                    item.getTargetPositionId(),
                    ApprovalLineStatus.WAITING));
        }
        return listLines(tenant, documentId);
    }

    private void saveTemplateItems(TenantKey tenant, UUID templateId, List<ApprovalLineRequest> approvalLines) {
        if (approvalLines == null) {
            return;
        }
        for (ApprovalLineRequest line : approvalLines) {
            approvalLineTemplateItemRepository.save(new ApprovalLineTemplateItem(
                    UUID.randomUUID(),
                    tenant.getComCd(),
                    templateId,
                    line.getLineStage(),
                    line.getLineSeq(),
                    line.getApprovalRoleType(),
                    line.getDecisionMode(),
                    line.getTargetType(),
                    line.getTargetUserId(),
                    line.getTargetDepartmentId(),
                    line.getTargetPositionId()));
        }
    }
}
