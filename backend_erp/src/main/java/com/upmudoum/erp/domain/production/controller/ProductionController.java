package com.upmudoum.erp.domain.production.controller;

import com.upmudoum.erp.common.response.ApiResponse;
import com.upmudoum.erp.domain.production.dto.ProductionConsumptionResponse;
import com.upmudoum.erp.domain.production.dto.ProductionOrderRequest;
import com.upmudoum.erp.domain.production.dto.ProductionOrderResponse;
import com.upmudoum.erp.domain.production.dto.ProductionOrderStepRequest;
import com.upmudoum.erp.domain.production.dto.ProductionOrderStepResponse;
import com.upmudoum.erp.domain.production.dto.ProductionResultCancelRequest;
import com.upmudoum.erp.domain.production.dto.ProductionResultRequest;
import com.upmudoum.erp.domain.production.dto.ProductionResultResponse;
import com.upmudoum.erp.domain.production.dto.ProductionResultStepResponse;
import com.upmudoum.erp.domain.production.service.ProductionPlanService;
import com.upmudoum.erp.domain.production.vo.ProductionOrderStatus;
import com.upmudoum.erp.domain.production.vo.ProductionResultStatus;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/erp/production")
public class ProductionController {

    private final ProductionPlanService productionPlanService;

    public ProductionController(ProductionPlanService productionPlanService) {
        this.productionPlanService = productionPlanService;
    }

    @PostMapping("/orders")
    public ApiResponse<ProductionOrderResponse> createOrder(@Valid @RequestBody ProductionOrderRequest request) {
        return ApiResponse.ok(ProductionOrderResponse.from(productionPlanService.createOrder(
                request.getOrderNo(), request.getItemId(), request.getBomVersionId(),
                request.getPlannedQuantity(), request.getDueDate(), request.getRouteId(),
                request.getPlannedProcessId(), request.getPlannedEquipmentId())));
    }

    @GetMapping("/orders")
    public ApiResponse<List<ProductionOrderResponse>> findOrders() {
        return ApiResponse.ok(productionPlanService.findOrders());
    }

    @GetMapping("/orders/search")
    public ApiResponse<List<ProductionOrderResponse>> searchOrders(@RequestParam(required = false) Long itemId,
                                                                   @RequestParam(required = false) Long routeId,
                                                                   @RequestParam(required = false) Long processId,
                                                                   @RequestParam(required = false) Long equipmentId,
                                                                   @RequestParam(required = false) ProductionOrderStatus status,
                                                                   @RequestParam(required = false) LocalDate dueFrom,
                                                                   @RequestParam(required = false) LocalDate dueTo,
                                                                   @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(productionPlanService.searchOrders(itemId, routeId, processId, equipmentId, status,
                dueFrom, dueTo, keyword));
    }

    @PostMapping("/orders/{productionOrderId}/results")
    public ApiResponse<ProductionResultResponse> recordResult(@PathVariable Long productionOrderId,
                                                              @Valid @RequestBody ProductionResultRequest request) {
        return ApiResponse.ok(ProductionResultResponse.from(productionPlanService.recordResult(
                productionOrderId, request.getWarehouseId(), request.getGoodQuantity(), request.getDefectQuantity(),
                request.getFinishedLotNo(), request.getRouteId(), request.getRouteStepId(),
                request.getProductionOrderStepId(), request.getProcessId(), request.getEquipmentId(),
                request.getWorkStartedAt(), request.getWorkEndedAt(), request.getAdjustments())));
    }

    @GetMapping("/orders/{productionOrderId}/results")
    public ApiResponse<List<ProductionResultResponse>> findResults(@PathVariable Long productionOrderId) {
        return ApiResponse.ok(productionPlanService.findResults(productionOrderId));
    }

    @GetMapping("/results/search")
    public ApiResponse<List<ProductionResultResponse>> searchResults(
            @RequestParam(required = false) Long productionOrderId,
            @RequestParam(required = false) Long itemId,
            @RequestParam(required = false) Long routeId,
            @RequestParam(required = false) Long processId,
            @RequestParam(required = false) Long equipmentId,
            @RequestParam(required = false) ProductionResultStatus status,
            @RequestParam(required = false) LocalDateTime completedFrom,
            @RequestParam(required = false) LocalDateTime completedTo,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(productionPlanService.searchResults(productionOrderId, itemId, routeId, processId,
                equipmentId, status, completedFrom, completedTo, keyword));
    }

    @GetMapping("/results/{productionResultId}/consumptions")
    public ApiResponse<List<ProductionConsumptionResponse>> findConsumptions(@PathVariable Long productionResultId) {
        return ApiResponse.ok(productionPlanService.findConsumptions(productionResultId));
    }

    @GetMapping("/orders/{productionOrderId}/steps")
    public ApiResponse<List<ProductionOrderStepResponse>> findOrderSteps(@PathVariable Long productionOrderId) {
        return ApiResponse.ok(productionPlanService.findOrderSteps(productionOrderId));
    }

    @PutMapping("/orders/{productionOrderId}/steps/{productionOrderStepId}")
    public ApiResponse<ProductionOrderStepResponse> updateOrderStep(@PathVariable Long productionOrderId,
                                                                    @PathVariable Long productionOrderStepId,
                                                                    @RequestBody ProductionOrderStepRequest request) {
        return ApiResponse.ok(productionPlanService.updateOrderStep(productionOrderId, productionOrderStepId, request));
    }

    @GetMapping("/results/{productionResultId}/steps")
    public ApiResponse<List<ProductionResultStepResponse>> findResultSteps(@PathVariable Long productionResultId) {
        return ApiResponse.ok(productionPlanService.findResultSteps(productionResultId));
    }

    @PostMapping("/results/{productionResultId}/cancel")
    public ApiResponse<ProductionResultResponse> cancelResult(@PathVariable Long productionResultId,
                                                              @Valid @RequestBody ProductionResultCancelRequest request) {
        return ApiResponse.ok(productionPlanService.cancelResult(productionResultId, request.getWarehouseId(),
                request.getFinishedLotSelections(), request.getMemo()));
    }
}
