package com.upmudoum.erp.domain.warehouse.controller;

import com.upmudoum.erp.common.response.ApiResponse;
import com.upmudoum.erp.domain.warehouse.dto.WarehouseSafetyStockRequest;
import com.upmudoum.erp.domain.warehouse.dto.WarehouseSafetyStockResponse;
import com.upmudoum.erp.domain.warehouse.service.WarehouseSafetyStockService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/erp/warehouse-safety-stocks")
public class WarehouseSafetyStockController {

    private final WarehouseSafetyStockService safetyStockService;

    public WarehouseSafetyStockController(WarehouseSafetyStockService safetyStockService) {
        this.safetyStockService = safetyStockService;
    }

    @PostMapping
    public ApiResponse<WarehouseSafetyStockResponse> save(@Valid @RequestBody WarehouseSafetyStockRequest request) {
        return ApiResponse.ok(safetyStockService.save(request));
    }

    @GetMapping
    public ApiResponse<List<WarehouseSafetyStockResponse>> findByWarehouse(@RequestParam Long warehouseId) {
        return ApiResponse.ok(safetyStockService.findByWarehouse(warehouseId));
    }
}
