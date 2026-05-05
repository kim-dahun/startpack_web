package com.upmudoum.erp;

import com.upmudoum.erp.domain.bom.entity.BomVersion;
import com.upmudoum.erp.domain.bom.repository.BomRepository;
import com.upmudoum.erp.domain.bom.service.BomService;
import com.upmudoum.erp.domain.cost.entity.ItemActualCostHistory;
import com.upmudoum.erp.domain.cost.entity.ItemStandardCost;
import com.upmudoum.erp.domain.cost.repository.ItemActualCostHistoryRepository;
import com.upmudoum.erp.domain.cost.service.ItemCostService;
import com.upmudoum.erp.domain.inventory.dto.LotDeductionRequest;
import com.upmudoum.erp.domain.inventory.dto.InventoryAdjustmentRequest;
import com.upmudoum.erp.domain.inventory.dto.InventoryBalanceResponse;
import com.upmudoum.erp.domain.inventory.repository.InventoryLotBalanceRepository;
import com.upmudoum.erp.domain.inventory.repository.InventoryMovementLotRepository;
import com.upmudoum.erp.domain.inventory.service.InventoryLotService;
import com.upmudoum.erp.domain.inventory.service.InventoryService;
import com.upmudoum.erp.domain.inventory.vo.InventoryMovementType;
import com.upmudoum.erp.domain.item.dto.ItemRequest;
import com.upmudoum.erp.domain.item.dto.ItemResponse;
import com.upmudoum.erp.domain.item.service.ItemService;
import com.upmudoum.erp.domain.item.vo.ItemType;
import com.upmudoum.erp.domain.lot.entity.Lot;
import com.upmudoum.erp.domain.lot.repository.LotRepository;
import com.upmudoum.erp.domain.partner.dto.PartnerRequest;
import com.upmudoum.erp.domain.partner.dto.PartnerResponse;
import com.upmudoum.erp.domain.partner.service.PartnerService;
import com.upmudoum.erp.domain.partner.vo.PartnerStatus;
import com.upmudoum.erp.domain.production.dto.ProductionConsumptionAdjustmentRequest;
import com.upmudoum.erp.domain.production.entity.ProductionOrder;
import com.upmudoum.erp.domain.production.entity.ProductionResult;
import com.upmudoum.erp.domain.production.repository.ProductionConsumptionRepository;
import com.upmudoum.erp.domain.production.service.ProductionPlanService;
import com.upmudoum.erp.domain.production.vo.ProductionConsumptionAdjustType;
import com.upmudoum.erp.domain.purchase.entity.PurchaseReceiptItem;
import com.upmudoum.erp.domain.purchase.service.PurchaseReceiptService;
import com.upmudoum.erp.domain.sales.entity.SalesShipmentItem;
import com.upmudoum.erp.domain.sales.service.SalesShipmentService;
import com.upmudoum.erp.domain.warehouse.dto.WarehouseRequest;
import com.upmudoum.erp.domain.warehouse.dto.WarehouseResponse;
import com.upmudoum.erp.domain.warehouse.service.WarehouseService;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "erp.gateway.allowed-credentials=backend_gateway:local-dev-gateway-secret,backend_gateway_blue:blue-secret"
})
@Transactional
class ErpApplicationTests {

    @Autowired
    private ItemService itemService;

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private BomService bomService;

    @Autowired
    private BomRepository bomRepository;

    @Autowired
    private InventoryLotService inventoryLotService;

    @Autowired
    private ProductionPlanService productionPlanService;

    @Autowired
    private LotRepository lotRepository;

    @Autowired
    private InventoryLotBalanceRepository lotBalanceRepository;

    @Autowired
    private InventoryMovementLotRepository movementLotRepository;

    @Autowired
    private ProductionConsumptionRepository consumptionRepository;

    @Autowired
    private ItemCostService itemCostService;

    @Autowired
    private ItemActualCostHistoryRepository actualCostHistoryRepository;

    @Autowired
    private PurchaseReceiptService purchaseReceiptService;

    @Autowired
    private SalesShipmentService salesShipmentService;

