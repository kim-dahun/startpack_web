package com.upmudoum.groupware.domain.schedule.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.upmudoum.groupware.domain.schedule.vo.RecurrenceFrequency;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "gw_schedule_recurrence_rule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleRecurrenceRule {

    @Id
    private UUID id;
    private String comCd;
    private UUID scheduleId;

    @Enumerated(EnumType.STRING)
    private RecurrenceFrequency frequency;

    private int intervalValue;
    private LocalDate untilDate;
    private Integer countLimit;
    private Instant createdAt;

    public ScheduleRecurrenceRule(UUID id, String comCd, UUID scheduleId, RecurrenceFrequency frequency,
            int intervalValue, LocalDate untilDate, Integer countLimit, Instant createdAt) {
        this.id = id;
        this.comCd = comCd;
        this.scheduleId = scheduleId;
        this.frequency = frequency;
        this.intervalValue = intervalValue;
        this.untilDate = untilDate;
        this.countLimit = countLimit;
        this.createdAt = createdAt;
    }
}
