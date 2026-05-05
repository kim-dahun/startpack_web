package com.upmudoum.groupware.domain.schedule.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upmudoum.groupware.domain.schedule.entity.ScheduleOccurrenceExclusion;

public interface ScheduleOccurrenceExclusionRepository extends JpaRepository<ScheduleOccurrenceExclusion, UUID> {

    List<ScheduleOccurrenceExclusion> findByComCdAndScheduleId(String comCd, UUID scheduleId);

    Optional<ScheduleOccurrenceExclusion> findByComCdAndScheduleIdAndOccurrenceDate(String comCd, UUID scheduleId, LocalDate occurrenceDate);
}
