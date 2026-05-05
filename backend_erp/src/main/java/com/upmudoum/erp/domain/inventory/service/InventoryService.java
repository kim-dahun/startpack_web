package com.upmudoum.erp.domain.inventory.service;

import com.upmudoum.erp.common.exception.BusinessException;
import com.upmudoum.erp.domain.inventory.dto.InventoryAdjustmentRequest;
import com.upmudoum.erp.domain.inventory.dto.InventoryBalanceResponse;
import com.upmudoum.erp.domain.inventory.dto.InventoryMovementResponse;
import com.upmudoum.erp.domain.inventory.entity.InventoryBalance;
import com.upmudoum.erp.domain.inventory.querydsl.InventoryQueryRepository;
import com.upmudoum.erp.domain.inventory.entity.InventoryMovement;
import com.upmudoum.erp.domain.inventory.repository.InventoryBalanceRepository;
import com.upmudoum.erp.domain.inventory.repository.InventoryMovementRepository;
import com.upmudoum.erp.domain.inventory.vo.InventoryMovementType;
import com.upmudoum.erp.domain.inventory.vo.Quantity;
import com.upmudoum.erp.domain.item.entity.Item;
import com.upmudoum.erp.domain.item.repository.ItemRepository;
import com.upmudoum.erp.domain.item.vo.ItemType;
import com.upmudoum.erp.domain.warehouse.entity.Warehouse;
import com.upmudoum.erp.domain.warehouse.repository.WarehouseRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class InventoryService {

    private final InventoryBalanceRepository balanceRepository;
    private final InventoryMovementRepository movementRepository;
    private final ItemRepository itemRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryQueryRepository inventoryQueryRepository;

    public InventoryService(InventoryBalanceRepository balanceRepository, InventoryMovementRepository movementRepository,
                            ItemRepository itemRepository, WarehouseRepository warehouseRepository,
                            InventoryQueryRepository inventoryQueryRepository) {
        this.balanceRepository = balanceRepository;
        this.movementRepository = movementRepository;
        this.itemRepository = itemRepository;
        this.warehouseRepository = warehouseRepository;
        this.inventoryQueryRepository = inventoryQueryRepository;
    }

    @Transactional
    public InventoryMovementResponse adjust(InventoryAdjustmentRequest request) {
        Item item = itemRepository.findById(request.getItemId()).orElseThrow(() -> new BusinessException("Item not found"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new BusinessException("Warehouse not found"));
        InventoryBalance balance = balanceRepository.findByItemIdAndWarehouseId(item.getId(), warehouse.getId())
                .orElseGet(() -> new InventoryBalance(item, warehouse));

        Quantity signedQuantity = signedQuantity(request.getMovementType(), Quantity.of(request.getQuantity()));
        Quantity nextQuantity = balance.getQuantity().add(signedQuantity);
        if (nextQuantity.isNegative()) {
            throw new BusinessException("Negative inventory is not allowed");
        }

        balance.add(signedQuantity);
        InventoryBalance savedBalance = balanceRepository.save(balance);
        InventoryMovement movement = new InventoryMovement(item, warehouse, request.getMovementType(), signedQuantity,
                savedBalance.getQuantity(), request.getMemo(), LocalDateTime.now());
        return InventoryMovementResponse.from(movementRepository.save(movement));
    }

    public List<InventoryMovementResponse> findMovements(Long itemId, Long warehouseId) {
        return movementRepository.findByItemIdAndWarehouseIdOrderByOccurredAtDesc(itemId, warehouseId)
                .stream()
                .map(InventoryMovementResponse::from)
                .toList();
    }

    public InventoryBalanceResponse findBalance(Long itemId, Long warehouseId) {
        InventoryBalance balance = balanceRepository.findByItemIdAndWarehouseId(itemId, warehouseId)
                .orElseThrow(() -> new BusinessException("Inventory balance not found"));
        return InventoryBalanceResponse.from(balance);
    }

    public List<InventoryBalanceResponse> searchBalances(Long warehouseId, ItemType itemType, Boolean positiveOnly,
                                                         String keyword) {
        return inventoryQueryRepository.searchBalances(warehouseId, itemType, positiveOnly, keyword).stream()
                .map(InventoryBalanceResponse::from)
                .toList();
    }

    private Quantity signedQuantity(InventoryMovementType movementType, Quantity quantity) {
        if (movementType == InventoryMovementType.ADJUSTMENT_OUT || movementType == InventoryMovementType.ISSUE
                || movementType == InventoryMovementType.TRANSFER_OUT) {
            return quantity.negate();
        }
        return quantity;
    }
}
