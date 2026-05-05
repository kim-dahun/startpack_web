package com.upmudoum.erp.domain.inventory.controller;

import com.upmudoum.erp.common.response.ApiResponse;
import com.upmudoum.erp.domain.inventory.dto.InventoryAdjustmentRequest;
import com.upmudoum.erp.domain.inventory.dto.InventoryBalanceResponse;
import com.upmudoum.erp.domain.inventory.dto.InventoryMovementResponse;
import com.upmudoum.erp.domain.inventory.dto.InventoryTransferRequest;
import com.upmudoum.erp.domain.inventory.dto.InventoryTransferResponse;
import com.upmudoum.erp.domain.inventory.service.InventoryService;
import com.upmudoum.erp.domain.inventory.service.InventoryTransferService;
import com.upmudoum.erp.domain.item.vo.ItemType;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/erp/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventoryTransferService transferService;

    public InventoryController(InventoryService inventoryService, InventoryTransferService transferService) {
        this.inventoryService = inventoryService;
        this.transferService = transferService;
    }

    @PostMapping("/adjustments")
    public ApiResponse<InventoryMovementResponse> adjust(@Valid @RequestBody InventoryAdjustmentRequest request) {
        return ApiResponse.ok(inventoryService.adjust(request));
    }

    @GetMapping("/movements")
    public ApiResponse<List<InventoryMovementResponse>> findMovements(@RequestParam Long itemId, @RequestParam Long warehouseId) {
        return ApiResponse.ok(inventoryService.findMovements(itemId, warehouseId));
    }

    @GetMapping("/balances")
    public ApiResponse<InventoryBalanceResponse> findBalance(@RequestParam Long itemId, @RequestParam Long warehouseId) {
        return ApiResponse.ok(inventoryService.findBalance(itemId, warehouseId));
    }

    @GetMapping("/balances/search")
    public ApiResponse<List<InventoryBalanceResponse>> searchBalances(@RequestParam(required = false) Long warehouseId,
                                                                      @RequestParam(required = false) ItemType itemType,
                                                                      @RequestParam(required = false) Boolean positiveOnly,
                                                                      @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(inventoryService.searchBalances(warehouseId, itemType, positiveOnly, keyword));
    }

    @PostMapping("/transfers")
    public ApiResponse<InventoryTransferResponse> transfer(@Valid @RequestBody InventoryTransferRequest request) {
        return ApiResponse.ok(transferService.transfer(request));
    }

    @GetMapping("/transfers")
    public ApiResponse<List<InventoryTransferResponse>> findTransfers(@RequestParam Long itemId) {
        return ApiResponse.ok(transferService.findTransfers(itemId));
    }
}
