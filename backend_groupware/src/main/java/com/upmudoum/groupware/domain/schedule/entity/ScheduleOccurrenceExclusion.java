package com.upmudoum.groupware.domain.schedule.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "gw_schedule_occurrence_exclusion")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleOccurrenceExclusion {

    @Id
    private UUID id;
    private String comCd;
    private UUID scheduleId;
    private LocalDate occurrenceDate;
    private String reason;
    private Instant createdAt;

    public ScheduleOccurrenceExclusion(UUID id, String comCd, UUID scheduleId, LocalDate occurrenceDate, String reason, Instant createdAt) {
        this.id = id;
        this.comCd = comCd;
        this.scheduleId = scheduleId;
        this.occurrenceDate = occurrenceDate;
        this.reason = reason;
        this.createdAt = createdAt;
    }
}
