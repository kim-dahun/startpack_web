package com.upmudoum.erp.domain.lot.service;

import com.upmudoum.erp.domain.inventory.repository.InventoryLotBalanceRepository;
import com.upmudoum.erp.domain.inventory.repository.InventoryMovementLotRepository;
import com.upmudoum.erp.domain.inventory.querydsl.InventoryQueryRepository;
import com.upmudoum.erp.domain.lot.dto.LotBalanceResponse;
import com.upmudoum.erp.domain.lot.dto.LotMovementResponse;
import com.upmudoum.erp.domain.lot.dto.LotResponse;
import com.upmudoum.erp.domain.lot.repository.LotRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LotQueryService {

    private final LotRepository lotRepository;
    private final InventoryLotBalanceRepository lotBalanceRepository;
    private final InventoryMovementLotRepository movementLotRepository;
    private final InventoryQueryRepository inventoryQueryRepository;

    public LotQueryService(LotRepository lotRepository, InventoryLotBalanceRepository lotBalanceRepository,
                           InventoryMovementLotRepository movementLotRepository,
                           InventoryQueryRepository inventoryQueryRepository) {
        this.lotRepository = lotRepository;
        this.lotBalanceRepository = lotBalanceRepository;
        this.movementLotRepository = movementLotRepository;
        this.inventoryQueryRepository = inventoryQueryRepository;
    }

    public List<LotResponse> findLots(Long itemId) {
        return lotRepository.findByItemIdAndEnabledTrueOrderByIdDesc(itemId).stream()
                .map(LotResponse::from)
                .toList();
    }

    public List<LotBalanceResponse> findBalances(Long itemId, Long warehouseId) {
        return lotBalanceRepository.findByItemIdAndWarehouseIdOrderByFirstReceivedAtAscIdAsc(itemId, warehouseId).stream()
                .map(LotBalanceResponse::from)
                .toList();
    }

    public List<LotBalanceResponse> searchBalances(Long itemId, Long warehouseId, String lotNo, Boolean positiveOnly) {
        return inventoryQueryRepository.searchLotBalances(itemId, warehouseId, lotNo, positiveOnly).stream()
                .map(LotBalanceResponse::from)
                .toList();
    }

    public List<LotMovementResponse> findMovementLots(Long movementId) {
        return movementLotRepository.findByMovementId(movementId).stream()
                .map(LotMovementResponse::from)
                .toList();
    }
}
