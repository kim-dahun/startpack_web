package com.upmudoum.erp.domain.warehouse.service;

import com.upmudoum.erp.common.exception.BusinessException;
import com.upmudoum.erp.domain.warehouse.dto.WarehouseRequest;
import com.upmudoum.erp.domain.warehouse.dto.WarehouseResponse;
import com.upmudoum.erp.domain.warehouse.entity.Warehouse;
import com.upmudoum.erp.domain.warehouse.repository.WarehouseRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Transactional
    public WarehouseResponse create(WarehouseRequest request) {
        if (warehouseRepository.existsByCodeValue(request.getCode())) {
            throw new BusinessException("Warehouse code already exists");
        }
        Warehouse warehouse = new Warehouse(request.getCode(), request.getName(), request.getLocation());
        warehouse.update(request.getName(), request.getLocation(), request.getStatus());
        return WarehouseResponse.from(warehouseRepository.save(warehouse));
    }

    public List<WarehouseResponse> findAll() {
        return warehouseRepository.findAll().stream().map(WarehouseResponse::from).toList();
    }

    @Transactional
    public WarehouseResponse update(Long id, WarehouseRequest request) {
        Warehouse warehouse = warehouseRepository.findById(id).orElseThrow(() -> new BusinessException("Warehouse not found"));
        warehouse.update(request.getName(), request.getLocation(), request.getStatus());
        return WarehouseResponse.from(warehouse);
    }
}
