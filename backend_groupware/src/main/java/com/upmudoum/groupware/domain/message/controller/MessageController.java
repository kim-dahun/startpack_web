package com.upmudoum.groupware.domain.message.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.upmudoum.groupware.common.dto.PageSlice;
import com.upmudoum.groupware.common.RequestContextResolver;
import com.upmudoum.groupware.domain.message.dto.MessageAttachmentRequest;
import com.upmudoum.groupware.domain.message.dto.MessageReadRequest;
import com.upmudoum.groupware.domain.message.dto.SendMessageRequest;
import com.upmudoum.groupware.domain.message.entity.MessageAttachment;
import com.upmudoum.groupware.domain.message.entity.MessageItem;
import com.upmudoum.groupware.domain.message.service.MessageService;

@RestController
@RequestMapping("/api/groupware/messages")
public class MessageController {

    private final RequestContextResolver contextResolver;
    private final MessageService messageService;

    public MessageController(RequestContextResolver contextResolver, MessageService messageService) {
        this.contextResolver = contextResolver;
        this.messageService = messageService;
    }

    @PostMapping
    public MessageItem send(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @Valid @RequestBody SendMessageRequest request) {
        return messageService.send(contextResolver.resolve(comCd, userId), request);
    }

    @GetMapping("/conversation")
    public PageSlice<MessageItem> conversation(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @RequestParam String peerUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return messageService.conversation(contextResolver.resolve(comCd, userId), peerUserId, page, size);
    }

    @GetMapping("/conversation/unread-count")
    public Map<String, Long> unreadCount(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @RequestParam String peerUserId) {
        return Map.of("count", messageService.countUnread(contextResolver.resolve(comCd, userId), peerUserId));
    }

    @PatchMapping("/conversation/read")
    public Map<String, Long> markRead(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @RequestParam String peerUserId,
            @Valid @RequestBody MessageReadRequest request) {
        return messageService.markConversationRead(
                contextResolver.resolve(comCd, userId),
                peerUserId,
                request.getLastReadMessageId());
    }

    @PostMapping("/{messageId}/attachments")
    public MessageAttachment addAttachment(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID messageId,
            @Valid @RequestBody MessageAttachmentRequest request) {
        return messageService.addAttachment(contextResolver.resolve(comCd, userId), messageId, request);
    }

    @GetMapping("/{messageId}/attachments")
    public List<MessageAttachment> listAttachments(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID messageId) {
        return messageService.listAttachments(contextResolver.resolve(comCd, userId), messageId);
    }

    @DeleteMapping("/attachments/{attachmentId}")
    public void deleteAttachment(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID attachmentId) {
        messageService.deleteAttachment(contextResolver.resolve(comCd, userId), attachmentId);
    }
}
