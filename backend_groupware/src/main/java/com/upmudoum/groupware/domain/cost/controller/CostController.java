package com.upmudoum.groupware.domain.cost.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.upmudoum.groupware.common.RequestContextResolver;
import com.upmudoum.groupware.common.vo.TenantKey;
import com.upmudoum.groupware.domain.cost.dto.CostAccountRequest;
import com.upmudoum.groupware.domain.cost.dto.CostItemRequest;
import com.upmudoum.groupware.domain.cost.dto.ScheduleCostRequest;
import com.upmudoum.groupware.domain.cost.entity.CostAccount;
import com.upmudoum.groupware.domain.cost.entity.CostItem;
import com.upmudoum.groupware.domain.cost.entity.ScheduleCost;
import com.upmudoum.groupware.domain.cost.service.CostService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/groupware/costs")
public class CostController {

    private final RequestContextResolver contextResolver;
    private final CostService costService;

    public CostController(RequestContextResolver contextResolver, CostService costService) {
        this.contextResolver = contextResolver;
        this.costService = costService;
    }

    @PostMapping("/items")
    public CostItem createItem(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @Valid @RequestBody CostItemRequest request) {
        return costService.createItem(contextResolver.resolve(comCd, userId), request);
    }

    @GetMapping("/items")
    public List<CostItem> listItems(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId) {
        return costService.listItems(contextResolver.resolve(comCd, userId));
    }

    @PatchMapping("/items/{costItemId}")
    public CostItem updateItem(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID costItemId,
            @Valid @RequestBody CostItemRequest request) {
        return costService.updateItem(contextResolver.resolve(comCd, userId), costItemId, request);
    }

    @DeleteMapping("/items/{costItemId}")
    public void deleteItem(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID costItemId) {
        costService.deleteItem(contextResolver.resolve(comCd, userId), costItemId);
    }

    @PostMapping("/accounts")
    public CostAccount createAccount(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @Valid @RequestBody CostAccountRequest request) {
        return costService.createAccount(contextResolver.resolve(comCd, userId), request);
    }

    @GetMapping("/accounts")
    public List<CostAccount> listAccounts(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId) {
        return costService.listAccounts(contextResolver.resolve(comCd, userId));
    }

    @PatchMapping("/accounts/{accountId}")
    public CostAccount updateAccount(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID accountId,
            @Valid @RequestBody CostAccountRequest request) {
        return costService.updateAccount(contextResolver.resolve(comCd, userId), accountId, request);
    }

    @DeleteMapping("/accounts/{accountId}")
    public void deleteAccount(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID accountId) {
        costService.deleteAccount(contextResolver.resolve(comCd, userId), accountId);
    }

    @PostMapping("/schedule-costs")
    public ScheduleCost createScheduleCost(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @Valid @RequestBody ScheduleCostRequest request) {
        TenantKey tenant = contextResolver.resolve(comCd, userId);
        return costService.createScheduleCost(tenant, request);
    }

    @GetMapping("/schedule-costs/by-schedule/{scheduleId}")
    public List<ScheduleCost> listBySchedule(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID scheduleId) {
        return costService.listBySchedule(contextResolver.resolve(comCd, userId), scheduleId);
    }

    @GetMapping("/schedule-costs/{scheduleCostId}")
    public ScheduleCost getScheduleCost(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID scheduleCostId) {
        return costService.getScheduleCost(contextResolver.resolve(comCd, userId), scheduleCostId);
    }

    @PatchMapping("/schedule-costs/{scheduleCostId}")
    public ScheduleCost updateScheduleCost(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID scheduleCostId,
            @Valid @RequestBody ScheduleCostRequest request) {
        return costService.updateScheduleCost(contextResolver.resolve(comCd, userId), scheduleCostId, request);
    }

    @DeleteMapping("/schedule-costs/{scheduleCostId}")
    public void deleteScheduleCost(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID scheduleCostId) {
        costService.deleteScheduleCost(contextResolver.resolve(comCd, userId), scheduleCostId);
    }

    @GetMapping("/schedule-costs/by-project-id/{projectId}")
    public List<ScheduleCost> listByProjectId(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID projectId) {
        return costService.listByProjectId(contextResolver.resolve(comCd, userId), projectId);
    }

    @GetMapping("/schedule-costs/by-project-code/{projectCode}")
    public List<ScheduleCost> listByProjectCode(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable String projectCode) {
        return costService.listByProjectCode(contextResolver.resolve(comCd, userId), projectCode);
    }

    @GetMapping("/schedule-costs/search")
    public List<ScheduleCost> searchScheduleCosts(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) UUID projectId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String projectCode,
            @org.springframework.web.bind.annotation.RequestParam(required = false) UUID costItemId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) UUID accountId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) LocalDate from,
            @org.springframework.web.bind.annotation.RequestParam(required = false) LocalDate to) {
        return costService.searchScheduleCosts(contextResolver.resolve(comCd, userId), projectId, projectCode, costItemId, accountId, from, to);
    }

    @GetMapping("/schedule-costs/summary/by-schedule/{scheduleId}")
    public Map<String, BigDecimal> summaryBySchedule(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID scheduleId) {
        return costService.summary(costService.listBySchedule(contextResolver.resolve(comCd, userId), scheduleId));
    }

    @GetMapping("/schedule-costs/summary/by-project-id/{projectId}")
    public Map<String, BigDecimal> summaryByProjectId(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID projectId) {
        return costService.summary(costService.listByProjectId(contextResolver.resolve(comCd, userId), projectId));
    }

    @GetMapping("/schedule-costs/summary/by-project-code/{projectCode}")
    public Map<String, BigDecimal> summaryByProjectCode(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable String projectCode) {
        return costService.summary(costService.listByProjectCode(contextResolver.resolve(comCd, userId), projectCode));
    }
}
