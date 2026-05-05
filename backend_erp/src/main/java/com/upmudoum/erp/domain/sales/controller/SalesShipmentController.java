package com.upmudoum.erp.domain.sales.controller;

import com.upmudoum.erp.common.response.ApiResponse;
import com.upmudoum.erp.domain.sales.dto.SalesShipmentItemResponse;
import com.upmudoum.erp.domain.sales.dto.SalesShipmentRequest;
import com.upmudoum.erp.domain.sales.service.SalesShipmentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/erp/sales")
public class SalesShipmentController {

    private final SalesShipmentService salesShipmentService;

    public SalesShipmentController(SalesShipmentService salesShipmentService) {
        this.salesShipmentService = salesShipmentService;
    }

    @PostMapping("/shipments")
    public ApiResponse<SalesShipmentItemResponse> ship(@Valid @RequestBody SalesShipmentRequest request) {
        return ApiResponse.ok(SalesShipmentItemResponse.from(salesShipmentService.ship(
                request.getPartnerId(), request.getItemId(), request.getWarehouseId(), request.getQuantity(),
                request.getUnitPrice(), request.getSalesDate(), request.getLotSelections())));
    }

    @GetMapping("/shipments/items")
    public ApiResponse<List<SalesShipmentItemResponse>> findShipmentItems(@RequestParam Long itemId) {
        return ApiResponse.ok(salesShipmentService.findShipmentItems(itemId));
    }
}
