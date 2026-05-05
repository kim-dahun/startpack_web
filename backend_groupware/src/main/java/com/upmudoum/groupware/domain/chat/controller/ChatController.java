package com.upmudoum.groupware.domain.chat.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.upmudoum.groupware.common.RequestContextResolver;
import com.upmudoum.groupware.common.dto.PageSlice;
import com.upmudoum.groupware.common.vo.TenantKey;
import com.upmudoum.groupware.domain.chat.dto.ChatMemberRequest;
import com.upmudoum.groupware.domain.chat.dto.CreateChatRoomRequest;
import com.upmudoum.groupware.domain.chat.dto.ReadChatMessageRequest;
import com.upmudoum.groupware.domain.chat.dto.SendChatMessageRequest;
import com.upmudoum.groupware.domain.chat.dto.UpdateChatRoomRequest;
import com.upmudoum.groupware.domain.chat.entity.ChatMessage;
import com.upmudoum.groupware.domain.chat.entity.ChatRoom;
import com.upmudoum.groupware.domain.chat.entity.ChatRoomMember;
import com.upmudoum.groupware.domain.chat.service.ChatService;

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

@RestController
@RequestMapping("/api/groupware/chats")
public class ChatController {

    private final RequestContextResolver contextResolver;
    private final ChatService chatService;

    public ChatController(RequestContextResolver contextResolver, ChatService chatService) {
        this.contextResolver = contextResolver;
        this.chatService = chatService;
    }

    @PostMapping("/rooms")
    public ChatRoom createRoom(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @Valid @RequestBody CreateChatRoomRequest request) {
        return chatService.createRoom(contextResolver.resolve(comCd, userId), request);
    }

    @GetMapping("/rooms")
    public List<ChatRoom> listRooms(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId) {
        return chatService.listRooms(contextResolver.resolve(comCd, userId));
    }

    @GetMapping("/rooms/{roomId}")
    public ChatRoom getRoom(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID roomId) {
        return chatService.getRoom(contextResolver.resolve(comCd, userId), roomId);
    }

    @PatchMapping("/rooms/{roomId}")
    public ChatRoom updateRoom(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID roomId,
            @Valid @RequestBody UpdateChatRoomRequest request) {
        return chatService.updateRoom(contextResolver.resolve(comCd, userId), roomId, request);
    }

    @DeleteMapping("/rooms/{roomId}")
    public ChatRoom deleteRoom(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID roomId) {
        return chatService.deleteRoom(contextResolver.resolve(comCd, userId), roomId);
    }

    @PatchMapping("/rooms/{roomId}/leave")
    public ChatRoomMember leaveRoom(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID roomId) {
        return chatService.leaveRoom(contextResolver.resolve(comCd, userId), roomId);
    }

    @PostMapping("/rooms/{roomId}/members")
    public ChatRoomMember addMember(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID roomId,
            @Valid @RequestBody ChatMemberRequest request) {
        return chatService.addMember(contextResolver.resolve(comCd, userId), roomId, request.getUserId());
    }

    @GetMapping("/rooms/{roomId}/members")
    public List<ChatRoomMember> listMembers(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID roomId) {
        return chatService.listMembers(contextResolver.resolve(comCd, userId), roomId);
    }

    @DeleteMapping("/rooms/{roomId}/members/{memberUserId}")
    public void removeMember(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID roomId,
            @PathVariable String memberUserId) {
        chatService.removeMember(contextResolver.resolve(comCd, userId), roomId, memberUserId);
    }

    @PostMapping("/rooms/{roomId}/messages")
    public ChatMessage sendMessage(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID roomId,
            @Valid @RequestBody SendChatMessageRequest request) {
        TenantKey tenant = contextResolver.resolve(comCd, userId);
        return chatService.sendMessage(tenant, roomId, request);
    }

    @GetMapping("/rooms/{roomId}/messages")
    public PageSlice<ChatMessage> listMessages(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return chatService.listMessages(contextResolver.resolve(comCd, userId), roomId, page, size);
    }

    @PatchMapping("/rooms/{roomId}/messages/{messageId}")
    public ChatMessage updateMessage(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID roomId,
            @PathVariable UUID messageId,
            @Valid @RequestBody SendChatMessageRequest request) {
        return chatService.updateMessage(contextResolver.resolve(comCd, userId), roomId, messageId, request);
    }

    @DeleteMapping("/rooms/{roomId}/messages/{messageId}")
    public void deleteMessage(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID roomId,
            @PathVariable UUID messageId) {
        chatService.deleteMessage(contextResolver.resolve(comCd, userId), roomId, messageId);
    }

    @GetMapping("/rooms/{roomId}/search-messages")
    public PageSlice<ChatMessage> searchMessages(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID roomId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return chatService.searchMessages(contextResolver.resolve(comCd, userId), roomId, keyword, page, size);
    }

    @PatchMapping("/rooms/{roomId}/read")
    public ChatRoomMember updateRead(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID roomId,
            @Valid @RequestBody ReadChatMessageRequest request) {
        return chatService.updateRead(contextResolver.resolve(comCd, userId), roomId, request.getLastReadMessageId());
    }

    @GetMapping("/rooms/{roomId}/unread-count")
    public Map<String, Long> unreadCount(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID roomId) {
        return chatService.unreadCount(contextResolver.resolve(comCd, userId), roomId);
    }
}
