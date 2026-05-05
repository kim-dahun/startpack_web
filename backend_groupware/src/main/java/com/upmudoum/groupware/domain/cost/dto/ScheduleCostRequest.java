package com.upmudoum.groupware.domain.cost.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleCostRequest {

    @NotNull
    private UUID scheduleId;
    private UUID projectId;
    private String projectCode;

    @NotNull
    private LocalDate costDate;

    @NotNull
    private UUID costItemId;

    @NotNull
    private UUID accountId;

    @NotNull
    private BigDecimal amount;
    private String description;
}
