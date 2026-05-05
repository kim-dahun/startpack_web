package com.upmudoum.erp.domain.production.service;

import com.upmudoum.erp.common.exception.BusinessException;
import com.upmudoum.erp.domain.bom.entity.BomComponent;
import com.upmudoum.erp.domain.bom.entity.BomVersion;
import com.upmudoum.erp.domain.bom.repository.BomComponentRepository;
import com.upmudoum.erp.domain.bom.repository.BomVersionRepository;
import com.upmudoum.erp.domain.equipment.entity.Equipment;
import com.upmudoum.erp.domain.equipment.repository.EquipmentRepository;
import com.upmudoum.erp.domain.inventory.service.InventoryLotService;
import com.upmudoum.erp.domain.inventory.vo.InventoryMovementType;
import com.upmudoum.erp.domain.inventory.vo.InventoryReferenceType;
import com.upmudoum.erp.domain.inventory.repository.InventoryMovementLotRepository;
import com.upmudoum.erp.domain.inventory.repository.InventoryMovementRepository;
import com.upmudoum.erp.domain.item.entity.Item;
import com.upmudoum.erp.domain.item.repository.ItemRepository;
import com.upmudoum.erp.domain.process.entity.ErpProcess;
import com.upmudoum.erp.domain.process.repository.ErpProcessRepository;
import com.upmudoum.erp.domain.production.dto.ProductionConsumptionAdjustmentRequest;
import com.upmudoum.erp.domain.production.dto.ProductionConsumptionResponse;
import com.upmudoum.erp.domain.production.dto.ProductionOrderResponse;
import com.upmudoum.erp.domain.production.dto.ProductionOrderStepRequest;
import com.upmudoum.erp.domain.production.dto.ProductionOrderStepResponse;
import com.upmudoum.erp.domain.production.dto.ProductionResultResponse;
import com.upmudoum.erp.domain.production.dto.ProductionResultStepResponse;
import com.upmudoum.erp.domain.production.entity.ProductionConsumption;
import com.upmudoum.erp.domain.production.entity.ProductionOrder;
import com.upmudoum.erp.domain.production.entity.ProductionOrderStep;
import com.upmudoum.erp.domain.production.entity.ProductionResult;
import com.upmudoum.erp.domain.production.entity.ProductionResultStep;
import com.upmudoum.erp.domain.production.querydsl.ProductionQueryRepository;
import com.upmudoum.erp.domain.production.repository.ProductionConsumptionRepository;
import com.upmudoum.erp.domain.production.repository.ProductionOrderRepository;
import com.upmudoum.erp.domain.production.repository.ProductionOrderStepRepository;
import com.upmudoum.erp.domain.production.repository.ProductionResultRepository;
import com.upmudoum.erp.domain.production.repository.ProductionResultStepRepository;
import com.upmudoum.erp.domain.production.vo.ProductionConsumptionAdjustType;
import com.upmudoum.erp.domain.production.vo.ProductionOrderStatus;
import com.upmudoum.erp.domain.production.vo.ProductionResultStatus;
import com.upmudoum.erp.domain.route.entity.Route;
import com.upmudoum.erp.domain.route.entity.RouteStep;
import com.upmudoum.erp.domain.route.repository.RouteRepository;
import com.upmudoum.erp.domain.route.repository.RouteStepRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductionPlanService {

    private final ProductionOrderRepository orderRepository;
    private final ProductionResultRepository resultRepository;
    private final ProductionOrderStepRepository orderStepRepository;
    private final ProductionResultStepRepository resultStepRepository;
    private final ProductionConsumptionRepository consumptionRepository;
    private final ItemRepository itemRepository;
    private final BomVersionRepository bomVersionRepository;
    private final BomComponentRepository bomComponentRepository;
    private final InventoryLotService inventoryLotService;
    private final InventoryMovementRepository movementRepository;
    private final InventoryMovementLotRepository movementLotRepository;
    private final RouteRepository routeRepository;
    private final RouteStepRepository routeStepRepository;
    private final ErpProcessRepository processRepository;
    private final EquipmentRepository equipmentRepository;
    private final ProductionQueryRepository productionQueryRepository;

    public ProductionPlanService(ProductionOrderRepository orderRepository, ProductionResultRepository resultRepository,
                                 ProductionOrderStepRepository orderStepRepository,
                                 ProductionResultStepRepository resultStepRepository,
                                 ProductionConsumptionRepository consumptionRepository, ItemRepository itemRepository,
                                 BomVersionRepository bomVersionRepository, BomComponentRepository bomComponentRepository,
                                 InventoryLotService inventoryLotService, InventoryMovementRepository movementRepository,
                                 InventoryMovementLotRepository movementLotRepository, RouteRepository routeRepository,
                                 RouteStepRepository routeStepRepository, ErpProcessRepository processRepository,
                                 EquipmentRepository equipmentRepository,
                                 ProductionQueryRepository productionQueryRepository) {
        this.orderRepository = orderRepository;
        this.resultRepository = resultRepository;
        this.orderStepRepository = orderStepRepository;
        this.resultStepRepository = resultStepRepository;
        this.consumptionRepository = consumptionRepository;
        this.itemRepository = itemRepository;
        this.bomVersionRepository = bomVersionRepository;
        this.bomComponentRepository = bomComponentRepository;
        this.inventoryLotService = inventoryLotService;
        this.movementRepository = movementRepository;
        this.movementLotRepository = movementLotRepository;
        this.routeRepository = routeRepository;
        this.routeStepRepository = routeStepRepository;
        this.processRepository = processRepository;
        this.equipmentRepository = equipmentRepository;
        this.productionQueryRepository = productionQueryRepository;
    }

    @Transactional
    public ProductionOrder createOrder(String orderNo, Long itemId, Long bomVersionId, BigDecimal plannedQuantity,
                                       LocalDate dueDate, Long routeId, Long plannedProcessId, Long plannedEquipmentId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new BusinessException("Production item not found"));
        if (!item.getItemType().isProducible()) {
            throw new BusinessException("Only semi-finished or finished goods can be produced");
        }
        BomVersion bomVersion = bomVersionRepository.findById(bomVersionId)
                .orElseThrow(() -> new BusinessException("BOM version not found"));
        if (!bomVersion.getBom().getParentItem().getId().equals(item.getId())) {
            throw new BusinessException("BOM version does not match production item");
        }
        Route route = resolveRoute(routeId);
        ErpProcess plannedProcess = resolveProcess(plannedProcessId);
        Equipment plannedEquipment = resolveEquipment(plannedEquipmentId);
        ProductionOrder order = orderRepository.save(new ProductionOrder(orderNo, item, bomVersion, route,
                plannedProcess, plannedEquipment, plannedQuantity, dueDate));
        createOrderSteps(order, route);
        return order;
    }

    @Transactional
    public ProductionOrder createOrder(String orderNo, Long itemId, Long bomVersionId, BigDecimal plannedQuantity,
                                       LocalDate dueDate) {
        return createOrder(orderNo, itemId, bomVersionId, plannedQuantity, dueDate, null, null, null);
    }

    @Transactional
    public ProductionResult recordResult(Long productionOrderId, Long warehouseId, BigDecimal goodQuantity,
                                         BigDecimal defectQuantity, String finishedLotNo, Long routeId,
                                         Long routeStepId, Long productionOrderStepId, Long processId, Long equipmentId,
                                         LocalDateTime workStartedAt, LocalDateTime workEndedAt,
                                         List<ProductionConsumptionAdjustmentRequest> adjustments) {
        ProductionOrder order = orderRepository.findById(productionOrderId)
                .orElseThrow(() -> new BusinessException("Production order not found"));
        Route route = resolveRoute(routeId == null && order.getRoute() != null ? order.getRoute().getId() : routeId);
        RouteStep routeStep = resolveRouteStep(routeStepId);
        ProductionOrderStep orderStep = resolveOrderStep(productionOrderStepId);
        ErpProcess process = resolveProcess(processId);
        Equipment equipment = resolveEquipment(equipmentId);
        if (orderStep != null) {
            process = process == null ? orderStep.getProcess() : process;
            equipment = equipment == null ? orderStep.getPlannedEquipment() : equipment;
            routeStep = routeStep == null ? orderStep.getRouteStep() : routeStep;
        }
        if (process == null) {
            process = order.getPlannedProcess();
        }
        if (equipment == null) {
            equipment = order.getPlannedEquipment();
        }
        ProductionResult result = resultRepository.save(new ProductionResult(
                order, route, routeStep, process, equipment, goodQuantity, defectQuantity,
                LocalDateTime.now(), workStartedAt, workEndedAt));
        if (process != null) {
            resultStepRepository.save(new ProductionResultStep(result, orderStep,
                    orderStep == null ? 1 : orderStep.getSequenceNo(), process, equipment, workStartedAt, workEndedAt));
        }

        List<ProductionConsumption> consumptions = buildConsumptions(order, result, goodQuantity,
                adjustments == null ? List.of() : adjustments);
        consumptionRepository.saveAll(consumptions);

        for (ProductionConsumption consumption : consumptions) {
            if (consumption.getActualQuantity().isPositive()) {
                ProductionConsumptionAdjustmentRequest adjustment = findAdjustment(adjustments, consumption.getItem().getId()).orElse(null);
                inventoryLotService.issue(consumption.getItem().getId(), warehouseId,
                        consumption.getActualQuantity().getValue(), BigDecimal.ZERO, BigDecimal.ZERO,
                        InventoryMovementType.PRODUCTION_CONSUMPTION, InventoryReferenceType.PRODUCTION, result.getId(),
                        adjustment == null ? List.of() : adjustment.getLotSelections(),
                        "production consumption order=" + order.getOrderNo());
            }
        }

        inventoryLotService.receive(order.getItem().getId(), warehouseId, finishedLotNo, goodQuantity,
                BigDecimal.ZERO, BigDecimal.ZERO, InventoryMovementType.PRODUCTION_RECEIPT,
                InventoryReferenceType.PRODUCTION, result.getId(), "production receipt order=" + order.getOrderNo());
        order.complete();
        return result;
    }

    @Transactional
    public ProductionResult recordResult(Long productionOrderId, Long warehouseId, BigDecimal goodQuantity,
                                         BigDecimal defectQuantity, String finishedLotNo,
                                         List<ProductionConsumptionAdjustmentRequest> adjustments) {
        return recordResult(productionOrderId, warehouseId, goodQuantity, defectQuantity, finishedLotNo,
                null, null, null, null, null, null, null, adjustments);
    }

    public List<ProductionOrderResponse> findOrders() {
        return orderRepository.findAllByOrderByDueDateDescIdDesc().stream()
                .map(ProductionOrderResponse::from)
                .toList();
    }

    public List<ProductionOrderResponse> searchOrders(Long itemId, Long routeId, Long processId, Long equipmentId,
                                                      ProductionOrderStatus status, LocalDate dueFrom, LocalDate dueTo,
                                                      String keyword) {
        return productionQueryRepository.searchOrders(itemId, routeId, processId, equipmentId, status,
                        dueFrom, dueTo, keyword).stream()
                .map(ProductionOrderResponse::from)
                .toList();
    }

    public List<ProductionResultResponse> findResults(Long productionOrderId) {
        return resultRepository.findByProductionOrderIdOrderByCompletedAtDescIdDesc(productionOrderId).stream()
                .map(ProductionResultResponse::from)
                .toList();
    }

    public List<ProductionResultResponse> searchResults(Long productionOrderId, Long itemId, Long routeId,
                                                        Long processId, Long equipmentId,
                                                        ProductionResultStatus status,
                                                        LocalDateTime completedFrom, LocalDateTime completedTo,
                                                        String keyword) {
        return productionQueryRepository.searchResults(productionOrderId, itemId, routeId, processId, equipmentId,
                        status, completedFrom, completedTo, keyword).stream()
                .map(ProductionResultResponse::from)
                .toList();
    }

    public List<ProductionConsumptionResponse> findConsumptions(Long productionResultId) {
        return consumptionRepository.findByProductionResultId(productionResultId).stream()
                .map(ProductionConsumptionResponse::from)
                .toList();
    }

    public List<ProductionOrderStepResponse> findOrderSteps(Long productionOrderId) {
        return orderStepRepository.findByProductionOrderIdOrderBySequenceNoAscIdAsc(productionOrderId).stream()
                .map(ProductionOrderStepResponse::from)
                .toList();
    }

    @Transactional
    public ProductionOrderStepResponse updateOrderStep(Long productionOrderId, Long productionOrderStepId,
                                                       ProductionOrderStepRequest request) {
        ProductionOrderStep step = orderStepRepository.findById(productionOrderStepId)
                .orElseThrow(() -> new BusinessException("Production order step not found"));
        if (!step.getProductionOrder().getId().equals(productionOrderId)) {
            throw new BusinessException("Production order step does not belong to order");
        }
        step.update(resolveEquipment(request.getPlannedEquipmentId()), request.getStatus(),
                request.getPlannedStartAt(), request.getPlannedEndAt());
        return ProductionOrderStepResponse.from(step);
    }

    public List<ProductionResultStepResponse> findResultSteps(Long productionResultId) {
        return resultStepRepository.findByProductionResultIdOrderBySequenceNoAscIdAsc(productionResultId).stream()
                .map(ProductionResultStepResponse::from)
                .toList();
    }

    @Transactional
    public ProductionResultResponse cancelResult(Long productionResultId, Long warehouseId,
                                                 List<com.upmudoum.erp.domain.inventory.dto.LotDeductionRequest> finishedLotSelections,
                                                 String memo) {
        ProductionResult result = resultRepository.findById(productionResultId)
                .orElseThrow(() -> new BusinessException("Production result not found"));
        if (result.getStatus() == com.upmudoum.erp.domain.production.vo.ProductionResultStatus.CANCELED) {
            throw new BusinessException("Production result already canceled");
        }
        ProductionOrder order = result.getProductionOrder();
        reverseProductionMovements(result, order, warehouseId, finishedLotSelections);
        result.cancel();
        return ProductionResultResponse.from(result);
    }

    private void reverseProductionMovements(ProductionResult result, ProductionOrder order, Long warehouseId,
                                            List<com.upmudoum.erp.domain.inventory.dto.LotDeductionRequest> finishedLotSelections) {
        List<com.upmudoum.erp.domain.inventory.entity.InventoryMovement> movements =
                movementRepository.findByReferenceTypeAndReferenceIdOrderByOccurredAtAscIdAsc(
                        InventoryReferenceType.PRODUCTION, result.getId());
        if (movements.isEmpty()) {
            inventoryLotService.issue(order.getItem().getId(), warehouseId, result.getGoodQuantity().getValue(),
                    InventoryMovementType.PRODUCTION_CONSUMPTION, finishedLotSelections,
                    "production result cancel finished issue result=" + result.getId());
            restoreConsumptionsWithoutOriginalLot(result, warehouseId);
            return;
        }
        for (com.upmudoum.erp.domain.inventory.entity.InventoryMovement movement : movements) {
            if (movement.getMovementType() == InventoryMovementType.PRODUCTION_RECEIPT) {
                List<com.upmudoum.erp.domain.inventory.dto.LotDeductionRequest> selections =
                        finishedLotSelections == null || finishedLotSelections.isEmpty()
                                ? movementLotRepository.findByMovementId(movement.getId()).stream()
                                .filter(movementLot -> movementLot.getLot() != null)
                                .map(movementLot -> new com.upmudoum.erp.domain.inventory.dto.LotDeductionRequest(
                                        movementLot.getLot().getId(), movementLot.getQuantity().getValue()))
                                .toList()
                                : finishedLotSelections;
                inventoryLotService.issue(movement.getItem().getId(), warehouseId,
                        movement.getQuantity().getValue(), BigDecimal.ZERO, BigDecimal.ZERO,
                        InventoryMovementType.PRODUCTION_CONSUMPTION, InventoryReferenceType.PRODUCTION, result.getId(),
                        selections, "production result cancel finished issue result=" + result.getId());
            }
            if (movement.getMovementType() == InventoryMovementType.PRODUCTION_CONSUMPTION) {
                for (com.upmudoum.erp.domain.inventory.entity.InventoryMovementLot movementLot
                        : movementLotRepository.findByMovementId(movement.getId())) {
                    String lotNo = movementLot.getLot() == null ? null : movementLot.getLot().getLotNo();
                    inventoryLotService.receive(movement.getItem().getId(), warehouseId, lotNo,
                            movementLot.getQuantity().getValue().abs(), BigDecimal.ZERO, BigDecimal.ZERO,
                            InventoryMovementType.PRODUCTION_RECEIPT, InventoryReferenceType.PRODUCTION, result.getId(),
                            "production result cancel component restore result=" + result.getId());
                }
            }
        }
    }

    private void restoreConsumptionsWithoutOriginalLot(ProductionResult result, Long warehouseId) {
        for (ProductionConsumption consumption : consumptionRepository.findByProductionResultId(result.getId())) {
            if (consumption.getActualQuantity().isPositive()) {
                inventoryLotService.receive(consumption.getItem().getId(), warehouseId, null,
                        consumption.getActualQuantity().getValue(), InventoryMovementType.PRODUCTION_RECEIPT,
                        "production result cancel component restore result=" + result.getId());
            }
        }
    }

    private List<ProductionConsumption> buildConsumptions(ProductionOrder order, ProductionResult result,
                                                          BigDecimal goodQuantity,
                                                          List<ProductionConsumptionAdjustmentRequest> adjustments) {
        List<ProductionConsumption> consumptions = new ArrayList<>();
        for (BomComponent component : bomComponentRepository.findByBomVersionId(order.getBomVersion().getId())) {
            BigDecimal planned = plannedConsumption(component, goodQuantity);
            Optional<ProductionConsumptionAdjustmentRequest> adjustment = findAdjustment(adjustments, component.getComponentItem().getId());
            BigDecimal actual = adjustment.map(ProductionConsumptionAdjustmentRequest::getActualQuantity).orElse(planned);
            ProductionConsumptionAdjustType adjustType = adjustment
                    .map(ProductionConsumptionAdjustmentRequest::getAdjustType)
                    .orElse(ProductionConsumptionAdjustType.BOM);
            consumptions.add(new ProductionConsumption(result, component.getComponentItem(), planned, actual, adjustType));
        }
        for (ProductionConsumptionAdjustmentRequest adjustment : adjustments) {
            boolean existsInBom = consumptions.stream()
                    .anyMatch(consumption -> consumption.getItem().getId().equals(adjustment.getItemId()));
            if (!existsInBom && adjustment.getAdjustType() == ProductionConsumptionAdjustType.ADDED) {
                Item item = itemRepository.findById(adjustment.getItemId())
                        .orElseThrow(() -> new BusinessException("Added consumption item not found"));
                consumptions.add(new ProductionConsumption(result, item, BigDecimal.ZERO,
                        adjustment.getActualQuantity(), ProductionConsumptionAdjustType.ADDED));
            }
        }
        return consumptions;
    }

    private Optional<ProductionConsumptionAdjustmentRequest> findAdjustment(
            List<ProductionConsumptionAdjustmentRequest> adjustments, Long itemId) {
        if (adjustments == null) {
            return Optional.empty();
        }
        return adjustments.stream()
                .filter(adjustment -> adjustment.getItemId().equals(itemId))
                .findFirst();
    }

    private BigDecimal plannedConsumption(BomComponent component, BigDecimal goodQuantity) {
        return component.getRequiredQuantity().getValue()
                .multiply(goodQuantity)
                .multiply(BigDecimal.ONE.add(component.getLossRate()));
    }

    private void createOrderSteps(ProductionOrder order, Route route) {
        if (route == null) {
            return;
        }
        List<ProductionOrderStep> steps = routeStepRepository
                .findByRouteIdAndEnabledTrueOrderBySequenceNoAscIdAsc(route.getId()).stream()
                .map(routeStep -> new ProductionOrderStep(order, routeStep, routeStep.getSequenceNo(),
                        routeStep.getProcess(), routeStep.getDefaultEquipment()))
                .toList();
        orderStepRepository.saveAll(steps);
    }

    private Route resolveRoute(Long routeId) {
        if (routeId == null) {
            return null;
        }
        return routeRepository.findById(routeId).orElseThrow(() -> new BusinessException("Route not found"));
    }

    private RouteStep resolveRouteStep(Long routeStepId) {
        if (routeStepId == null) {
            return null;
        }
        return routeStepRepository.findById(routeStepId).orElseThrow(() -> new BusinessException("Route step not found"));
    }

    private ProductionOrderStep resolveOrderStep(Long orderStepId) {
        if (orderStepId == null) {
            return null;
        }
        return orderStepRepository.findById(orderStepId)
                .orElseThrow(() -> new BusinessException("Production order step not found"));
    }

    private ErpProcess resolveProcess(Long processId) {
        if (processId == null) {
            return null;
        }
        return processRepository.findById(processId).orElseThrow(() -> new BusinessException("Process not found"));
    }

    private Equipment resolveEquipment(Long equipmentId) {
        if (equipmentId == null) {
            return null;
        }
        return equipmentRepository.findById(equipmentId).orElseThrow(() -> new BusinessException("Equipment not found"));
    }
}
