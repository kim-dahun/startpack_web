package com.upmudoum.groupware.domain.schedule.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upmudoum.groupware.domain.schedule.entity.ScheduleRecurrenceRule;

public interface ScheduleRecurrenceRuleRepository extends JpaRepository<ScheduleRecurrenceRule, UUID> {

    Optional<ScheduleRecurrenceRule> findByComCdAndScheduleId(String comCd, UUID scheduleId);

    List<ScheduleRecurrenceRule> findByComCdOrderByCreatedAtDesc(String comCd);
}
