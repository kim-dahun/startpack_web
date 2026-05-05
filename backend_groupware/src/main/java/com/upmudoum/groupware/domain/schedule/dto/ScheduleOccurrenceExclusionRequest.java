package com.upmudoum.groupware.domain.schedule.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleOccurrenceExclusionRequest {

    @NotNull
    private LocalDate occurrenceDate;
    private String reason;
}
