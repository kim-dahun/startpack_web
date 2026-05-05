package com.upmudoum.erp.domain.cost.controller;

import com.upmudoum.erp.common.response.ApiResponse;
import com.upmudoum.erp.domain.cost.dto.ActualCostReversalRequest;
import com.upmudoum.erp.domain.cost.dto.ItemActualCostHistoryResponse;
import com.upmudoum.erp.domain.cost.dto.ItemStandardCostRequest;
import com.upmudoum.erp.domain.cost.dto.ItemStandardCostResponse;
import com.upmudoum.erp.domain.cost.service.ItemCostService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/erp/costs")
public class ItemCostController {

    private final ItemCostService itemCostService;

    public ItemCostController(ItemCostService itemCostService) {
        this.itemCostService = itemCostService;
    }

    @PostMapping("/standard")
    public ApiResponse<ItemStandardCostResponse> registerStandardCost(@Valid @RequestBody ItemStandardCostRequest request) {
        return ApiResponse.ok(ItemStandardCostResponse.from(itemCostService.registerStandardCost(
                request.getItemId(), request.getStandardCost(), request.getCurrencyCode(),
                request.getEffectiveFrom(), request.getEffectiveTo())));
    }

    @GetMapping("/standard")
    public ApiResponse<List<ItemStandardCostResponse>> findStandardCosts(@RequestParam Long itemId) {
        return ApiResponse.ok(itemCostService.findStandardCosts(itemId));
    }

    @GetMapping("/actual-histories")
    public ApiResponse<List<ItemActualCostHistoryResponse>> findActualCostHistories(@RequestParam Long itemId) {
        return ApiResponse.ok(itemCostService.findActualCostHistories(itemId));
    }

    @PostMapping("/actual-histories/reversals")
    public ApiResponse<List<ItemActualCostHistoryResponse>> reverseActualCost(
            @Valid @RequestBody ActualCostReversalRequest request) {
        return ApiResponse.ok(itemCostService.reverseActualCost(
                request.getOriginalReferenceType(), request.getOriginalReferenceId()));
    }
}
