package com.upmudoum.erp.domain.inventory.service;

import com.upmudoum.erp.common.exception.BusinessException;
import com.upmudoum.erp.domain.inventory.dto.InventoryTransferRequest;
import com.upmudoum.erp.domain.inventory.dto.InventoryTransferResponse;
import com.upmudoum.erp.domain.inventory.entity.InventoryMovement;
import com.upmudoum.erp.domain.inventory.entity.InventoryTransfer;
import com.upmudoum.erp.domain.inventory.repository.InventoryTransferRepository;
import com.upmudoum.erp.domain.inventory.vo.InventoryMovementType;
import com.upmudoum.erp.domain.inventory.vo.InventoryReferenceType;
import com.upmudoum.erp.domain.item.entity.Item;
import com.upmudoum.erp.domain.item.repository.ItemRepository;
import com.upmudoum.erp.domain.warehouse.entity.Warehouse;
import com.upmudoum.erp.domain.warehouse.repository.WarehouseRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class InventoryTransferService {

    private final InventoryTransferRepository transferRepository;
    private final ItemRepository itemRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryLotService inventoryLotService;

    public InventoryTransferService(InventoryTransferRepository transferRepository, ItemRepository itemRepository,
                                    WarehouseRepository warehouseRepository, InventoryLotService inventoryLotService) {
        this.transferRepository = transferRepository;
        this.itemRepository = itemRepository;
        this.warehouseRepository = warehouseRepository;
        this.inventoryLotService = inventoryLotService;
    }

    @Transactional
    public InventoryTransferResponse transfer(InventoryTransferRequest request) {
        if (request.getFromWarehouseId().equals(request.getToWarehouseId())) {
            throw new BusinessException("Source and target warehouses must be different");
        }
        if (transferRepository.existsByTransferNo(request.getTransferNo())) {
            throw new BusinessException("Transfer number already exists");
        }
        Item item = itemRepository.findById(request.getItemId()).orElseThrow(() -> new BusinessException("Item not found"));
        Warehouse fromWarehouse = warehouseRepository.findById(request.getFromWarehouseId())
                .orElseThrow(() -> new BusinessException("Source warehouse not found"));
        Warehouse toWarehouse = warehouseRepository.findById(request.getToWarehouseId())
                .orElseThrow(() -> new BusinessException("Target warehouse not found"));
        InventoryTransfer transfer = transferRepository.save(new InventoryTransfer(request.getTransferNo(), item,
                fromWarehouse, toWarehouse, request.getQuantity(), request.getMemo(), LocalDateTime.now()));
        BigDecimal zero = BigDecimal.ZERO;
        InventoryMovement outMovement = inventoryLotService.issue(item.getId(), fromWarehouse.getId(), request.getQuantity(),
                zero, zero, InventoryMovementType.TRANSFER_OUT, InventoryReferenceType.TRANSFER, transfer.getId(),
                request.getLotSelections(), "inventory transfer out no=" + request.getTransferNo());
        InventoryMovement inMovement = inventoryLotService.receive(item.getId(), toWarehouse.getId(), request.getToLotNo(),
                request.getQuantity(), zero, zero, InventoryMovementType.TRANSFER_IN, InventoryReferenceType.TRANSFER,
                transfer.getId(), "inventory transfer in no=" + request.getTransferNo());
        transfer.linkMovements(outMovement, inMovement);
        return InventoryTransferResponse.from(transfer);
    }

    public List<InventoryTransferResponse> findTransfers(Long itemId) {
        return transferRepository.findByItemIdOrderByTransferredAtDescIdDesc(itemId).stream()
                .map(InventoryTransferResponse::from)
                .toList();
    }
}
