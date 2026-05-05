package com.upmudoum.groupware.domain.message.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.upmudoum.groupware.domain.message.entity.MessageItem;

public interface MessageRepository extends JpaRepository<MessageItem, UUID> {

    @Query("""
            select m
            from MessageItem m
            where m.comCd = :comCd
              and ((m.senderUserId = :userId and m.receiverUserId = :peerUserId)
                or (m.senderUserId = :peerUserId and m.receiverUserId = :userId))
            order by m.sentAt desc
            """)
    Page<MessageItem> findConversation(
            @Param("comCd") String comCd,
            @Param("userId") String userId,
            @Param("peerUserId") String peerUserId,
            Pageable pageable);

    @Query("""
            select count(m)
            from MessageItem m
            where m.comCd = :comCd
              and m.senderUserId = :peerUserId
              and m.receiverUserId = :userId
              and m.readAt is null
            """)
    long countUnreadFromPeer(
            @Param("comCd") String comCd,
            @Param("userId") String userId,
            @Param("peerUserId") String peerUserId);

    @Query("""
            select m
            from MessageItem m
            where m.comCd = :comCd
              and m.senderUserId = :peerUserId
              and m.receiverUserId = :userId
              and m.readAt is null
              and m.sentAt <= (
                    select target.sentAt
                    from MessageItem target
                    where target.id = :lastReadMessageId
                      and target.comCd = :comCd
              )
            """)
    java.util.List<MessageItem> findUnreadUntil(
            @Param("comCd") String comCd,
            @Param("userId") String userId,
            @Param("peerUserId") String peerUserId,
            @Param("lastReadMessageId") UUID lastReadMessageId);

    java.util.Optional<MessageItem> findByIdAndComCd(UUID id, String comCd);

    java.util.List<MessageItem> findByComCd(String comCd);
}
