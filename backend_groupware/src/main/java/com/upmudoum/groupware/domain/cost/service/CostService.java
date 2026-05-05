package com.upmudoum.groupware.domain.cost.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.upmudoum.groupware.common.vo.TenantKey;
import com.upmudoum.groupware.domain.cost.dto.CostAccountRequest;
import com.upmudoum.groupware.domain.cost.dto.CostItemRequest;
import com.upmudoum.groupware.domain.cost.dto.ScheduleCostRequest;
import com.upmudoum.groupware.domain.cost.entity.CostAccount;
import com.upmudoum.groupware.domain.cost.entity.CostItem;
import com.upmudoum.groupware.domain.cost.entity.ScheduleCost;
import com.upmudoum.groupware.domain.cost.repository.CostAccountRepository;
import com.upmudoum.groupware.domain.cost.repository.CostItemRepository;
import com.upmudoum.groupware.domain.cost.repository.ScheduleCostRepository;

@Service
public class CostService {

    private final CostItemRepository costItemRepository;
    private final CostAccountRepository costAccountRepository;
    private final ScheduleCostRepository scheduleCostRepository;

    public CostService(
            CostItemRepository costItemRepository,
            CostAccountRepository costAccountRepository,
            ScheduleCostRepository scheduleCostRepository) {
        this.costItemRepository = costItemRepository;
        this.costAccountRepository = costAccountRepository;
        this.scheduleCostRepository = scheduleCostRepository;
    }

    public CostItem createItem(TenantKey tenant, CostItemRequest request) {
        return costItemRepository.save(new CostItem(UUID.randomUUID(), tenant.getComCd(), request.getCostItemName(), request.isEnabled()));
    }

    public List<CostItem> listItems(TenantKey tenant) {
        return costItemRepository.findByComCdAndEnabledTrueAndDeletedYnFalseOrderByCostItemNameAsc(tenant.getComCd());
    }

    public CostItem updateItem(TenantKey tenant, UUID costItemId, CostItemRequest request) {
        CostItem item = costItemRepository.findByIdAndComCdAndDeletedYnFalse(costItemId, tenant.getComCd())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "cost item not found"));
        return costItemRepository.save(item.update(request.getCostItemName(), request.isEnabled()));
    }

    public void deleteItem(TenantKey tenant, UUID costItemId) {
        CostItem item = costItemRepository.findByIdAndComCdAndDeletedYnFalse(costItemId, tenant.getComCd())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "cost item not found"));
        costItemRepository.save(item.delete());
    }

    public CostAccount createAccount(TenantKey tenant, CostAccountRequest request) {
        return costAccountRepository.save(new CostAccount(UUID.randomUUID(), tenant.getComCd(), request.getAccountName(), request.isEnabled()));
    }

    public List<CostAccount> listAccounts(TenantKey tenant) {
        return costAccountRepository.findByComCdAndEnabledTrueAndDeletedYnFalseOrderByAccountNameAsc(tenant.getComCd());
    }

    public CostAccount updateAccount(TenantKey tenant, UUID accountId, CostAccountRequest request) {
        CostAccount account = costAccountRepository.findByIdAndComCdAndDeletedYnFalse(accountId, tenant.getComCd())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "cost account not found"));
        return costAccountRepository.save(account.update(request.getAccountName(), request.isEnabled()));
    }

    public void deleteAccount(TenantKey tenant, UUID accountId) {
        CostAccount account = costAccountRepository.findByIdAndComCdAndDeletedYnFalse(accountId, tenant.getComCd())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "cost account not found"));
        costAccountRepository.save(account.delete());
    }

    public ScheduleCost createScheduleCost(TenantKey tenant, ScheduleCostRequest request) {
        return scheduleCostRepository.save(new ScheduleCost(
                UUID.randomUUID(),
                tenant.getComCd(),
                request.getScheduleId(),
                request.getProjectId(),
                request.getProjectCode(),
                request.getCostDate(),
                request.getCostItemId(),
                request.getAccountId(),
                request.getAmount(),
                request.getDescription()));
    }

    public List<ScheduleCost> listBySchedule(TenantKey tenant, UUID scheduleId) {
        return scheduleCostRepository.findByComCdAndScheduleIdAndDeletedYnFalseOrderByCostDateDesc(tenant.getComCd(), scheduleId);
    }

    public List<ScheduleCost> listByProjectId(TenantKey tenant, UUID projectId) {
        return scheduleCostRepository.findByComCdAndProjectIdAndDeletedYnFalseOrderByCostDateDesc(tenant.getComCd(), projectId);
    }

    public List<ScheduleCost> listByProjectCode(TenantKey tenant, String projectCode) {
        return scheduleCostRepository.findByComCdAndProjectCodeAndDeletedYnFalseOrderByCostDateDesc(tenant.getComCd(), projectCode);
    }

    public ScheduleCost getScheduleCost(TenantKey tenant, UUID scheduleCostId) {
        return scheduleCostRepository.findByIdAndComCdAndDeletedYnFalse(scheduleCostId, tenant.getComCd())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "schedule cost not found"));
    }

    public ScheduleCost updateScheduleCost(TenantKey tenant, UUID scheduleCostId, ScheduleCostRequest request) {
        ScheduleCost cost = getScheduleCost(tenant, scheduleCostId);
        return scheduleCostRepository.save(cost.update(request.getScheduleId(), request.getProjectId(), request.getProjectCode(),
                request.getCostDate(), request.getCostItemId(), request.getAccountId(), request.getAmount(), request.getDescription()));
    }

    public void deleteScheduleCost(TenantKey tenant, UUID scheduleCostId) {
        scheduleCostRepository.save(getScheduleCost(tenant, scheduleCostId).delete());
    }

    public List<ScheduleCost> searchScheduleCosts(TenantKey tenant, UUID projectId, String projectCode, UUID costItemId,
            UUID accountId, LocalDate from, LocalDate to) {
        return scheduleCostRepository.findByComCdAndDeletedYnFalseOrderByCostDateDesc(tenant.getComCd()).stream()
                .filter(cost -> projectId == null || projectId.equals(cost.getProjectId()))
                .filter(cost -> projectCode == null || projectCode.equals(cost.getProjectCode()))
                .filter(cost -> costItemId == null || costItemId.equals(cost.getCostItemId()))
                .filter(cost -> accountId == null || accountId.equals(cost.getAccountId()))
                .filter(cost -> from == null || !cost.getCostDate().isBefore(from))
                .filter(cost -> to == null || !cost.getCostDate().isAfter(to))
                .toList();
    }

    public Map<String, BigDecimal> summary(List<ScheduleCost> costs) {
        BigDecimal total = costs.stream()
                .map(ScheduleCost::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return Map.of("totalAmount", total);
    }
}