    @BeforeEach
    void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void rejectsDirectApiRequestAndAllowsGatewayRequest() throws Exception {
        mockMvc.perform(get("/api/erp/items"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/erp/items").header("X-Gateway-Request", "true"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/erp/items")
                        .header("X-Internal-Gateway-Id", "unknown-gateway")
                        .header("X-Internal-Gateway-Secret", "local-dev-gateway-secret"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/erp/items")
                        .header("X-Internal-Gateway-Id", "backend_gateway")
                        .header("X-Internal-Gateway-Secret", "wrong-secret"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/erp/items")
                        .header("X-Internal-Gateway-Id", "backend_gateway")
                        .header("X-Internal-Gateway-Secret", "local-dev-gateway-secret")
                        .header("X-Request-Id", "test-request-id")
                        .header("X-User-Id", "test-user")
                        .header("X-Com-Cd", "TEST-COMPANY"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/erp/items")
                        .header("X-Internal-Gateway-Id", "backend_gateway_blue")
                        .header("X-Internal-Gateway-Secret", "blue-secret"))
                .andExpect(status().isOk());

        Path todayLog = Path.of("logs", "9094", LocalDate.now() + ".log");
        assertThat(Files.exists(todayLog)).isTrue();
        String logContent = new String(Files.readAllBytes(todayLog), StandardCharsets.UTF_8);
        assertThat(logContent).contains("api result=SUCCESS method=GET path=/api/erp/items status=200");
        assertThat(logContent).contains("test-request-id");
    }

    @Test
    void createsMasterDataAndCalculatesInventoryBalance() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setCode("ITEM-001");
        itemRequest.setName("원재료 A");
        itemRequest.setUnit("EA");
        ItemResponse item = itemService.create(itemRequest);

        WarehouseRequest warehouseRequest = new WarehouseRequest();
        warehouseRequest.setCode("WH-001");
        warehouseRequest.setName("기본 창고");
        warehouseRequest.setLocation("Seoul");
        WarehouseResponse warehouse = warehouseService.create(warehouseRequest);

        PartnerRequest partnerRequest = new PartnerRequest();
        partnerRequest.setCode("PTN-001");
        partnerRequest.setName("거래처 A");
        partnerRequest.setBusinessNumber("123-45-67890");
        partnerRequest.setPartnerType("SUPPLIER");
        partnerRequest.setStatus(PartnerStatus.ACTIVE);
        PartnerResponse partner = partnerService.create(partnerRequest);
        assertThat(partner.getStatus()).isEqualTo(PartnerStatus.ACTIVE);

        InventoryAdjustmentRequest initial = new InventoryAdjustmentRequest();
        initial.setItemId(item.getId());
        initial.setWarehouseId(warehouse.getId());
        initial.setMovementType(InventoryMovementType.INITIAL);
        initial.setQuantity(new BigDecimal("10.000000"));
        inventoryService.adjust(initial);

        InventoryAdjustmentRequest issue = new InventoryAdjustmentRequest();
        issue.setItemId(item.getId());
        issue.setWarehouseId(warehouse.getId());
        issue.setMovementType(InventoryMovementType.ISSUE);
        issue.setQuantity(new BigDecimal("3.500000"));
        inventoryService.adjust(issue);

        InventoryBalanceResponse balance = inventoryService.findBalance(item.getId(), warehouse.getId());
        assertThat(balance.getQuantity()).isEqualByComparingTo("6.500000");

        Integer coreTableCount = jdbcTemplate.queryForObject("""
                select count(*)
                from information_schema.tables
                where lower(table_schema) = 'erp_service'
                  and lower(table_name) in (
                    'erp_items',
                    'erp_partners',
                    'erp_warehouses',
                    'erp_inventory_balances',
                    'erp_inventory_movements'
                  )
                """, Integer.class);
        assertThat(coreTableCount).isEqualTo(5);
    }

    @Test
    void recordsProductionResultWithBomConsumptionFifoAndSelectedLot() {
        ItemResponse raw = createItem("RAW-001", "원재료", ItemType.RAW_MATERIAL);
        ItemResponse finished = createItem("FG-001", "완제품", ItemType.FINISHED_GOOD);
        WarehouseResponse warehouse = createWarehouse("WH-PROD", "생산 창고");

        inventoryLotService.receive(raw.getId(), warehouse.getId(), "RAW-L1", new BigDecimal("5.000000"),
                InventoryMovementType.RECEIPT, "raw lot 1");
        inventoryLotService.receive(raw.getId(), warehouse.getId(), "RAW-L2", new BigDecimal("10.000000"),
                InventoryMovementType.RECEIPT, "raw lot 2");

        BomVersion bomVersion = bomService.createVersion(finished.getId(), "v1", LocalDate.now(), null);
        assertThat(bomRepository.findByParentItemIdAndEnabledTrue(finished.getId())
                .orElseThrow().getDefaultBomVersion().getId()).isEqualTo(bomVersion.getId());
        bomService.addComponent(bomVersion.getId(), raw.getId(), new BigDecimal("2.000000"), BigDecimal.ZERO);

        ProductionOrder firstOrder = productionPlanService.createOrder("PO-001", finished.getId(), bomVersion.getId(),
                new BigDecimal("3.000000"), LocalDate.now());
        ProductionResult firstResult = productionPlanService.recordResult(firstOrder.getId(), warehouse.getId(),
                new BigDecimal("3.000000"), BigDecimal.ZERO, "FG-L1", List.of());

        Lot rawLot1 = lotRepository.findByItemIdAndLotNo(raw.getId(), "RAW-L1").orElseThrow();
        Lot rawLot2 = lotRepository.findByItemIdAndLotNo(raw.getId(), "RAW-L2").orElseThrow();
        Lot finishedLot = lotRepository.findByItemIdAndLotNo(finished.getId(), "FG-L1").orElseThrow();

        assertThat(lotBalanceRepository.findByItemIdAndWarehouseIdAndLotId(raw.getId(), warehouse.getId(), rawLot1.getId())
                .orElseThrow().getQuantity().getValue()).isEqualByComparingTo("0.000000");
        assertThat(lotBalanceRepository.findByItemIdAndWarehouseIdAndLotId(raw.getId(), warehouse.getId(), rawLot2.getId())
                .orElseThrow().getQuantity().getValue()).isEqualByComparingTo("9.000000");
        assertThat(lotBalanceRepository.findByItemIdAndWarehouseIdAndLotId(finished.getId(), warehouse.getId(), finishedLot.getId())
                .orElseThrow().getQuantity().getValue()).isEqualByComparingTo("3.000000");
        assertThat(consumptionRepository.findByProductionResultId(firstResult.getId())).hasSize(1);

        ProductionOrder secondOrder = productionPlanService.createOrder("PO-002", finished.getId(), bomVersion.getId(),
                new BigDecimal("1.000000"), LocalDate.now());
        ProductionConsumptionAdjustmentRequest selectedLotAdjustment = new ProductionConsumptionAdjustmentRequest(
                raw.getId(), new BigDecimal("1.000000"), ProductionConsumptionAdjustType.ADJUSTED,
                List.of(new LotDeductionRequest(rawLot2.getId(), new BigDecimal("1.000000"))));
        productionPlanService.recordResult(secondOrder.getId(), warehouse.getId(), new BigDecimal("1.000000"),
                BigDecimal.ZERO, "FG-L2", List.of(selectedLotAdjustment));

        assertThat(lotBalanceRepository.findByItemIdAndWarehouseIdAndLotId(raw.getId(), warehouse.getId(), rawLot2.getId())
                .orElseThrow().getQuantity().getValue()).isEqualByComparingTo("8.000000");
    }

    @Test
    void tracksCostPurchaseReceiptSalesShipmentAndLotAmounts() {
        ItemResponse item = createItem("SALE-ITEM-001", "판매 품목", ItemType.FINISHED_GOOD);
        WarehouseResponse warehouse = createWarehouse("WH-SALES", "판매 창고");
        PartnerResponse partner = createPartner("PTN-SALES", "매입매출 거래처");

        ItemStandardCost standardCost = itemCostService.registerStandardCost(
                item.getId(), new BigDecimal("900.0000"), "KRW", LocalDate.now(), null);
        assertThat(standardCost.getStandardCost().getValue()).isEqualByComparingTo("900.0000");

        PurchaseReceiptItem firstReceiptItem = purchaseReceiptService.receive(partner.getId(), item.getId(), warehouse.getId(),
                "PLOT-1", new BigDecimal("10.000000"), new BigDecimal("1000.0000"), LocalDate.now());
        PurchaseReceiptItem secondReceiptItem = purchaseReceiptService.receive(partner.getId(), item.getId(), warehouse.getId(),
                "PLOT-2", new BigDecimal("5.000000"), new BigDecimal("1100.0000"), LocalDate.now());

        assertThat(firstReceiptItem.getSupplyAmount().getValue()).isEqualByComparingTo("10000.0000000000");
        assertThat(secondReceiptItem.getSupplyAmount().getValue()).isEqualByComparingTo("5500.0000000000");

        Lot firstLot = lotRepository.findByItemIdAndLotNo(item.getId(), "PLOT-1").orElseThrow();
        Lot secondLot = lotRepository.findByItemIdAndLotNo(item.getId(), "PLOT-2").orElseThrow();

        SalesShipmentItem fifoShipmentItem = salesShipmentService.ship(partner.getId(), item.getId(), warehouse.getId(),
                new BigDecimal("12.000000"), new BigDecimal("1500.0000"), LocalDate.now(), List.of());
        assertThat(fifoShipmentItem.getSupplyAmount().getValue()).isEqualByComparingTo("18000.0000000000");
        assertThat(movementLotRepository.findByMovementId(fifoShipmentItem.getInventoryMovement().getId())).hasSize(2);
        assertThat(lotBalanceRepository.findByItemIdAndWarehouseIdAndLotId(item.getId(), warehouse.getId(), firstLot.getId())
                .orElseThrow().getQuantity().getValue()).isEqualByComparingTo("0.000000");
        assertThat(lotBalanceRepository.findByItemIdAndWarehouseIdAndLotId(item.getId(), warehouse.getId(), secondLot.getId())
                .orElseThrow().getQuantity().getValue()).isEqualByComparingTo("3.000000");

        SalesShipmentItem selectedLotShipmentItem = salesShipmentService.ship(partner.getId(), item.getId(), warehouse.getId(),
                new BigDecimal("1.000000"), new BigDecimal("1600.0000"), LocalDate.now(),
                List.of(new LotDeductionRequest(secondLot.getId(), new BigDecimal("1.000000"))));
        assertThat(movementLotRepository.findByMovementId(selectedLotShipmentItem.getInventoryMovement().getId())).hasSize(1);
        assertThat(lotBalanceRepository.findByItemIdAndWarehouseIdAndLotId(item.getId(), warehouse.getId(), secondLot.getId())
                .orElseThrow().getQuantity().getValue()).isEqualByComparingTo("2.000000");

        List<ItemActualCostHistory> histories = actualCostHistoryRepository.findByItemId(item.getId());
        assertThat(histories).hasSize(4);
    }

    private ItemResponse createItem(String code, String name, ItemType itemType) {
        ItemRequest request = new ItemRequest();
        request.setCode(code);
        request.setName(name);
        request.setUnit("EA");
        request.setItemType(itemType);
        return itemService.create(request);
    }

    private WarehouseResponse createWarehouse(String code, String name) {
        WarehouseRequest request = new WarehouseRequest();
        request.setCode(code);
        request.setName(name);
        request.setLocation("Seoul");
        return warehouseService.create(request);
    }

    private PartnerResponse createPartner(String code, String name) {
        PartnerRequest request = new PartnerRequest();
        request.setCode(code);
        request.setName(name);
        request.setBusinessNumber("000-00-00000");
        request.setPartnerType("CUSTOMER");
        request.setStatus(PartnerStatus.ACTIVE);
        return partnerService.create(request);
    }
}
