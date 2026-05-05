package com.upmudoum.erp.domain.warehouse.service;

import com.upmudoum.erp.common.exception.BusinessException;
import com.upmudoum.erp.domain.item.entity.Item;
import com.upmudoum.erp.domain.item.repository.ItemRepository;
import com.upmudoum.erp.domain.warehouse.dto.WarehouseSafetyStockRequest;
import com.upmudoum.erp.domain.warehouse.dto.WarehouseSafetyStockResponse;
import com.upmudoum.erp.domain.warehouse.entity.Warehouse;
import com.upmudoum.erp.domain.warehouse.entity.WarehouseSafetyStock;
import com.upmudoum.erp.domain.warehouse.repository.WarehouseRepository;
import com.upmudoum.erp.domain.warehouse.repository.WarehouseSafetyStockRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class WarehouseSafetyStockService {

    private final WarehouseSafetyStockRepository safetyStockRepository;
    private final ItemRepository itemRepository;
    private final WarehouseRepository warehouseRepository;

    public WarehouseSafetyStockService(WarehouseSafetyStockRepository safetyStockRepository, ItemRepository itemRepository,
                                       WarehouseRepository warehouseRepository) {
        this.safetyStockRepository = safetyStockRepository;
        this.itemRepository = itemRepository;
        this.warehouseRepository = warehouseRepository;
    }

    @Transactional
    public WarehouseSafetyStockResponse save(WarehouseSafetyStockRequest request) {
        WarehouseSafetyStock safetyStock = safetyStockRepository
                .findByItemIdAndWarehouseId(request.getItemId(), request.getWarehouseId())
                .orElseGet(() -> newSafetyStock(request));
        safetyStock.update(request.getSafetyQuantity(), request.isActive());
        return WarehouseSafetyStockResponse.from(safetyStockRepository.save(safetyStock));
    }

    public List<WarehouseSafetyStockResponse> findByWarehouse(Long warehouseId) {
        return safetyStockRepository.findByWarehouseIdAndActiveTrueOrderByItemCodeValueAsc(warehouseId).stream()
                .map(WarehouseSafetyStockResponse::from)
                .toList();
    }

    private WarehouseSafetyStock newSafetyStock(WarehouseSafetyStockRequest request) {
        Item item = itemRepository.findById(request.getItemId()).orElseThrow(() -> new BusinessException("Item not found"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new BusinessException("Warehouse not found"));
        return new WarehouseSafetyStock(item, warehouse, request.getSafetyQuantity());
    }
}
