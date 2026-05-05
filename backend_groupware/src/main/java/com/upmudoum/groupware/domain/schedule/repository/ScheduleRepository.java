package com.upmudoum.groupware.domain.schedule.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.upmudoum.groupware.domain.schedule.entity.ScheduleItem;
import com.upmudoum.groupware.domain.schedule.vo.ScheduleScope;

public interface ScheduleRepository extends JpaRepository<ScheduleItem, UUID> {

    @Query("""
            select s
            from ScheduleItem s
            where s.comCd = :comCd
              and (s.scope = :companyScope or s.ownerUserId = :userId)
              and s.deletedYn = false
              and (:from is null or s.endAt >= :from)
              and (:to is null or s.startAt <= :to)
            order by s.startAt asc
            """)
    List<ScheduleItem> findVisibleSchedules(
            @Param("comCd") String comCd,
            @Param("userId") String userId,
            @Param("companyScope") ScheduleScope companyScope,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    Optional<ScheduleItem> findByIdAndComCdAndOwnerUserIdAndDeletedYnFalse(UUID id, String comCd, String ownerUserId);

    Optional<ScheduleItem> findByIdAndComCdAndDeletedYnFalse(UUID id, String comCd);

    List<ScheduleItem> findByComCdAndProjectIdAndDeletedYnFalseOrderByStartAtAsc(String comCd, UUID projectId);

    List<ScheduleItem> findByComCdAndProjectCodeAndDeletedYnFalseOrderByStartAtAsc(String comCd, String projectCode);
}
