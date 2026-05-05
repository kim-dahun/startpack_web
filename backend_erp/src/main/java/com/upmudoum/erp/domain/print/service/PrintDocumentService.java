package com.upmudoum.erp.domain.print.service;

import com.upmudoum.erp.common.exception.BusinessException;
import com.upmudoum.erp.domain.inventory.entity.InventoryMovement;
import com.upmudoum.erp.domain.inventory.entity.InventoryMovementLot;
import com.upmudoum.erp.domain.inventory.repository.InventoryMovementLotRepository;
import com.upmudoum.erp.domain.inventory.repository.InventoryMovementRepository;
import com.upmudoum.erp.domain.print.dto.PrintBarcodeRequest;
import com.upmudoum.erp.domain.print.dto.PrintBarcodeResponse;
import com.upmudoum.erp.domain.print.dto.PrintDocumentResponse;
import com.upmudoum.erp.domain.print.entity.PrintBarcode;
import com.upmudoum.erp.domain.print.repository.PrintBarcodeRepository;
import com.upmudoum.erp.domain.print.vo.PrintDocumentType;
import com.upmudoum.erp.domain.production.entity.ProductionOrder;
import com.upmudoum.erp.domain.production.entity.ProductionOrderStep;
import com.upmudoum.erp.domain.production.repository.ProductionOrderRepository;
import com.upmudoum.erp.domain.production.repository.ProductionOrderStepRepository;
import com.upmudoum.erp.domain.purchase.entity.PurchaseReceipt;
import com.upmudoum.erp.domain.purchase.entity.PurchaseReceiptItem;
import com.upmudoum.erp.domain.purchase.repository.PurchaseReceiptItemRepository;
import com.upmudoum.erp.domain.purchase.repository.PurchaseReceiptRepository;
import com.upmudoum.erp.domain.sales.entity.SalesShipment;
import com.upmudoum.erp.domain.sales.entity.SalesShipmentItem;
import com.upmudoum.erp.domain.sales.repository.SalesShipmentItemRepository;
import com.upmudoum.erp.domain.sales.repository.SalesShipmentRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PrintDocumentService {

    private final PrintBarcodeRepository printBarcodeRepository;
    private final ProductionOrderRepository productionOrderRepository;
    private final ProductionOrderStepRepository productionOrderStepRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final InventoryMovementLotRepository inventoryMovementLotRepository;
    private final PurchaseReceiptRepository purchaseReceiptRepository;
    private final PurchaseReceiptItemRepository purchaseReceiptItemRepository;
    private final SalesShipmentRepository salesShipmentRepository;
    private final SalesShipmentItemRepository salesShipmentItemRepository;

    public PrintDocumentService(PrintBarcodeRepository printBarcodeRepository,
                                ProductionOrderRepository productionOrderRepository,
                                ProductionOrderStepRepository productionOrderStepRepository,
                                InventoryMovementRepository inventoryMovementRepository,
                                InventoryMovementLotRepository inventoryMovementLotRepository,
                                PurchaseReceiptRepository purchaseReceiptRepository,
                                PurchaseReceiptItemRepository purchaseReceiptItemRepository,
                                SalesShipmentRepository salesShipmentRepository,
                                SalesShipmentItemRepository salesShipmentItemRepository) {
        this.printBarcodeRepository = printBarcodeRepository;
        this.productionOrderRepository = productionOrderRepository;
        this.productionOrderStepRepository = productionOrderStepRepository;
        this.inventoryMovementRepository = inventoryMovementRepository;
        this.inventoryMovementLotRepository = inventoryMovementLotRepository;
        this.purchaseReceiptRepository = purchaseReceiptRepository;
        this.purchaseReceiptItemRepository = purchaseReceiptItemRepository;
        this.salesShipmentRepository = salesShipmentRepository;
        this.salesShipmentItemRepository = salesShipmentItemRepository;
    }

    @Transactional
    public PrintBarcodeResponse createBarcode(PrintBarcodeRequest request) {
        if (printBarcodeRepository.existsByBarcodeValueAndEnabledTrue(request.getBarcodeValue())) {
            throw new BusinessException("Print barcode already exists");
        }
        PrintBarcode barcode = new PrintBarcode(request.getBarcodeValue(), request.getDocumentType(), request.getDocumentKey());
        barcode.update(request.getBarcodeValue(), request.getDocumentType(), request.getDocumentKey(), request.isEnabled());
        return PrintBarcodeResponse.from(printBarcodeRepository.save(barcode));
    }

    @Transactional
    public PrintBarcodeResponse updateBarcode(Long id, PrintBarcodeRequest request) {
        PrintBarcode barcode = printBarcodeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Print barcode not found"));
        if (!barcode.getBarcodeValue().equals(request.getBarcodeValue())) {
            printBarcodeRepository.findByBarcodeValue(request.getBarcodeValue())
                    .ifPresent(duplicated -> {
                        throw new BusinessException("Print barcode already exists");
                    });
        }
        barcode.update(request.getBarcodeValue(), request.getDocumentType(), request.getDocumentKey(), request.isEnabled());
        return PrintBarcodeResponse.from(barcode);
    }

    @Transactional
    public void disableBarcode(Long id) {
        PrintBarcode barcode = printBarcodeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Print barcode not found"));
        barcode.disable();
    }

    public List<PrintBarcodeResponse> findBarcodes(PrintDocumentType documentType) {
        List<PrintBarcode> barcodes = documentType == null
                ? printBarcodeRepository.findByEnabledTrueOrderByIdDesc()
                : printBarcodeRepository.findByDocumentTypeAndEnabledTrueOrderByIdDesc(documentType);
        return barcodes.stream()
                .map(PrintBarcodeResponse::from)
                .toList();
    }

    public PrintDocumentResponse findByDocumentKey(PrintDocumentType type, String documentKey) {
        String barcodeValue = printBarcodeRepository.findByDocumentTypeAndDocumentKeyAndEnabledTrue(type, documentKey)
                .map(PrintBarcode::getBarcodeValue)
                .orElse(null);
        return build(type, documentKey, barcodeValue);
    }

    public PrintDocumentResponse findByBarcode(String barcodeValue) {
        PrintBarcode barcode = printBarcodeRepository.findByBarcodeValueAndEnabledTrue(barcodeValue)
                .orElseThrow(() -> new BusinessException("Print barcode not found"));
        return build(barcode.getDocumentType(), barcode.getDocumentKey(), barcode.getBarcodeValue());
    }

    private PrintDocumentResponse build(PrintDocumentType type, String documentKey, String barcodeValue) {
        return switch (type) {
            case WORK_INSTRUCTION -> buildWorkInstruction(documentKey, barcodeValue);
            case ISSUE_SLIP -> buildIssueSlip(documentKey, barcodeValue);
            case TRANSACTION_STATEMENT -> buildTransactionStatement(documentKey, barcodeValue);
            case PURCHASE_ORDER -> buildPurchaseDocument(PrintDocumentType.PURCHASE_ORDER, documentKey, barcodeValue,
                    "Purchase Order");
            case GOODS_RECEIPT -> buildPurchaseDocument(PrintDocumentType.GOODS_RECEIPT, documentKey, barcodeValue,
                    "Goods Receipt");
        };
    }

    private PrintDocumentResponse buildWorkInstruction(String documentKey, String barcodeValue) {
        ProductionOrder order = productionOrderRepository.findById(extractId(documentKey))
                .orElseThrow(() -> new BusinessException("Production order not found"));
        List<Map<String, Object>> lines = productionOrderStepRepository
                .findByProductionOrderIdOrderBySequenceNoAscIdAsc(order.getId()).stream()
                .map(this::workInstructionLine)
                .toList();
        return new PrintDocumentResponse(PrintDocumentType.WORK_INSTRUCTION, documentKey, barcodeValue,
                "Work Instruction",
                Map.of(
                        "productionOrderId", order.getId(),
                        "orderNo", order.getOrderNo(),
                        "itemId", order.getItem().getId(),
                        "itemCode", order.getItem().getCode().getValue(),
                        "bomVersionId", order.getBomVersion().getId(),
                        "routeId", value(order.getRoute() == null ? null : order.getRoute().getId()),
                        "routeCode", value(order.getRoute() == null ? null : order.getRoute().getCode()),
                        "plannedQuantity", order.getPlannedQuantity().getValue(),
                        "dueDate", order.getDueDate(),
                        "status", order.getStatus()
                ), lines, Map.of("stepCount", lines.size()));
    }

    private Map<String, Object> workInstructionLine(ProductionOrderStep step) {
        return Map.of(
                "sequenceNo", step.getSequenceNo(),
                "processId", step.getProcess().getId(),
                "processCode", step.getProcess().getCode(),
                "processName", step.getProcess().getName(),
                "equipmentId", value(step.getPlannedEquipment() == null ? null : step.getPlannedEquipment().getId()),
                "equipmentCode", value(step.getPlannedEquipment() == null ? null : step.getPlannedEquipment().getCode()),
                "status", step.getStatus()
        );
    }

    private PrintDocumentResponse buildIssueSlip(String documentKey, String barcodeValue) {
        InventoryMovement movement = inventoryMovementRepository.findById(extractId(documentKey))
                .orElseThrow(() -> new BusinessException("Inventory movement not found"));
        List<Map<String, Object>> lines = inventoryMovementLotRepository.findByMovementId(movement.getId()).stream()
                .map(this::movementLotLine)
                .toList();
        return new PrintDocumentResponse(PrintDocumentType.ISSUE_SLIP, documentKey, barcodeValue, "Issue Slip",
                movementHeader(movement), lines,
                Map.of("quantity", movement.getQuantity().getValue(),
                        "supplyAmount", movement.getSupplyAmount().getValue()));
    }

    private Map<String, Object> movementLotLine(InventoryMovementLot movementLot) {
        return Map.of(
                "lotId", value(movementLot.getLot() == null ? null : movementLot.getLot().getId()),
                "lotNo", value(movementLot.getLot() == null ? null : movementLot.getLot().getLotNo()),
                "quantity", movementLot.getQuantity().getValue()
        );
    }

    private Map<String, Object> movementHeader(InventoryMovement movement) {
        return Map.of(
                "movementId", movement.getId(),
                "itemId", movement.getItem().getId(),
                "itemCode", movement.getItem().getCode().getValue(),
                "warehouseId", movement.getWarehouse().getId(),
                "warehouseCode", movement.getWarehouse().getCode().getValue(),
                "movementType", movement.getMovementType(),
                "referenceType", movement.getReferenceType(),
                "referenceId", value(movement.getReferenceId()),
                "unitCost", movement.getUnitCost().getValue(),
                "occurredAt", movement.getOccurredAt()
        );
    }

    private PrintDocumentResponse buildTransactionStatement(String documentKey, String barcodeValue) {
        Long id = extractId(documentKey);
        SalesShipment shipment = salesShipmentRepository.findById(id)
                .orElseGet(() -> salesShipmentItemRepository.findById(id)
                        .map(SalesShipmentItem::getSalesShipment)
                        .orElseThrow(() -> new BusinessException("Sales shipment not found")));
        List<SalesShipmentItem> items = salesShipmentItemRepository.findBySalesShipmentId(shipment.getId());
        List<Map<String, Object>> lines = items.stream()
                .map(this::salesLine)
                .toList();
        return new PrintDocumentResponse(PrintDocumentType.TRANSACTION_STATEMENT, documentKey, barcodeValue,
                "Transaction Statement",
                Map.of(
                        "salesShipmentId", shipment.getId(),
                        "salesDate", shipment.getSalesDate(),
                        "partnerId", shipment.getPartner().getId(),
                        "partnerCode", shipment.getPartner().getCode().getValue(),
                        "partnerName", shipment.getPartner().getName()
                ), lines,
                Map.of("lineCount", lines.size(), "totalAmount", shipment.getTotalAmount().getValue()));
    }

    private Map<String, Object> salesLine(SalesShipmentItem item) {
        return Map.of(
                "salesShipmentItemId", item.getId(),
                "itemId", item.getItem().getId(),
                "itemCode", item.getItem().getCode().getValue(),
                "warehouseId", item.getWarehouse().getId(),
                "warehouseCode", item.getWarehouse().getCode().getValue(),
                "lotId", value(item.getLot() == null ? null : item.getLot().getId()),
                "lotNo", value(item.getLot() == null ? null : item.getLot().getLotNo()),
                "quantity", item.getQuantity().getValue(),
                "unitPrice", item.getUnitPrice().getValue(),
                "supplyAmount", item.getSupplyAmount().getValue()
        );
    }

    private PrintDocumentResponse buildPurchaseDocument(PrintDocumentType type, String documentKey,
                                                        String barcodeValue, String title) {
        Long id = extractId(documentKey);
        PurchaseReceipt receipt = purchaseReceiptRepository.findById(id)
                .orElseGet(() -> purchaseReceiptItemRepository.findById(id)
                        .map(PurchaseReceiptItem::getPurchaseReceipt)
                        .orElseThrow(() -> new BusinessException("Purchase receipt not found")));
        List<PurchaseReceiptItem> items = purchaseReceiptItemRepository.findByPurchaseReceiptId(receipt.getId());
        List<Map<String, Object>> lines = items.stream()
                .map(this::purchaseLine)
                .toList();
        return new PrintDocumentResponse(type, documentKey, barcodeValue, title,
                Map.of(
                        "purchaseReceiptId", receipt.getId(),
                        "purchaseDate", receipt.getPurchaseDate(),
                        "partnerId", receipt.getPartner().getId(),
                        "partnerCode", receipt.getPartner().getCode().getValue(),
                        "partnerName", receipt.getPartner().getName()
                ), lines,
                Map.of("lineCount", lines.size(), "totalAmount", receipt.getTotalAmount().getValue()));
    }

    private Map<String, Object> purchaseLine(PurchaseReceiptItem item) {
        return Map.of(
                "purchaseReceiptItemId", item.getId(),
                "itemId", item.getItem().getId(),
                "itemCode", item.getItem().getCode().getValue(),
                "warehouseId", item.getWarehouse().getId(),
                "warehouseCode", item.getWarehouse().getCode().getValue(),
                "lotId", value(item.getLot() == null ? null : item.getLot().getId()),
                "lotNo", value(item.getLot() == null ? null : item.getLot().getLotNo()),
                "quantity", item.getQuantity().getValue(),
                "unitPrice", item.getUnitPrice().getValue(),
                "supplyAmount", item.getSupplyAmount().getValue()
        );
    }

    private Object value(Object value) {
        return value == null ? "" : value;
    }

    private Long extractId(String documentKey) {
        int separator = documentKey.lastIndexOf('-');
        String idValue = separator < 0 ? documentKey : documentKey.substring(separator + 1);
        try {
            return Long.valueOf(idValue);
        } catch (NumberFormatException e) {
            throw new BusinessException("Invalid print document key");
        }
    }
}
