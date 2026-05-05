package com.upmudoum.erp.domain.sales.service;

import com.upmudoum.erp.common.exception.BusinessException;
import com.upmudoum.erp.domain.cost.service.ItemCostService;
import com.upmudoum.erp.domain.cost.vo.ActualCostReferenceType;
import com.upmudoum.erp.domain.inventory.dto.LotDeductionRequest;
import com.upmudoum.erp.domain.inventory.entity.InventoryMovement;
import com.upmudoum.erp.domain.inventory.service.InventoryLotService;
import com.upmudoum.erp.domain.inventory.vo.InventoryMovementType;
import com.upmudoum.erp.domain.inventory.vo.InventoryReferenceType;
import com.upmudoum.erp.domain.item.entity.Item;
import com.upmudoum.erp.domain.item.repository.ItemRepository;
import com.upmudoum.erp.domain.lot.entity.Lot;
import com.upmudoum.erp.domain.lot.repository.LotRepository;
import com.upmudoum.erp.domain.partner.entity.Partner;
import com.upmudoum.erp.domain.partner.repository.PartnerRepository;
import com.upmudoum.erp.domain.sales.entity.SalesShipment;
import com.upmudoum.erp.domain.sales.entity.SalesShipmentItem;
import com.upmudoum.erp.domain.sales.dto.SalesShipmentItemResponse;
import com.upmudoum.erp.domain.sales.repository.SalesShipmentItemRepository;
import com.upmudoum.erp.domain.sales.repository.SalesShipmentRepository;
import com.upmudoum.erp.domain.warehouse.entity.Warehouse;
import com.upmudoum.erp.domain.warehouse.repository.WarehouseRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SalesShipmentService {

    private final SalesShipmentRepository shipmentRepository;
    private final SalesShipmentItemRepository shipmentItemRepository;
    private final PartnerRepository partnerRepository;
    private final ItemRepository itemRepository;
    private final WarehouseRepository warehouseRepository;
    private final LotRepository lotRepository;
    private final InventoryLotService inventoryLotService;
    private final ItemCostService itemCostService;

    public SalesShipmentService(SalesShipmentRepository shipmentRepository, SalesShipmentItemRepository shipmentItemRepository,
                                PartnerRepository partnerRepository, ItemRepository itemRepository,
                                WarehouseRepository warehouseRepository, LotRepository lotRepository,
                                InventoryLotService inventoryLotService, ItemCostService itemCostService) {
        this.shipmentRepository = shipmentRepository;
        this.shipmentItemRepository = shipmentItemRepository;
        this.partnerRepository = partnerRepository;
        this.itemRepository = itemRepository;
        this.warehouseRepository = warehouseRepository;
        this.lotRepository = lotRepository;
        this.inventoryLotService = inventoryLotService;
        this.itemCostService = itemCostService;
    }

    @Transactional
    public SalesShipmentItem ship(Long partnerId, Long itemId, Long warehouseId, BigDecimal quantity,
                                  BigDecimal unitPrice, LocalDate salesDate, List<LotDeductionRequest> lotSelections) {
        BigDecimal supplyAmount = quantity.multiply(unitPrice);
        Partner partner = partnerRepository.findById(partnerId).orElseThrow(() -> new BusinessException("Partner not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new BusinessException("Item not found"));
        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow(() -> new BusinessException("Warehouse not found"));

        SalesShipment shipment = shipmentRepository.save(new SalesShipment(partner, salesDate, supplyAmount));
        InventoryMovement movement = inventoryLotService.issue(itemId, warehouseId, quantity, unitPrice, supplyAmount,
                InventoryMovementType.ISSUE, InventoryReferenceType.SALE, shipment.getId(), lotSelections,
                "sales shipment id=" + shipment.getId());
        Lot lot = firstSelectedLot(itemId, lotSelections);
        SalesShipmentItem shipmentItem = shipmentItemRepository.save(new SalesShipmentItem(
                shipment, item, warehouse, lot, quantity, unitPrice, supplyAmount, movement));
        itemCostService.recordActualCost(itemId, ActualCostReferenceType.SALE, shipmentItem.getId(), unitPrice, quantity);
        return shipmentItem;
    }

    public List<SalesShipmentItemResponse> findShipmentItems(Long itemId) {
        return shipmentItemRepository.findByItemIdOrderByIdDesc(itemId).stream()
                .map(SalesShipmentItemResponse::from)
                .toList();
    }

    private Lot firstSelectedLot(Long itemId, List<LotDeductionRequest> lotSelections) {
        if (lotSelections == null || lotSelections.isEmpty()) {
            return null;
        }
        Long lotId = lotSelections.getFirst().getLotId();
        return lotId == null ? null : lotRepository.findById(lotId)
                .filter(lot -> lot.getItem().getId().equals(itemId))
                .orElse(null);
    }
}
