package com.upmudoum.erp.domain.inventory.service;

import com.upmudoum.erp.common.exception.BusinessException;
import com.upmudoum.erp.domain.inventory.dto.LotDeductionRequest;
import com.upmudoum.erp.domain.inventory.entity.InventoryBalance;
import com.upmudoum.erp.domain.inventory.entity.InventoryLotBalance;
import com.upmudoum.erp.domain.inventory.entity.InventoryMovement;
import com.upmudoum.erp.domain.inventory.entity.InventoryMovementLot;
import com.upmudoum.erp.domain.inventory.repository.InventoryBalanceRepository;
import com.upmudoum.erp.domain.inventory.repository.InventoryLotBalanceRepository;
import com.upmudoum.erp.domain.inventory.repository.InventoryMovementLotRepository;
import com.upmudoum.erp.domain.inventory.repository.InventoryMovementRepository;
import com.upmudoum.erp.domain.inventory.vo.InventoryMovementType;
import com.upmudoum.erp.domain.inventory.vo.InventoryReferenceType;
import com.upmudoum.erp.domain.inventory.vo.Quantity;
import com.upmudoum.erp.domain.item.entity.Item;
import com.upmudoum.erp.domain.item.repository.ItemRepository;
import com.upmudoum.erp.domain.lot.entity.Lot;
import com.upmudoum.erp.domain.lot.repository.LotRepository;
import com.upmudoum.erp.domain.warehouse.entity.Warehouse;
import com.upmudoum.erp.domain.warehouse.repository.WarehouseRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class InventoryLotService {

    private final InventoryBalanceRepository balanceRepository;
    private final InventoryMovementRepository movementRepository;
    private final InventoryLotBalanceRepository lotBalanceRepository;
    private final InventoryMovementLotRepository movementLotRepository;
    private final ItemRepository itemRepository;
    private final WarehouseRepository warehouseRepository;
    private final LotRepository lotRepository;

    public InventoryLotService(InventoryBalanceRepository balanceRepository, InventoryMovementRepository movementRepository,
                               InventoryLotBalanceRepository lotBalanceRepository,
                               InventoryMovementLotRepository movementLotRepository, ItemRepository itemRepository,
                               WarehouseRepository warehouseRepository, LotRepository lotRepository) {
        this.balanceRepository = balanceRepository;
        this.movementRepository = movementRepository;
        this.lotBalanceRepository = lotBalanceRepository;
        this.movementLotRepository = movementLotRepository;
        this.itemRepository = itemRepository;
        this.warehouseRepository = warehouseRepository;
        this.lotRepository = lotRepository;
    }

    @Transactional
    public InventoryMovement receive(Long itemId, Long warehouseId, String lotNo, BigDecimal quantity,
                                     InventoryMovementType movementType, String memo) {
        return receive(itemId, warehouseId, lotNo, quantity, BigDecimal.ZERO, BigDecimal.ZERO,
                movementType, InventoryReferenceType.MANUAL, null, memo);
    }

    @Transactional
    public InventoryMovement receive(Long itemId, Long warehouseId, String lotNo, BigDecimal quantity,
                                     BigDecimal unitCost, BigDecimal supplyAmount, InventoryMovementType movementType,
                                     InventoryReferenceType referenceType, Long referenceId, String memo) {
        Item item = findItem(itemId);
        Warehouse warehouse = findWarehouse(warehouseId);
        Quantity receiveQuantity = Quantity.of(quantity);
        Lot lot = resolveLot(item, lotNo);

        InventoryBalance balance = balanceRepository.findByItemIdAndWarehouseId(itemId, warehouseId)
                .orElseGet(() -> new InventoryBalance(item, warehouse));
        balance.add(receiveQuantity);
        InventoryBalance savedBalance = balanceRepository.save(balance);

        InventoryLotBalance lotBalance = findLotBalance(item, warehouse, lot)
                .orElseGet(() -> new InventoryLotBalance(item, warehouse, lot, LocalDateTime.now()));
        lotBalance.add(receiveQuantity);
        lotBalanceRepository.save(lotBalance);

        InventoryMovement movement = movementRepository.save(new InventoryMovement(
                item, warehouse, movementType, referenceType, referenceId, receiveQuantity,
                unitCost, supplyAmount, savedBalance.getQuantity(), memo, LocalDateTime.now()));
        movementLotRepository.save(new InventoryMovementLot(movement, lot, receiveQuantity));
        return movement;
    }

    @Transactional
    public InventoryMovement issue(Long itemId, Long warehouseId, BigDecimal quantity, InventoryMovementType movementType,
                                   List<LotDeductionRequest> lotSelections, String memo) {
        return issue(itemId, warehouseId, quantity, BigDecimal.ZERO, BigDecimal.ZERO, movementType,
                InventoryReferenceType.MANUAL, null, lotSelections, memo);
    }

    @Transactional
    public InventoryMovement issue(Long itemId, Long warehouseId, BigDecimal quantity, BigDecimal unitCost,
                                   BigDecimal supplyAmount, InventoryMovementType movementType,
                                   InventoryReferenceType referenceType, Long referenceId,
                                   List<LotDeductionRequest> lotSelections, String memo) {
        Item item = findItem(itemId);
        Warehouse warehouse = findWarehouse(warehouseId);
        Quantity issueQuantity = Quantity.of(quantity);

        InventoryBalance balance = balanceRepository.findByItemIdAndWarehouseId(itemId, warehouseId)
                .orElseThrow(() -> new BusinessException("Inventory balance not found"));
        Quantity nextBalance = balance.getQuantity().subtract(issueQuantity);
        if (nextBalance.isNegative()) {
            throw new BusinessException("Negative inventory is not allowed");
        }

        List<InventoryLotBalance> sourceLots = (lotSelections == null || lotSelections.isEmpty())
                ? fifoLots(itemId, warehouseId)
                : selectedLots(lotSelections, itemId, warehouseId);

        InventoryMovement movement = movementRepository.save(new InventoryMovement(
                item, warehouse, movementType, referenceType, referenceId, issueQuantity.negate(),
                unitCost, supplyAmount, nextBalance, memo, LocalDateTime.now()));
        deductLots(sourceLots, lotSelections, issueQuantity, movement);

        balance.add(issueQuantity.negate());
        balanceRepository.save(balance);
        return movement;
    }

    private void deductLots(List<InventoryLotBalance> sourceLots, List<LotDeductionRequest> selections,
                            Quantity issueQuantity, InventoryMovement movement) {
        Quantity remaining = issueQuantity;
        for (InventoryLotBalance lotBalance : sourceLots) {
            if (!remaining.isPositive()) {
                break;
            }
            Quantity requested = selectedQuantity(selections, lotBalance);
            Quantity deduction = requested == null
                    ? (lotBalance.getQuantity().isGreaterThanOrEqualTo(remaining) ? remaining : lotBalance.getQuantity())
                    : (requested.isGreaterThanOrEqualTo(remaining) ? remaining : requested);
            if (!lotBalance.getQuantity().isGreaterThanOrEqualTo(deduction)) {
                throw new BusinessException("Lot inventory is not enough");
            }
            lotBalance.add(deduction.negate());
            lotBalanceRepository.save(lotBalance);
            movementLotRepository.save(new InventoryMovementLot(movement, lotBalance.getLot(), deduction.negate()));
            remaining = remaining.subtract(deduction);
        }
        if (remaining.isPositive()) {
            throw new BusinessException("Lot inventory is not enough");
        }
    }

    private Quantity selectedQuantity(List<LotDeductionRequest> selections, InventoryLotBalance lotBalance) {
        if (selections == null || selections.isEmpty()) {
            return null;
        }
        Long lotId = lotBalance.getLot() == null ? null : lotBalance.getLot().getId();
        return selections.stream()
                .filter(selection -> lotId != null && lotId.equals(selection.getLotId()))
                .findFirst()
                .map(selection -> Quantity.of(selection.getQuantity()))
                .orElse(Quantity.zero());
    }

    private List<InventoryLotBalance> fifoLots(Long itemId, Long warehouseId) {
        return lotBalanceRepository.findByItemIdAndWarehouseIdAndQuantityValueGreaterThanOrderByFirstReceivedAtAscIdAsc(
                itemId, warehouseId, BigDecimal.ZERO);
    }

    private List<InventoryLotBalance> selectedLots(List<LotDeductionRequest> selections, Long itemId, Long warehouseId) {
        return selections.stream()
                .map(selection -> lotBalanceRepository.findByItemIdAndWarehouseIdAndLotId(itemId, warehouseId, selection.getLotId())
                        .orElseThrow(() -> new BusinessException("Selected lot balance not found")))
                .toList();
    }

    private java.util.Optional<InventoryLotBalance> findLotBalance(Item item, Warehouse warehouse, Lot lot) {
        if (lot == null) {
            return lotBalanceRepository.findByItemIdAndWarehouseIdAndLotIsNull(item.getId(), warehouse.getId());
        }
        return lotBalanceRepository.findByItemIdAndWarehouseIdAndLotId(item.getId(), warehouse.getId(), lot.getId());
    }

    private Lot resolveLot(Item item, String lotNo) {
        if (lotNo == null || lotNo.isBlank()) {
            return null;
        }
        return lotRepository.findByItemIdAndLotNo(item.getId(), lotNo)
                .orElseGet(() -> lotRepository.save(new Lot(item, lotNo, null, null)));
    }

    private Item findItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new BusinessException("Item not found"));
    }

    private Warehouse findWarehouse(Long warehouseId) {
        return warehouseRepository.findById(warehouseId).orElseThrow(() -> new BusinessException("Warehouse not found"));
    }
}
