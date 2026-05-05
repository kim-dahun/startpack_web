import { routeMetaMap } from '@/router/routes'

const ErpMasterPage = () => import('@/pages/erp/master/Main.vue')
const ErpInventoryPage = () => import('@/pages/erp/inventory/Main.vue')
const ErpProductionPage = () => import('@/pages/erp/production/Main.vue')
const ErpFinancePage = () => import('@/pages/erp/finance/Main.vue')
const ErpPrintPage = () => import('@/pages/erp/prints/Main.vue')

export const erpRoutes = [
  { path: 'erp/items', name: 'erpItems', component: ErpMasterPage, props: { resourceKey: 'items' }, meta: routeMetaMap.erpItems },
  { path: 'erp/item-categories', name: 'erpItemCategories', component: ErpMasterPage, props: { resourceKey: 'itemCategories' }, meta: routeMetaMap.erpItemCategories },
  { path: 'erp/partners', name: 'erpPartners', component: ErpMasterPage, props: { resourceKey: 'partners' }, meta: routeMetaMap.erpPartners },
  { path: 'erp/warehouses', name: 'erpWarehouses', component: ErpMasterPage, props: { resourceKey: 'warehouses' }, meta: routeMetaMap.erpWarehouses },
  { path: 'erp/warehouse-safety-stocks', name: 'erpSafetyStocks', component: ErpMasterPage, props: { resourceKey: 'safetyStocks' }, meta: routeMetaMap.erpSafetyStocks },
  { path: 'erp/processes', name: 'erpProcesses', component: ErpMasterPage, props: { resourceKey: 'processes' }, meta: routeMetaMap.erpProcesses },
  { path: 'erp/routes', name: 'erpRoutes', component: ErpMasterPage, props: { resourceKey: 'routes' }, meta: routeMetaMap.erpRoutes },
  { path: 'erp/equipments', name: 'erpEquipments', component: ErpMasterPage, props: { resourceKey: 'equipments' }, meta: routeMetaMap.erpEquipments },
  { path: 'erp/costs/standard', name: 'erpStandardCosts', component: ErpMasterPage, props: { resourceKey: 'standardCosts' }, meta: routeMetaMap.erpStandardCosts },
  { path: 'erp/inventory/balances', name: 'erpInventoryBalances', component: ErpInventoryPage, props: { resourceKey: 'inventoryBalances' }, meta: routeMetaMap.erpInventoryBalances },
  { path: 'erp/inventory/movements', name: 'erpInventoryMovements', component: ErpInventoryPage, props: { resourceKey: 'inventoryMovements' }, meta: routeMetaMap.erpInventoryMovements },
  { path: 'erp/lots/balances', name: 'erpLotBalances', component: ErpInventoryPage, props: { resourceKey: 'lotBalances' }, meta: routeMetaMap.erpLotBalances },
  { path: 'erp/inventory/transfers', name: 'erpInventoryTransfers', component: ErpInventoryPage, props: { resourceKey: 'inventoryTransfers' }, meta: routeMetaMap.erpInventoryTransfers },
  { path: 'erp/purchases/receipts', name: 'erpPurchaseReceipts', component: ErpInventoryPage, props: { resourceKey: 'purchaseReceipts' }, meta: routeMetaMap.erpPurchaseReceipts },
  { path: 'erp/sales/shipments', name: 'erpSalesShipments', component: ErpInventoryPage, props: { resourceKey: 'salesShipments' }, meta: routeMetaMap.erpSalesShipments },
  { path: 'erp/boms', name: 'erpBoms', component: ErpProductionPage, props: { resourceKey: 'boms' }, meta: routeMetaMap.erpBoms },
  { path: 'erp/production/orders', name: 'erpProductionOrders', component: ErpProductionPage, props: { resourceKey: 'productionOrders' }, meta: routeMetaMap.erpProductionOrders },
  { path: 'erp/production/results', name: 'erpProductionResults', component: ErpProductionPage, props: { resourceKey: 'productionResults' }, meta: routeMetaMap.erpProductionResults },
  { path: 'erp/production/consumptions', name: 'erpProductionConsumptions', component: ErpProductionPage, props: { resourceKey: 'productionConsumptions' }, meta: routeMetaMap.erpProductionConsumptions },
  { path: 'erp/costs/actual-histories', name: 'erpActualCostHistories', component: ErpFinancePage, props: { resourceKey: 'actualCostHistories' }, meta: routeMetaMap.erpActualCostHistories },
  { path: 'erp/accounting/vouchers', name: 'erpAccountingVouchers', component: ErpFinancePage, props: { resourceKey: 'accountingVouchers' }, meta: routeMetaMap.erpAccountingVouchers },
  { path: 'erp/batch-executions', name: 'erpBatchExecutions', component: ErpFinancePage, props: { resourceKey: 'batchExecutions' }, meta: routeMetaMap.erpBatchExecutions },
  { path: 'erp/prints/work-instructions', name: 'erpPrintWorkInstructions', component: ErpPrintPage, props: { resourceKey: 'printWorkInstructions' }, meta: routeMetaMap.erpPrintWorkInstructions },
  { path: 'erp/prints/issue-slips', name: 'erpPrintIssueSlips', component: ErpPrintPage, props: { resourceKey: 'printIssueSlips' }, meta: routeMetaMap.erpPrintIssueSlips },
  { path: 'erp/prints/transaction-statements', name: 'erpPrintTransactionStatements', component: ErpPrintPage, props: { resourceKey: 'printTransactionStatements' }, meta: routeMetaMap.erpPrintTransactionStatements },
  { path: 'erp/prints/purchase-orders', name: 'erpPrintPurchaseOrders', component: ErpPrintPage, props: { resourceKey: 'printPurchaseOrders' }, meta: routeMetaMap.erpPrintPurchaseOrders },
  { path: 'erp/prints/goods-receipts', name: 'erpPrintGoodsReceipts', component: ErpPrintPage, props: { resourceKey: 'printGoodsReceipts' }, meta: routeMetaMap.erpPrintGoodsReceipts },
  { path: 'erp/prints/barcodes', name: 'erpPrintBarcodeLookup', component: ErpPrintPage, props: { resourceKey: 'printBarcodeLookup' }, meta: routeMetaMap.erpPrintBarcodeLookup },
]
