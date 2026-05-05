package com.upmudoum.groupware.domain.schedule.dto;

import java.time.LocalDate;

import com.upmudoum.groupware.domain.schedule.vo.RecurrenceFrequency;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRecurrenceRequest {

    @NotNull
    private RecurrenceFrequency frequency;
    @Min(1)
    private int intervalValue;
    private LocalDate untilDate;
    private Integer countLimit;
}
