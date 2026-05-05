package com.upmudoum.groupware.domain.chat.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upmudoum.groupware.domain.chat.entity.ChatRoomMember;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, UUID> {

    List<ChatRoomMember> findByRoomIdAndComCdAndLeftAtIsNull(UUID roomId, String comCd);

    Optional<ChatRoomMember> findByRoomIdAndComCdAndUserId(UUID roomId, String comCd, String userId);

    Optional<ChatRoomMember> findByRoomIdAndComCdAndUserIdAndLeftAtIsNull(UUID roomId, String comCd, String userId);

    boolean existsByRoomIdAndComCdAndUserIdAndLeftAtIsNull(UUID roomId, String comCd, String userId);
}
