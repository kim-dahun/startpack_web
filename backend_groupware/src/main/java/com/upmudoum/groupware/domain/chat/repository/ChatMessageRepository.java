package com.upmudoum.groupware.domain.chat.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.upmudoum.groupware.domain.chat.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    Page<ChatMessage> findByRoomIdAndComCdAndDeletedYnFalseOrderByCreatedAtDesc(UUID roomId, String comCd, Pageable pageable);

    java.util.Optional<ChatMessage> findByIdAndRoomIdAndComCdAndDeletedYnFalse(UUID id, UUID roomId, String comCd);

    Page<ChatMessage> findByRoomIdAndComCdAndContentContainingIgnoreCaseAndDeletedYnFalseOrderByCreatedAtDesc(
            UUID roomId,
            String comCd,
            String content,
            Pageable pageable);

    @Query("""
            select count(m)
            from ChatMessage m
            where m.roomId = :roomId
              and m.comCd = :comCd
              and m.deletedYn = false
              and m.senderUserId <> :userId
              and (:lastReadMessageId is null or m.createdAt > (
                    select readMessage.createdAt
                    from ChatMessage readMessage
                    where readMessage.id = :lastReadMessageId
                      and readMessage.comCd = :comCd
              ))
            """)
    long countUnread(
            @Param("roomId") UUID roomId,
            @Param("comCd") String comCd,
            @Param("userId") String userId,
            @Param("lastReadMessageId") UUID lastReadMessageId);
}
