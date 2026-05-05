package com.upmudoum.groupware.domain.chat.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.upmudoum.groupware.domain.chat.entity.ChatRoom;
import com.upmudoum.groupware.domain.chat.vo.ChatRoomType;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {

    Optional<ChatRoom> findByIdAndComCdAndDeletedYnFalse(UUID id, String comCd);

    @Query("""
            select distinct r
            from ChatRoom r
            join ChatRoomMember m on m.roomId = r.id
            where r.comCd = :comCd
              and m.userId = :userId
              and m.leftAt is null
              and r.deletedYn = false
            order by r.lastMessageAt desc nulls last, r.createdAt desc
            """)
    List<ChatRoom> findJoinedRooms(@Param("comCd") String comCd, @Param("userId") String userId);

    @Query("""
            select r
            from ChatRoom r
            where r.comCd = :comCd
              and r.roomType = :roomType
              and r.deletedYn = false
              and exists (
                select 1 from ChatRoomMember m1
                where m1.roomId = r.id and m1.userId = :userA and m1.leftAt is null
              )
              and exists (
                select 1 from ChatRoomMember m2
                where m2.roomId = r.id and m2.userId = :userB and m2.leftAt is null
              )
            """)
    List<ChatRoom> findDirectRooms(
            @Param("comCd") String comCd,
            @Param("roomType") ChatRoomType roomType,
            @Param("userA") String userA,
            @Param("userB") String userB);
}
