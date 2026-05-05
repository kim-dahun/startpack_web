package com.upmudoum.groupware.domain.cost.entity;

import java.math.BigDecimal;
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
@Table(name = "gw_schedule_cost")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleCost {

    @Id
    private UUID id;
    private String comCd;
    private UUID scheduleId;
    private UUID projectId;
    private String projectCode;
    private LocalDate costDate;
    private UUID costItemId;
    private UUID accountId;
    private BigDecimal amount;
    private String description;
    private boolean deletedYn;

    public ScheduleCost(UUID id, String comCd, UUID scheduleId, UUID projectId, String projectCode, LocalDate costDate,
            UUID costItemId, UUID accountId, BigDecimal amount, String description) {
        this.id = id;
        this.comCd = comCd;
        this.scheduleId = scheduleId;
        this.projectId = projectId;
        this.projectCode = projectCode;
        this.costDate = costDate;
        this.costItemId = costItemId;
        this.accountId = accountId;
        this.amount = amount;
        this.description = description;
        this.deletedYn = false;
    }

    public ScheduleCost update(UUID scheduleId, UUID projectId, String projectCode, LocalDate costDate,
            UUID costItemId, UUID accountId, BigDecimal amount, String description) {
        ScheduleCost cost = new ScheduleCost(id, comCd, scheduleId, projectId, projectCode, costDate, costItemId,
                accountId, amount, description);
        cost.deletedYn = deletedYn;
        return cost;
    }

    public ScheduleCost delete() {
        ScheduleCost cost = update(scheduleId, projectId, projectCode, costDate, costItemId, accountId, amount, description);
        cost.deletedYn = true;
        return cost;
    }
}
