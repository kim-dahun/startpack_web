package com.upmudoum.groupware.domain.chat.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.upmudoum.groupware.common.dto.PageSlice;
import com.upmudoum.groupware.common.infra.GroupwareEventPublisher;
import com.upmudoum.groupware.common.vo.TenantKey;
import com.upmudoum.groupware.domain.chat.dto.CreateChatRoomRequest;
import com.upmudoum.groupware.domain.chat.dto.SendChatMessageRequest;
import com.upmudoum.groupware.domain.chat.dto.UpdateChatRoomRequest;
import com.upmudoum.groupware.domain.chat.entity.ChatMessage;
import com.upmudoum.groupware.domain.chat.entity.ChatRoom;
import com.upmudoum.groupware.domain.chat.entity.ChatRoomMember;
import com.upmudoum.groupware.domain.chat.repository.ChatMessageRepository;
import com.upmudoum.groupware.domain.chat.repository.ChatRoomMemberRepository;
import com.upmudoum.groupware.domain.chat.repository.ChatRoomRepository;
import com.upmudoum.groupware.domain.chat.vo.ChatMessageType;
import com.upmudoum.groupware.domain.chat.vo.ChatRoomRole;
import com.upmudoum.groupware.domain.chat.vo.ChatRoomType;

@Service
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final GroupwareEventPublisher eventPublisher;

    public ChatService(
            ChatRoomRepository chatRoomRepository,
            ChatRoomMemberRepository chatRoomMemberRepository,
            ChatMessageRepository chatMessageRepository,
            GroupwareEventPublisher eventPublisher) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomMemberRepository = chatRoomMemberRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ChatRoom createRoom(TenantKey tenant, CreateChatRoomRequest request) {
        List<String> members = normalizeMembers(tenant.getUserId(), request.getMemberUserIds());
        if (request.getRoomType() == ChatRoomType.DIRECT) {
            if (members.size() != 2) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "direct chat requires exactly two members");
            }
            List<ChatRoom> existing = chatRoomRepository.findDirectRooms(
                    tenant.getComCd(),
                    ChatRoomType.DIRECT,
                    members.get(0),
                    members.get(1));
            if (!existing.isEmpty()) {
                return existing.get(0);
            }
        }
        ChatRoom room = chatRoomRepository.save(new ChatRoom(
                UUID.randomUUID(),
                tenant.getComCd(),
                request.getRoomType(),
                request.getRoomName(),
                tenant.getUserId(),
                Instant.now()));
        for (String memberUserId : members) {
            chatRoomMemberRepository.save(new ChatRoomMember(
                    UUID.randomUUID(),
                    room.getId(),
                    tenant.getComCd(),
                    memberUserId,
                    Instant.now(),
                    tenant.getUserId().equals(memberUserId) ? ChatRoomRole.OWNER : ChatRoomRole.MEMBER));
        }
        return room;
    }

    public List<ChatRoom> listRooms(TenantKey tenant) {
        return chatRoomRepository.findJoinedRooms(tenant.getComCd(), tenant.getUserId());
    }

    public ChatRoom getRoom(TenantKey tenant, UUID roomId) {
        return findJoinedRoom(tenant, roomId);
    }

    @Transactional
    public ChatRoom updateRoom(TenantKey tenant, UUID roomId, UpdateChatRoomRequest request) {
        ChatRoom room = findJoinedRoom(tenant, roomId);
        if (room.getRoomType() == ChatRoomType.DIRECT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "direct chat room name cannot be changed");
        }
        return chatRoomRepository.save(room.updateName(request.getRoomName()));
    }

    @Transactional
    public ChatRoom deleteRoom(TenantKey tenant, UUID roomId) {
        ChatRoom room = findJoinedRoom(tenant, roomId);
        if (!tenant.getUserId().equals(room.getCreatedBy())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "only room creator can delete room");
        }
        return chatRoomRepository.save(room.markDeleted());
    }

    @Transactional
    public ChatRoomMember leaveRoom(TenantKey tenant, UUID roomId) {
        ChatRoomMember member = chatRoomMemberRepository.findByRoomIdAndComCdAndUserIdAndLeftAtIsNull(
                        roomId,
                        tenant.getComCd(),
                        tenant.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "chat member not found"));
        member.leave(Instant.now());
        return chatRoomMemberRepository.save(member);
    }

    @Transactional
    public ChatRoomMember addMember(TenantKey tenant, UUID roomId, String userId) {
        ChatRoom room = findJoinedRoom(tenant, roomId);
        if (room.getRoomType() == ChatRoomType.DIRECT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "direct chat member cannot be changed");
        }
        return chatRoomMemberRepository.findByRoomIdAndComCdAndUserId(roomId, tenant.getComCd(), userId)
                .map(existing -> {
                    if (existing.getLeftAt() == null) {
                        return existing;
                    }
                    return chatRoomMemberRepository.save(new ChatRoomMember(
                            UUID.randomUUID(),
                            roomId,
                            tenant.getComCd(),
                            userId,
                            Instant.now(),
                            ChatRoomRole.MEMBER));
                })
                .orElseGet(() -> chatRoomMemberRepository.save(new ChatRoomMember(
                        UUID.randomUUID(),
                        roomId,
                        tenant.getComCd(),
                        userId,
                        Instant.now(),
                        ChatRoomRole.MEMBER)));
    }

    @Transactional
    public void removeMember(TenantKey tenant, UUID roomId, String userId) {
        ChatRoom room = findJoinedRoom(tenant, roomId);
        if (room.getRoomType() == ChatRoomType.DIRECT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "direct chat member cannot be changed");
        }
        ChatRoomMember member = chatRoomMemberRepository.findByRoomIdAndComCdAndUserIdAndLeftAtIsNull(
                        roomId,
                        tenant.getComCd(),
                        userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "chat member not found"));
        member.leave(Instant.now());
        chatRoomMemberRepository.save(member);
    }

    public List<ChatRoomMember> listMembers(TenantKey tenant, UUID roomId) {
        findJoinedRoom(tenant, roomId);
        return chatRoomMemberRepository.findByRoomIdAndComCdAndLeftAtIsNull(roomId, tenant.getComCd());
    }

    @Transactional
    public ChatMessage sendMessage(TenantKey tenant, UUID roomId, SendChatMessageRequest request) {
        ChatRoom room = findJoinedRoom(tenant, roomId);
        ChatMessage message = chatMessageRepository.save(new ChatMessage(
                UUID.randomUUID(),
                roomId,
                tenant.getComCd(),
                tenant.getUserId(),
                request.getMessageType() == null ? ChatMessageType.TEXT : request.getMessageType(),
                request.getContent(),
                Instant.now()));
        room.updateLastMessage(message);
        chatRoomRepository.save(room);
        for (ChatRoomMember member : chatRoomMemberRepository.findByRoomIdAndComCdAndLeftAtIsNull(roomId, tenant.getComCd())) {
            eventPublisher.publishToUser(
                    new TenantKey(tenant.getComCd(), member.getUserId()),
                    "chat",
                    "chat.message.created",
                    Map.of("roomId", roomId.toString(), "messageId", message.getId().toString()));
        }
        return message;
    }

    public PageSlice<ChatMessage> listMessages(TenantKey tenant, UUID roomId, int page, int size) {
        findJoinedRoom(tenant, roomId);
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        Page<ChatMessage> result = chatMessageRepository.findByRoomIdAndComCdAndDeletedYnFalseOrderByCreatedAtDesc(
                roomId,
                tenant.getComCd(),
                PageRequest.of(safePage, safeSize));
        return new PageSlice<>(result.getContent(), safePage, safeSize, result.hasNext());
    }

    @Transactional
    public ChatMessage updateMessage(TenantKey tenant, UUID roomId, UUID messageId, SendChatMessageRequest request) {
        findJoinedRoom(tenant, roomId);
        ChatMessage message = chatMessageRepository.findByIdAndRoomIdAndComCdAndDeletedYnFalse(messageId, roomId, tenant.getComCd())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "chat message not found"));
        if (!message.getSenderUserId().equals(tenant.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "only sender can update message");
        }
        return chatMessageRepository.save(message.updateContent(request.getContent()));
    }

    @Transactional
    public void deleteMessage(TenantKey tenant, UUID roomId, UUID messageId) {
        findJoinedRoom(tenant, roomId);
        ChatMessage message = chatMessageRepository.findByIdAndRoomIdAndComCdAndDeletedYnFalse(messageId, roomId, tenant.getComCd())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "chat message not found"));
        if (!message.getSenderUserId().equals(tenant.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "only sender can delete message");
        }
        chatMessageRepository.save(message.delete());
    }

    public PageSlice<ChatMessage> searchMessages(TenantKey tenant, UUID roomId, String keyword, int page, int size) {
        findJoinedRoom(tenant, roomId);
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        Page<ChatMessage> result = chatMessageRepository.findByRoomIdAndComCdAndContentContainingIgnoreCaseAndDeletedYnFalseOrderByCreatedAtDesc(
                roomId,
                tenant.getComCd(),
                keyword == null ? "" : keyword,
                PageRequest.of(safePage, safeSize));
        return new PageSlice<>(result.getContent(), safePage, safeSize, result.hasNext());
    }

    @Transactional
    public ChatRoomMember updateRead(TenantKey tenant, UUID roomId, UUID lastReadMessageId) {
        findJoinedRoom(tenant, roomId);
        ChatRoomMember member = chatRoomMemberRepository.findByRoomIdAndComCdAndUserIdAndLeftAtIsNull(
                        roomId,
                        tenant.getComCd(),
                        tenant.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "chat member not found"));
        member.updateLastReadMessage(lastReadMessageId);
        return chatRoomMemberRepository.save(member);
    }

    public Map<String, Long> unreadCount(TenantKey tenant, UUID roomId) {
        findJoinedRoom(tenant, roomId);
        ChatRoomMember member = chatRoomMemberRepository.findByRoomIdAndComCdAndUserIdAndLeftAtIsNull(
                        roomId,
                        tenant.getComCd(),
                        tenant.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "chat member not found"));
        return Map.of("count", chatMessageRepository.countUnread(
                roomId,
                tenant.getComCd(),
                tenant.getUserId(),
                member.getLastReadMessageId()));
    }

    private ChatRoom findJoinedRoom(TenantKey tenant, UUID roomId) {
        ChatRoom room = chatRoomRepository.findByIdAndComCdAndDeletedYnFalse(roomId, tenant.getComCd())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "chat room not found"));
        if (!chatRoomMemberRepository.existsByRoomIdAndComCdAndUserIdAndLeftAtIsNull(roomId, tenant.getComCd(), tenant.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "chat room is not joined");
        }
        return room;
    }

    private List<String> normalizeMembers(String actorUserId, List<String> requestedMembers) {
        LinkedHashSet<String> members = new LinkedHashSet<>();
        members.add(actorUserId);
        if (requestedMembers != null) {
            requestedMembers.stream()
                    .filter(userId -> userId != null && !userId.isBlank())
                    .forEach(members::add);
        }
        return new ArrayList<>(members);
    }
}
