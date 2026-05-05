package com.upmudoum.erp.domain.lot.controller;

import com.upmudoum.erp.common.response.ApiResponse;
import com.upmudoum.erp.domain.lot.dto.LotBalanceResponse;
import com.upmudoum.erp.domain.lot.dto.LotMovementResponse;
import com.upmudoum.erp.domain.lot.dto.LotResponse;
import com.upmudoum.erp.domain.lot.service.LotQueryService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/erp/lots")
public class LotController {

    private final LotQueryService lotQueryService;

    public LotController(LotQueryService lotQueryService) {
        this.lotQueryService = lotQueryService;
    }

    @GetMapping
    public ApiResponse<List<LotResponse>> findLots(@RequestParam Long itemId) {
        return ApiResponse.ok(lotQueryService.findLots(itemId));
    }

    @GetMapping("/balances")
    public ApiResponse<List<LotBalanceResponse>> findBalances(@RequestParam Long itemId, @RequestParam Long warehouseId) {
        return ApiResponse.ok(lotQueryService.findBalances(itemId, warehouseId));
    }

    @GetMapping("/balances/search")
    public ApiResponse<List<LotBalanceResponse>> searchBalances(@RequestParam(required = false) Long itemId,
                                                                @RequestParam(required = false) Long warehouseId,
                                                                @RequestParam(required = false) String lotNo,
                                                                @RequestParam(required = false) Boolean positiveOnly) {
        return ApiResponse.ok(lotQueryService.searchBalances(itemId, warehouseId, lotNo, positiveOnly));
    }

    @GetMapping("/movement-lots")
    public ApiResponse<List<LotMovementResponse>> findMovementLots(@RequestParam Long movementId) {
        return ApiResponse.ok(lotQueryService.findMovementLots(movementId));
    }
}
