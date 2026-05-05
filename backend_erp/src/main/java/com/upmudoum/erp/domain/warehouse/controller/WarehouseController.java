package com.upmudoum.erp.domain.warehouse.controller;

import com.upmudoum.erp.common.response.ApiResponse;
import com.upmudoum.erp.domain.warehouse.dto.WarehouseRequest;
import com.upmudoum.erp.domain.warehouse.dto.WarehouseResponse;
import com.upmudoum.erp.domain.warehouse.service.WarehouseService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/erp/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping
    public ApiResponse<WarehouseResponse> create(@Valid @RequestBody WarehouseRequest request) {
        return ApiResponse.ok(warehouseService.create(request));
    }

    @GetMapping
    public ApiResponse<List<WarehouseResponse>> findAll() {
        return ApiResponse.ok(warehouseService.findAll());
    }

    @PutMapping("/{id}")
    public ApiResponse<WarehouseResponse> update(@PathVariable Long id, @Valid @RequestBody WarehouseRequest request) {
        return ApiResponse.ok(warehouseService.update(id, request));
    }
}
