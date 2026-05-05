package com.upmudoum.erp.domain.purchase.service;

import com.upmudoum.erp.common.exception.BusinessException;
import com.upmudoum.erp.domain.cost.service.ItemCostService;
import com.upmudoum.erp.domain.cost.vo.ActualCostReferenceType;
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
import com.upmudoum.erp.domain.purchase.dto.PurchaseReceiptItemResponse;
import com.upmudoum.erp.domain.purchase.entity.PurchaseReceipt;
import com.upmudoum.erp.domain.purchase.entity.PurchaseReceiptItem;
import com.upmudoum.erp.domain.purchase.repository.PurchaseReceiptItemRepository;
import com.upmudoum.erp.domain.purchase.repository.PurchaseReceiptRepository;
import com.upmudoum.erp.domain.warehouse.entity.Warehouse;
import com.upmudoum.erp.domain.warehouse.repository.WarehouseRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PurchaseReceiptService {

    private final PurchaseReceiptRepository receiptRepository;
    private final PurchaseReceiptItemRepository receiptItemRepository;
    private final PartnerRepository partnerRepository;
    private final ItemRepository itemRepository;
    private final WarehouseRepository warehouseRepository;
    private final LotRepository lotRepository;
    private final InventoryLotService inventoryLotService;
    private final ItemCostService itemCostService;

    public PurchaseReceiptService(PurchaseReceiptRepository receiptRepository,
                                  PurchaseReceiptItemRepository receiptItemRepository,
                                  PartnerRepository partnerRepository, ItemRepository itemRepository,
                                  WarehouseRepository warehouseRepository, LotRepository lotRepository,
                                  InventoryLotService inventoryLotService, ItemCostService itemCostService) {
        this.receiptRepository = receiptRepository;
        this.receiptItemRepository = receiptItemRepository;
        this.partnerRepository = partnerRepository;
        this.itemRepository = itemRepository;
        this.warehouseRepository = warehouseRepository;
        this.lotRepository = lotRepository;
        this.inventoryLotService = inventoryLotService;
        this.itemCostService = itemCostService;
    }

    @Transactional
    public PurchaseReceiptItem receive(Long partnerId, Long itemId, Long warehouseId, String lotNo,
                                       BigDecimal quantity, BigDecimal unitPrice, LocalDate purchaseDate) {
        BigDecimal supplyAmount = quantity.multiply(unitPrice);
        Partner partner = partnerRepository.findById(partnerId).orElseThrow(() -> new BusinessException("Partner not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new BusinessException("Item not found"));
        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow(() -> new BusinessException("Warehouse not found"));

        PurchaseReceipt receipt = receiptRepository.save(new PurchaseReceipt(partner, purchaseDate, supplyAmount));
        InventoryMovement movement = inventoryLotService.receive(itemId, warehouseId, lotNo, quantity, unitPrice, supplyAmount,
                InventoryMovementType.RECEIPT, InventoryReferenceType.PURCHASE, receipt.getId(),
                "purchase receipt id=" + receipt.getId());
        Lot lot = lotNo == null || lotNo.isBlank() ? null : lotRepository.findByItemIdAndLotNo(itemId, lotNo).orElse(null);
        PurchaseReceiptItem receiptItem = receiptItemRepository.save(new PurchaseReceiptItem(
                receipt, item, warehouse, lot, quantity, unitPrice, supplyAmount, movement));
        itemCostService.recordActualCost(itemId, ActualCostReferenceType.PURCHASE, receiptItem.getId(), unitPrice, quantity);
        return receiptItem;
    }

    public List<PurchaseReceiptItemResponse> findReceiptItems(Long itemId) {
        return receiptItemRepository.findByItemIdOrderByIdDesc(itemId).stream()
                .map(PurchaseReceiptItemResponse::from)
                .toList();
    }
}
