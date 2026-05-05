package com.upmudoum.groupware.domain.schedule.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.upmudoum.groupware.domain.schedule.vo.ScheduleScope;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequest {

    @NotBlank
    private String title;

    private UUID projectId;
    private String projectCode;
    private String memo;

    @NotNull
    private LocalDateTime startAt;

    @NotNull
    private LocalDateTime endAt;

    private boolean allDay;

    @NotNull
    private ScheduleScope scope;

    public ScheduleRequest(String title, String memo, LocalDateTime startAt, LocalDateTime endAt, boolean allDay, ScheduleScope scope) {
        this.title = title;
        this.memo = memo;
        this.startAt = startAt;
        this.endAt = endAt;
        this.allDay = allDay;
        this.scope = scope;
    }
}
