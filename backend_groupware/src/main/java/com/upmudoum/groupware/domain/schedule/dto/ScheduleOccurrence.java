package com.upmudoum.groupware.domain.schedule.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleOccurrence {

    private UUID scheduleId;
    private String title;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
