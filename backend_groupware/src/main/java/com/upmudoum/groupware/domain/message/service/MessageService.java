package com.upmudoum.groupware.domain.message.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.upmudoum.groupware.common.dto.PageSlice;
import com.upmudoum.groupware.common.GroupwareErrorCode;
import com.upmudoum.groupware.common.GroupwareException;
import com.upmudoum.groupware.common.infra.GroupwareEventPublisher;
import com.upmudoum.groupware.common.vo.TenantKey;
import com.upmudoum.groupware.domain.directory.infra.BackendUserDirectoryClient;
import com.upmudoum.groupware.domain.message.dto.MessageAttachmentRequest;
import com.upmudoum.groupware.domain.message.entity.MessageAttachment;
import com.upmudoum.groupware.domain.message.dto.SendMessageRequest;
import com.upmudoum.groupware.domain.message.entity.MessageItem;
import com.upmudoum.groupware.domain.message.repository.MessageAttachmentRepository;
import com.upmudoum.groupware.domain.message.repository.MessageRepository;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageAttachmentRepository messageAttachmentRepository;
    private final GroupwareEventPublisher eventPublisher;
    private final BackendUserDirectoryClient backendUserDirectoryClient;

    public MessageService(MessageRepository messageRepository, MessageAttachmentRepository messageAttachmentRepository,
            GroupwareEventPublisher eventPublisher,
            BackendUserDirectoryClient backendUserDirectoryClient) {
        this.messageRepository = messageRepository;
        this.messageAttachmentRepository = messageAttachmentRepository;
        this.eventPublisher = eventPublisher;
        this.backendUserDirectoryClient = backendUserDirectoryClient;
    }

    public MessageItem send(TenantKey sender, SendMessageRequest request) {
        if (!backendUserDirectoryClient.isActiveUser(sender.getComCd(), request.getReceiverUserId())) {
            throw new GroupwareException(GroupwareErrorCode.USER_INACTIVE);
        }
        MessageItem item = new MessageItem(
                UUID.randomUUID(),
                sender.getComCd(),
                sender.getUserId(),
                request.getReceiverUserId(),
                request.getContent(),
                Instant.now());
        MessageItem saved = messageRepository.save(item);
        eventPublisher.publishToUser(
                new TenantKey(sender.getComCd(), request.getReceiverUserId()),
                "messages",
                "message.received",
                Map.of("messageId", saved.getId().toString(), "senderUserId", sender.getUserId()));
        return saved;
    }

    public PageSlice<MessageItem> conversation(TenantKey tenant, String peerUserId, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        Page<MessageItem> result = messageRepository.findConversation(
                tenant.getComCd(),
                tenant.getUserId(),
                peerUserId,
                PageRequest.of(safePage, safeSize));
        return new PageSlice<>(result.getContent(), safePage, safeSize, result.hasNext());
    }

    public long countUnread(TenantKey tenant, String peerUserId) {
        return messageRepository.countUnreadFromPeer(tenant.getComCd(), tenant.getUserId(), peerUserId);
    }

    public Map<String, Long> markConversationRead(TenantKey tenant, String peerUserId, UUID lastReadMessageId) {
        List<MessageItem> unread = messageRepository.findUnreadUntil(
                tenant.getComCd(),
                tenant.getUserId(),
                peerUserId,
                lastReadMessageId);
        Instant now = Instant.now();
        List<MessageItem> readItems = unread.stream()
                .map(message -> message.markRead(now))
                .toList();
        messageRepository.saveAll(readItems);
        return Map.of("readCount", (long) readItems.size());
    }

    public MessageAttachment addAttachment(TenantKey tenant, UUID messageId, MessageAttachmentRequest request) {
        MessageItem message = messageRepository.findByIdAndComCd(messageId, tenant.getComCd())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "message not found"));
        if (!message.getSenderUserId().equals(tenant.getUserId()) && !message.getReceiverUserId().equals(tenant.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "message is not visible");
        }
        return messageAttachmentRepository.save(new MessageAttachment(
                UUID.randomUUID(),
                tenant.getComCd(),
                messageId,
                request.getFileName(),
                request.getContentType(),
                request.getFileSize(),
                request.getStoragePath(),
                tenant.getUserId(),
                Instant.now(),
                false));
    }

    public List<MessageAttachment> listAttachments(TenantKey tenant, UUID messageId) {
        MessageItem message = messageRepository.findByIdAndComCd(messageId, tenant.getComCd())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "message not found"));
        if (!message.getSenderUserId().equals(tenant.getUserId()) && !message.getReceiverUserId().equals(tenant.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "message is not visible");
        }
        return messageAttachmentRepository.findByComCdAndMessageIdAndDeletedYnFalseOrderByUploadedAtAsc(tenant.getComCd(), messageId);
    }

    public void deleteAttachment(TenantKey tenant, UUID attachmentId) {
        MessageAttachment attachment = messageAttachmentRepository.findByIdAndComCdAndDeletedYnFalse(
                        attachmentId,
                        tenant.getComCd())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "message attachment not found"));
        if (!attachment.getUploadedBy().equals(tenant.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "only uploader can delete attachment");
        }
        messageAttachmentRepository.save(attachment.delete());
    }
}
