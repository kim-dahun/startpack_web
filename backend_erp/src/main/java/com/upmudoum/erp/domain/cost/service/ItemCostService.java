package com.upmudoum.erp.domain.cost.service;

import com.upmudoum.erp.common.exception.BusinessException;
import com.upmudoum.erp.domain.cost.dto.ItemActualCostHistoryResponse;
import com.upmudoum.erp.domain.cost.dto.ItemStandardCostResponse;
import com.upmudoum.erp.domain.cost.entity.ItemActualCostHistory;
import com.upmudoum.erp.domain.cost.entity.ItemStandardCost;
import com.upmudoum.erp.domain.cost.repository.ItemActualCostHistoryRepository;
import com.upmudoum.erp.domain.cost.repository.ItemStandardCostRepository;
import com.upmudoum.erp.domain.cost.vo.ActualCostReferenceType;
import com.upmudoum.erp.domain.item.entity.Item;
import com.upmudoum.erp.domain.item.repository.ItemRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ItemCostService {

    private final ItemRepository itemRepository;
    private final ItemStandardCostRepository standardCostRepository;
    private final ItemActualCostHistoryRepository actualCostHistoryRepository;

    public ItemCostService(ItemRepository itemRepository, ItemStandardCostRepository standardCostRepository,
                           ItemActualCostHistoryRepository actualCostHistoryRepository) {
        this.itemRepository = itemRepository;
        this.standardCostRepository = standardCostRepository;
        this.actualCostHistoryRepository = actualCostHistoryRepository;
    }

    @Transactional
    public ItemStandardCost registerStandardCost(Long itemId, BigDecimal standardCost, String currencyCode,
                                                 LocalDate effectiveFrom, LocalDate effectiveTo) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new BusinessException("Item not found"));
        return standardCostRepository.save(new ItemStandardCost(item, standardCost, currencyCode, effectiveFrom, effectiveTo));
    }

    @Transactional
    public ItemActualCostHistory recordActualCost(Long itemId, ActualCostReferenceType referenceType, Long referenceId,
                                                  BigDecimal unitCost, BigDecimal quantity) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new BusinessException("Item not found"));
        return actualCostHistoryRepository.save(new ItemActualCostHistory(
                item, referenceType, referenceId, unitCost, quantity, LocalDateTime.now()));
    }

    public List<ItemStandardCostResponse> findStandardCosts(Long itemId) {
        return standardCostRepository.findByItemIdAndEnabledTrue(itemId).stream()
                .map(ItemStandardCostResponse::from)
                .toList();
    }

    public List<ItemActualCostHistoryResponse> findActualCostHistories(Long itemId) {
        return actualCostHistoryRepository.findByItemId(itemId).stream()
                .map(ItemActualCostHistoryResponse::from)
                .toList();
    }

    @Transactional
    public List<ItemActualCostHistoryResponse> reverseActualCost(ActualCostReferenceType originalReferenceType,
                                                                 Long originalReferenceId) {
        List<ItemActualCostHistory> originals = actualCostHistoryRepository
                .findByReferenceTypeAndReferenceId(originalReferenceType, originalReferenceId);
        if (originals.isEmpty()) {
            throw new BusinessException("Original actual cost history not found");
        }
        return originals.stream()
                .map(original -> actualCostHistoryRepository.save(new ItemActualCostHistory(
                        original.getItem(), ActualCostReferenceType.REVERSAL, original.getId(),
                        original.getUnitCost().getValue().negate(), original.getQuantity().getValue().negate(),
                        LocalDateTime.now())))
                .map(ItemActualCostHistoryResponse::from)
                .toList();
    }
}
