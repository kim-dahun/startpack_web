<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Button from 'primevue/button'
import Dropdown from 'primevue/dropdown'
import InputNumber from 'primevue/inputnumber'
import InputText from 'primevue/inputtext'
import SelectButton from 'primevue/selectbutton'
import { useToast } from 'primevue/usetoast'

import TradeCandleChart from '@/components/chart/TradeCandleChart.vue'
import BaseEmptyState from '@/components/common/BaseEmptyState.vue'
import BasePageHeader from '@/components/common/BasePageHeader.vue'
import TradePanel from '@/components/trade/TradePanel.vue'
import TradeRecordTable from '@/components/trade/TradeRecordTable.vue'
import { useAppI18n } from '@/composables/useAppI18n'
import { useSessionStore } from '@/stores/session'
import { useTradeRealtimeStore } from '@/stores/trade/realtime'
import { useTradeSocketStore } from '@/stores/trade/socket'
import { useTradeWorkspaceStore } from '@/stores/trade/workspace'
import type {
  TradeAccountSummary,
  TradeCashOrderResult,
  TradeChartDrawing,
  TradeChartPeriodType,
  TradeDrawingType,
  TradeEventItem,
  TradeMode,
  TradeOrderValidationResult,
  TradeOrderableAmountResult,
  TradeRankingRow,
  TradeRankingType,
  TradeSide,
} from '@/types/trade'

import {
  addWatchlistForCurrentUser,
  createCurrentUserItemDrawing,
  deleteCurrentUserItemDrawing,
  executeCashOrder,
  fetchAccounts,
  fetchCorporateActions,
  fetchIpoSubscriptions,
  fetchParValueChanges,
  fetchRankingRows,
  fetchRealtimeStatus,
  fetchTradeEvents,
  fetchWorkspaceChartSnapshot,
  fetchWorkspaceSnapshot,
  fetchWorkspaceTradingSnapshot,
  runValidateOrder,
  updateCurrentUserItemDrawing,
} from './api/api'
import LiveOrderConfirmPopupView from '../item/popup-view/LiveOrderConfirmPopupView.vue'

type EventTab = 'ALL' | 'IPO_SUBSCRIPTION' | 'PAR_VALUE_CHANGE' | 'CORPORATE_ACTION'

const today = new Date().toISOString().slice(0, 10)
const monthAgo = new Date(Date.now() - 1000 * 60 * 60 * 24 * 30).toISOString().slice(0, 10)

const route = useRoute()
const router = useRouter()
const toast = useToast()
const { t } = useAppI18n()
const sessionStore = useSessionStore()
const workspaceStore = useTradeWorkspaceStore()
const realtimeStore = useTradeRealtimeStore()
const socketStore = useTradeSocketStore()

const loading = ref(false)
const loadingChart = ref(false)
const loadingTrading = ref(false)
const loadingValidation = ref(false)
const loadingOrder = ref(false)
const liveOrderPopupVisible = ref(false)
const realtimeConnected = ref(false)

const accounts = ref<TradeAccountSummary[]>([])
const marketEvents = ref<TradeEventItem[]>([])
const rankingRows = ref<TradeRankingRow[]>([])

const selectedEventTab = ref<EventTab>('ALL')
const rankingType = ref<TradeRankingType>('volume')
const rankingMasterType = ref('KOSPI')
const chartFrom = ref(monthAgo)
const chartTo = ref(today)

const drawingForm = reactive({
  id: 0,
  drawingType: 'UPPER_LINE' as TradeDrawingType,
  startDate: today,
  startPrice: 0,
  endDate: today,
  endPrice: 0,
  memo: '',
})

const orderForm = reactive({
  accountNo: '',
  itemCode: '',
  side: 'BUY' as TradeSide,
  quantity: 1,
  price: 0,
})

const validationResult = ref<TradeOrderValidationResult | null>(null)
const orderResult = ref<TradeCashOrderResult | null>(null)
const manualOrderable = ref<TradeOrderableAmountResult | null>(null)

const tradeModeOptions = [
  { label: 'LIVE', value: 'LIVE' },
  { label: 'PAPER', value: 'PAPER' },
]

const intervalOptions = [
  { label: '1m', value: '1m' },
  { label: '5m', value: '5m' },
  { label: '15m', value: '15m' },
  { label: '30m', value: '30m' },
  { label: '60m', value: '60m' },
  { label: 'DAY', value: 'DAY' },
  { label: 'WEEK', value: 'WEEK' },
  { label: 'MONTH', value: 'MONTH' },
  { label: 'YEAR', value: 'YEAR' },
]

const currentUserId = computed(() => sessionStore.persisted.user?.userId ?? '')
const currentQuote = computed(() => realtimeStore.quote)
const currentMetrics = computed(() => realtimeStore.metrics)
const currentOrderbook = computed(() => realtimeStore.orderbook)
const currentBalance = computed(() => realtimeStore.balance)
const currentPositions = computed(() => realtimeStore.positions)
const currentPosition = computed(() => realtimeStore.currentPosition)
const currentDrawings = computed(() => realtimeStore.drawings)
const currentIndicators = computed(() => realtimeStore.indicators)
const currentChartRows = computed(() => realtimeStore.chartRows)
const currentWatchlistItems = computed(() => realtimeStore.watchlistItems)
const currentFrequentSearches = computed(() => realtimeStore.frequentSearches)
const currentOrderable = computed(() => manualOrderable.value ?? realtimeStore.orderableAmount)

const accountOptions = computed(() =>
  accounts.value.map((account) => ({
    label: `${account.accountNo} / ${account.accountName}`,
    value: account.accountNo,
  })),
)

const selectedAccountLabel = computed(() =>
  accountOptions.value.find((option) => option.value === workspaceStore.selectedAccountNo)?.label ?? workspaceStore.selectedAccountNo,
)

const eventColumns = computed(() => [
  { field: 'eventDate', title: 'Date' },
  { field: 'title', title: t('trade.analysis.title') },
  { field: 'itemCode', title: t('trade.label.itemCode') },
  { field: 'eventType', title: t('trade.analysis.eventType') },
])

const rankingColumns = computed(() => [
  { field: 'rank', title: t('trade.analysis.rank') },
  { field: 'itemCode', title: t('trade.label.itemCode') },
  { field: 'itemName', title: t('trade.label.itemName') },
  { field: 'marketCode', title: t('trade.label.market') },
  { field: 'metricValue', title: t('trade.analysis.metricValue') },
])

const positionColumns = computed(() => [
  { field: 'itemCode', title: t('trade.label.itemCode') },
  { field: 'itemName', title: t('trade.label.itemName') },
  { field: 'quantity', title: t('trade.label.quantity') },
  { field: 'orderableQuantity', title: t('trade.label.orderableQuantity') },
  { field: 'averagePrice', title: 'Avg' },
  { field: 'currentPrice', title: t('trade.label.currentPrice') },
  { field: 'profitLossAmount', title: 'P/L' },
  { field: 'profitLossRate', title: 'P/L %' },
])

const watchlistColumns = computed(() => [
  { field: 'itemCode', title: t('trade.label.itemCode') },
  { field: 'itemName', title: t('trade.label.itemName') },
  { field: 'memo', title: t('trade.label.memo') },
])

const frequentColumns = computed(() => [
  { field: 'itemCode', title: t('trade.label.itemCode') },
  { field: 'itemName', title: t('trade.label.itemName') },
  { field: 'searchCount', title: t('trade.label.searchCount') },
  { field: 'lastSearchedAt', title: t('trade.label.lastSearchedAt') },
])

const drawingColumns = computed(() => [
  { field: 'drawingTypeLabel', title: t('trade.label.type') },
  { field: 'startDate', title: t('trade.label.startDate') },
  { field: 'startPrice', title: t('trade.label.startPrice') },
  { field: 'endDate', title: t('trade.label.endDate') },
  { field: 'endPrice', title: t('trade.label.endPrice') },
  { field: 'memo', title: t('trade.label.memo') },
])

const eventFeedColumns = computed(() => [
  { field: 'occurredAt', title: 'Time' },
  { field: 'eventType', title: 'Event' },
  { field: 'itemCode', title: t('trade.label.itemCode') },
  { field: 'detail', title: 'Detail' },
])

const summaryStats = computed(() => [
  { label: t('trade.label.currentPrice'), value: Number(currentQuote.value?.currentPrice ?? 0).toLocaleString() },
  { label: t('trade.label.changeRate'), value: `${Number(currentQuote.value?.changeRate ?? 0).toFixed(2)}%` },
  { label: t('trade.label.volume'), value: Number(currentQuote.value?.accumulatedVolume ?? 0).toLocaleString() },
  { label: '52W High', value: Number(currentMetrics.value?.high52WeekPrice ?? 0).toLocaleString() },
  { label: '52W Low', value: Number(currentMetrics.value?.low52WeekPrice ?? 0).toLocaleString() },
  { label: 'PBR', value: currentMetrics.value?.pbr ?? '-' },
  { label: 'PER', value: currentMetrics.value?.per ?? '-' },
  { label: t('trade.label.realtimeConnection'), value: realtimeConnected.value ? 'CONNECTED' : 'DISCONNECTED' },
])

const metaFacts = computed(() => [
  { label: t('trade.label.itemName'), value: currentQuote.value?.itemName ?? workspaceStore.selectedItemName ?? '-' },
  { label: t('trade.label.market'), value: currentMetrics.value?.marketCode ?? '-' },
  { label: t('trade.label.sector'), value: currentMetrics.value?.sectorName ?? '-' },
  { label: 'EPS', value: currentMetrics.value?.eps ?? '-' },
  { label: 'BPS', value: currentMetrics.value?.bps ?? '-' },
  { label: t('trade.label.salesAmount'), value: currentMetrics.value?.salesAmount ?? '-' },
  { label: t('trade.label.operatingProfit'), value: currentMetrics.value?.operatingProfit ?? '-' },
  { label: t('trade.label.marketCap'), value: currentMetrics.value?.marketCap ?? '-' },
])

const indicatorStats = computed(() => {
  const indicators = currentIndicators.value
  return [
    { label: 'MA5', value: Number(indicators?.movingAverages?.ma5 ?? 0).toFixed(2) },
    { label: 'MA20', value: Number(indicators?.movingAverages?.ma20 ?? 0).toFixed(2) },
    { label: 'MA60', value: Number(indicators?.movingAverages?.ma60 ?? 0).toFixed(2) },
    { label: 'MA120', value: Number(indicators?.movingAverages?.ma120 ?? 0).toFixed(2) },
    { label: 'RSI', value: Number(indicators?.rsi ?? 0).toFixed(2) },
    { label: 'MACD', value: Number(indicators?.macd ?? 0).toFixed(2) },
    { label: 'Signal', value: Number(indicators?.macdSignal ?? 0).toFixed(2) },
    { label: 'Boll+', value: Number(indicators?.bollingerUpper ?? 0).toFixed(2) },
    { label: 'Boll-', value: Number(indicators?.bollingerLower ?? 0).toFixed(2) },
  ]
})

const accountStats = computed(() => [
  { label: 'Account', value: selectedAccountLabel.value || '-' },
  { label: t('trade.account.balance.totalAsset'), value: Number(currentBalance.value?.totalAssetAmount ?? 0).toLocaleString() },
  { label: t('trade.account.balance.cash'), value: Number(currentBalance.value?.cashAmount ?? 0).toLocaleString() },
  { label: t('trade.account.balance.orderableCash'), value: Number(currentBalance.value?.orderableCashAmount ?? 0).toLocaleString() },
  { label: 'Current Position', value: currentPosition.value ? `${currentPosition.value.itemName} / ${Number(currentPosition.value.quantity).toLocaleString()}` : '-' },
])

const orderEventRows = computed(() =>
  realtimeStore.orderEvents.map((event, index) => ({
    id: `${event.eventType}-${event.sequenceNo ?? index}`,
    occurredAt: event.occurredAt,
    eventType: event.eventType,
    itemCode: event.itemCode ?? '',
    detail: JSON.stringify(event.payload ?? {}),
  })),
)

const drawingRows = computed(() =>
  currentDrawings.value.map((drawing) => ({
    ...drawing,
    drawingTypeLabel: drawing.drawingType === 'UPPER_LINE' ? t('trade.label.drawingTypeUpper') : t('trade.label.drawingTypeLower'),
  })),
)

const rankingTableRows = computed(() =>
  rankingRows.value.map((row) => ({
    ...row,
    metricValue: Number(row.metricValue ?? 0).toLocaleString(),
  })),
)

const selectItem = (itemCode: string, itemName = '') => {
  workspaceStore.setSelectedItem(itemCode, itemName)
  orderForm.itemCode = itemCode
}

const syncSelectedAccount = () => {
  if (!workspaceStore.selectedAccountNo && accounts.value.length) {
    workspaceStore.setSelectedAccountNo(accounts.value[0].accountNo)
  }

  orderForm.accountNo = workspaceStore.selectedAccountNo
}

const syncWorkspaceRoute = async () => {
  const queryItemCode = typeof route.query.itemCode === 'string' ? route.query.itemCode : ''
  if (queryItemCode && queryItemCode !== workspaceStore.selectedItemCode) {
    selectItem(queryItemCode)
  }
}

const refreshRealtimeStatus = async () => {
  const status = await fetchRealtimeStatus()
  realtimeConnected.value = status.kisConnected
}

const refreshAccounts = async () => {
  accounts.value = await fetchAccounts(workspaceStore.tradeMode)
  syncSelectedAccount()
}

const refreshWorkspaceSnapshot = async () => {
  if (!workspaceStore.selectedItemCode) {
    realtimeStore.resetWorkspace()
    return
  }

  const snapshot = await fetchWorkspaceSnapshot(workspaceStore.selectedItemCode, workspaceStore.tradeMode)
  realtimeStore.setWorkspaceSnapshot(snapshot)
  selectItem(snapshot.itemCode, snapshot.quote?.itemName ?? workspaceStore.selectedItemName)

  if (!orderForm.price) {
    orderForm.price = Number(snapshot.quote?.currentPrice ?? 0)
  }
}

const refreshChartSnapshot = async () => {
  if (!workspaceStore.selectedItemCode) {
    realtimeStore.setChartSnapshot(null)
    return
  }

  loadingChart.value = true
  try {
    const snapshot = await fetchWorkspaceChartSnapshot(
      workspaceStore.selectedItemCode,
      workspaceStore.chartInterval,
      chartFrom.value,
      chartTo.value,
      workspaceStore.tradeMode,
    )
    realtimeStore.setChartSnapshot(snapshot)
    drawingForm.startDate = snapshot.candles[0]?.baseDate ?? today
    drawingForm.endDate = snapshot.candles.at(-1)?.baseDate ?? today
  } finally {
    loadingChart.value = false
  }
}

const refreshTradingSnapshot = async () => {
  if (!workspaceStore.selectedItemCode || !workspaceStore.selectedAccountNo) {
    realtimeStore.setTradingSnapshot(null)
    return
  }

  loadingTrading.value = true
  try {
    const snapshot = await fetchWorkspaceTradingSnapshot(
      workspaceStore.selectedItemCode,
      workspaceStore.selectedAccountNo,
      orderForm.price || currentQuote.value?.currentPrice || null,
      workspaceStore.tradeMode,
    )
    realtimeStore.setTradingSnapshot(snapshot)
    manualOrderable.value = null
  } finally {
    loadingTrading.value = false
  }
}

const refreshMarketInfo = async () => {
  const [eventsResult, rankingResult] = await Promise.allSettled([
    selectedEventTab.value === 'IPO_SUBSCRIPTION'
      ? fetchIpoSubscriptions()
      : selectedEventTab.value === 'PAR_VALUE_CHANGE'
        ? fetchParValueChanges()
        : selectedEventTab.value === 'CORPORATE_ACTION'
          ? fetchCorporateActions()
          : fetchTradeEvents(),
    fetchRankingRows(rankingType.value, rankingMasterType.value),
  ])

  marketEvents.value = eventsResult.status === 'fulfilled' ? eventsResult.value : []
  rankingRows.value = rankingResult.status === 'fulfilled' ? rankingResult.value : []
}

const refreshWorkspace = async () => {
  loading.value = true
  try {
    await Promise.allSettled([
      refreshRealtimeStatus(),
      refreshAccounts(),
    ])

    await Promise.allSettled([
      refreshWorkspaceSnapshot(),
      refreshChartSnapshot(),
    ])

    await refreshTradingSnapshot()
    await refreshMarketInfo()

    socketStore.connect()
    socketStore.syncWorkspaceSubscriptions({
      itemCode: workspaceStore.selectedItemCode,
      accountNo: workspaceStore.selectedAccountNo,
    })
  } finally {
    loading.value = false
  }
}

const handleAddWatchlist = async () => {
  if (!workspaceStore.selectedItemCode || !currentQuote.value) {
    return
  }

  await addWatchlistForCurrentUser({
    itemCode: workspaceStore.selectedItemCode,
    itemName: currentQuote.value.itemName,
  })

  toast.add({
    severity: 'success',
    summary: t('trade.market.watchlistAddedTitle'),
    detail: t('trade.market.watchlistAddedDetail', undefined, { itemName: currentQuote.value.itemName }),
    life: 2000,
  })

  await refreshWorkspaceSnapshot()
}

const handleOrderbookPick = async (side: TradeSide, price: number) => {
  orderForm.side = side
  orderForm.price = price
  workspaceStore.setSelectedOrderSide(side)
  workspaceStore.setSelectedOrderPrice(price)
  await refreshTradingSnapshot()
}

const handleValidate = async () => {
  if (!orderForm.accountNo || !orderForm.itemCode || !orderForm.price) {
    return
  }

  loadingValidation.value = true
  try {
    validationResult.value = await runValidateOrder({
      accountNo: orderForm.accountNo,
      itemCode: orderForm.itemCode,
      side: orderForm.side,
      quantity: Number(orderForm.quantity),
      price: Number(orderForm.price),
      availableCashAmount: currentOrderable.value?.orderableCashAmount ?? currentBalance.value?.orderableCashAmount ?? null,
      availableQuantity: currentPosition.value?.orderableQuantity ?? currentPosition.value?.quantity ?? null,
    })
  } finally {
    loadingValidation.value = false
  }
}

const executeOrder = async (confirmLiveOrder: boolean) => {
  loadingOrder.value = true
  try {
    orderResult.value = await executeCashOrder({
      accountNo: orderForm.accountNo,
      itemCode: orderForm.itemCode,
      side: orderForm.side,
      quantity: Number(orderForm.quantity),
      price: Number(orderForm.price),
      tradeMode: workspaceStore.tradeMode,
      confirmLiveOrder,
    })

    toast.add({
      severity: 'success',
      summary: t('trade.market.orderAcceptedTitle'),
      detail: `${orderResult.value.orderNo} / ${orderResult.value.message}`,
      life: 2200,
    })

    await refreshTradingSnapshot()
  } finally {
    loadingOrder.value = false
  }
}

const requestOrder = async () => {
  if (workspaceStore.tradeMode === 'LIVE') {
    liveOrderPopupVisible.value = true
    return
  }

  await executeOrder(false)
}

const resetDrawingForm = () => {
  drawingForm.id = 0
  drawingForm.drawingType = 'UPPER_LINE'
  drawingForm.startDate = currentChartRows.value[0]?.baseDate ?? today
  drawingForm.startPrice = 0
  drawingForm.endDate = currentChartRows.value.at(-1)?.baseDate ?? today
  drawingForm.endPrice = 0
  drawingForm.memo = ''
}

const saveDrawing = async () => {
  if (!workspaceStore.selectedItemCode) {
    return
  }

  const payload = {
    drawingType: drawingForm.drawingType,
    startDate: drawingForm.startDate,
    startPrice: Number(drawingForm.startPrice),
    endDate: drawingForm.endDate,
    endPrice: Number(drawingForm.endPrice),
    memo: drawingForm.memo,
  }

  if (drawingForm.id) {
    await updateCurrentUserItemDrawing(workspaceStore.selectedItemCode, drawingForm.id, payload)
  } else {
    await createCurrentUserItemDrawing(workspaceStore.selectedItemCode, payload)
  }

  resetDrawingForm()
  await refreshChartSnapshot()
}

const editDrawing = (row: TradeChartDrawing) => {
  drawingForm.id = row.id
  drawingForm.drawingType = row.drawingType
  drawingForm.startDate = row.startDate
  drawingForm.startPrice = row.startPrice
  drawingForm.endDate = row.endDate
  drawingForm.endPrice = row.endPrice
  drawingForm.memo = row.memo ?? ''
}

const removeDrawing = async (row: TradeChartDrawing) => {
  if (!workspaceStore.selectedItemCode) {
    return
  }

  await deleteCurrentUserItemDrawing(workspaceStore.selectedItemCode, row.id)
  await refreshChartSnapshot()
}

const openSearch = async () => {
  await router.push({ name: 'items' })
}

const openAnalysis = async () => {
  await router.push({ name: 'tradeAnalysis' })
}

const openAdmin = async () => {
  await router.push({ name: 'masters' })
}

const openWatchlist = async () => {
  await router.push({ name: 'watchlist' })
}

watch(
  () => route.query.itemCode,
  async () => {
    await syncWorkspaceRoute()
    await refreshWorkspace()
  },
)

watch(
  () => workspaceStore.tradeMode,
  async () => {
    await refreshWorkspace()
  },
)

watch(
  () => workspaceStore.selectedAccountNo,
  async () => {
    syncSelectedAccount()
    socketStore.syncWorkspaceSubscriptions({
      itemCode: workspaceStore.selectedItemCode,
      accountNo: workspaceStore.selectedAccountNo,
    })
    await refreshTradingSnapshot()
  },
)

watch(
  () => workspaceStore.chartInterval,
  async () => {
    await refreshChartSnapshot()
  },
)

watch([selectedEventTab, rankingType, rankingMasterType], async () => {
  await refreshMarketInfo()
})

onMounted(async () => {
  await syncWorkspaceRoute()
  if (workspaceStore.selectedItemCode) {
    orderForm.itemCode = workspaceStore.selectedItemCode
  }
  await refreshWorkspace()
})

onBeforeUnmount(() => {
  socketStore.clearWorkspaceSubscriptions()
})
</script>

<template>
  <div class="page-stack">
    <BasePageHeader title="menu.realtime" :description="'Workspace for detail, chart, orderbook, order, and market context in one flow.'" />

    <BaseEmptyState
      v-if="!workspaceStore.selectedItemCode"
      :title="t('trade.market.emptyTitle')"
      :description="t('trade.market.emptyDescription')"
    />

    <template v-else>
      <TradePanel :title="'Stock Workspace'" :description="'HTTP snapshot first, then WebSocket delta applies over quote, orderbook, order status, and balance.'" >
        <template #actions>
          <Dropdown
            :model-value="workspaceStore.tradeMode"
            :options="tradeModeOptions"
            option-label="label"
            option-value="value"
            class="trade-panel__dropdown"
            @update:model-value="workspaceStore.setTradeMode($event as TradeMode)"
          />
          <Dropdown
            :model-value="workspaceStore.selectedAccountNo"
            :options="accountOptions"
            option-label="label"
            option-value="value"
            class="trade-panel__dropdown"
            :placeholder="t('trade.market.selectAccount')"
            @update:model-value="workspaceStore.setSelectedAccountNo(String($event ?? ''))"
          />
          <InputText
            :model-value="workspaceStore.selectedItemCode"
            :placeholder="t('trade.label.itemCode')"
            @update:model-value="selectItem(String($event ?? ''))"
          />
          <Button icon="pi pi-refresh" :label="t('common.refresh')" size="small" :loading="loading" @click="refreshWorkspace" />
          <Button icon="pi pi-search" :label="t('trade.action.searchItems')" size="small" severity="secondary" @click="openSearch" />
          <Button icon="pi pi-chart-bar" :label="'Market Info'" size="small" severity="secondary" @click="openAnalysis" />
          <Button icon="pi pi-star" :label="'Watchlist'" size="small" severity="secondary" @click="openWatchlist" />
          <Button icon="pi pi-cog" :label="'Admin'" size="small" severity="secondary" @click="openAdmin" />
        </template>

        <div class="trade-summary-strip">
          <div v-for="stat in summaryStats" :key="stat.label" class="trade-summary-strip__item">
            <span>{{ stat.label }}</span>
            <strong>{{ stat.value }}</strong>
          </div>
        </div>

        <div class="workspace-kpi-grid">
          <div class="trade-record-card">
            <dl class="trade-detail-list">
              <template v-for="fact in metaFacts" :key="fact.label">
                <dt>{{ fact.label }}</dt>
                <dd>{{ fact.value }}</dd>
              </template>
            </dl>
          </div>

          <div class="trade-record-card">
            <h3 class="workspace-card-title">Watchlist / Frequent</h3>
            <div class="workspace-inline-stack">
              <div class="workspace-chip-group">
                <span v-for="row in currentWatchlistItems.slice(0, 6)" :key="row.id" class="trade-chip-static">
                  {{ row.itemName }}
                </span>
                <span v-if="!currentWatchlistItems.length" class="trade-chip-static">No watchlist item</span>
              </div>
              <div class="workspace-chip-group">
                <span v-for="row in currentFrequentSearches.slice(0, 6)" :key="row.id" class="trade-chip-static">
                  {{ row.itemName }} / {{ row.searchCount }}
                </span>
                <span v-if="!currentFrequentSearches.length" class="trade-chip-static">No recent search</span>
              </div>
            </div>
          </div>
        </div>
      </TradePanel>

      <div class="trade-workspace-grid">
        <TradePanel :title="'Chart / Indicators / Drawing'" :description="'Minute to yearly intervals share one workspace. Snapshot loads candles and indicators, then delta keeps quote state current.'" >
          <template #actions>
            <SelectButton
              :model-value="workspaceStore.chartInterval"
              :options="intervalOptions"
              option-label="label"
              option-value="value"
              @update:model-value="workspaceStore.setChartInterval($event as TradeChartPeriodType)"
            />
            <InputText :model-value="chartFrom" type="date" @update:model-value="chartFrom = String($event ?? monthAgo)" />
            <InputText :model-value="chartTo" type="date" @update:model-value="chartTo = String($event ?? today)" />
            <Button icon="pi pi-chart-line" :label="'Reload Chart'" size="small" :loading="loadingChart" @click="refreshChartSnapshot" />
          </template>

          <TradeCandleChart :rows="currentChartRows" :indicators="currentIndicators" :drawings="currentDrawings" />

          <div class="trade-summary-strip">
            <div v-for="indicator in indicatorStats" :key="indicator.label" class="trade-summary-strip__item">
              <span>{{ indicator.label }}</span>
              <strong>{{ indicator.value }}</strong>
            </div>
          </div>

          <div class="trade-two-column">
            <div class="trade-record-card">
              <h3 class="workspace-card-title">Drawing Toolbar</h3>
              <div class="trade-form-grid">
                <label class="inline-input">
                  <span>{{ t('trade.label.type') }}</span>
                  <select v-model="drawingForm.drawingType" class="native-select">
                    <option value="UPPER_LINE">{{ t('trade.label.drawingTypeUpper') }}</option>
                    <option value="LOWER_LINE">{{ t('trade.label.drawingTypeLower') }}</option>
                  </select>
                </label>
                <label class="inline-input">
                  <span>{{ t('trade.label.startDate') }}</span>
                  <InputText v-model="drawingForm.startDate" type="date" />
                </label>
                <label class="inline-input">
                  <span>{{ t('trade.label.startPrice') }}</span>
                  <InputNumber v-model="drawingForm.startPrice" :min="0" fluid />
                </label>
                <label class="inline-input">
                  <span>{{ t('trade.label.endDate') }}</span>
                  <InputText v-model="drawingForm.endDate" type="date" />
                </label>
                <label class="inline-input">
                  <span>{{ t('trade.label.endPrice') }}</span>
                  <InputNumber v-model="drawingForm.endPrice" :min="0" fluid />
                </label>
                <label class="inline-input">
                  <span>{{ t('trade.label.memo') }}</span>
                  <InputText v-model="drawingForm.memo" />
                </label>
              </div>
              <div class="trade-inline-actions">
                <Button icon="pi pi-save" :label="t('trade.action.saveDrawing')" size="small" @click="saveDrawing" />
                <Button icon="pi pi-undo" :label="t('trade.action.reset')" size="small" severity="secondary" @click="resetDrawingForm" />
              </div>
            </div>

            <TradeRecordTable :columns="drawingColumns" :rows="drawingRows as unknown as Array<Record<string, unknown>>" row-key="id">
              <template #actions="{ row }">
                <Button text size="small" :label="t('common.edit')" @click="editDrawing(row as unknown as TradeChartDrawing)" />
                <Button text size="small" severity="danger" :label="t('common.delete')" @click="removeDrawing(row as unknown as TradeChartDrawing)" />
              </template>
            </TradeRecordTable>
          </div>
        </TradePanel>

        <TradePanel :title="'Orderbook / Order Flow'" :description="'Click an ask or bid level to fill price and side, then validate and submit. Account delta events patch the same workspace.'" >
          <div class="orderbook-ladder">
            <button
              v-for="level in currentOrderbook?.levels ?? []"
              :key="level.level"
              type="button"
              class="orderbook-ladder__row"
              @click="handleOrderbookPick('SELL', level.askPrice)"
            >
              <span class="orderbook-ladder__level">{{ level.level }}</span>
              <span class="orderbook-ladder__ask">{{ Number(level.askPrice).toLocaleString() }}</span>
              <span class="orderbook-ladder__qty">{{ Number(level.askQuantity).toLocaleString() }}</span>
              <span class="orderbook-ladder__bid">{{ Number(level.bidPrice).toLocaleString() }}</span>
              <span class="orderbook-ladder__qty">{{ Number(level.bidQuantity).toLocaleString() }}</span>
            </button>
          </div>

          <div class="trade-form-grid">
            <label class="inline-input">
              <span>{{ t('trade.market.orderSide') }}</span>
              <select v-model="orderForm.side" class="native-select">
                <option value="BUY">BUY</option>
                <option value="SELL">SELL</option>
              </select>
            </label>
            <label class="inline-input">
              <span>{{ t('trade.label.quantity') }}</span>
              <InputNumber v-model="orderForm.quantity" :min="1" fluid />
            </label>
            <label class="inline-input">
              <span>{{ t('trade.label.price') }}</span>
              <InputNumber v-model="orderForm.price" :min="0" fluid />
            </label>
          </div>

          <div class="trade-inline-actions">
            <Button icon="pi pi-star" :label="t('trade.action.addWatchlist')" size="small" @click="handleAddWatchlist" />
            <Button icon="pi pi-check-circle" :label="t('trade.action.validateOrder')" size="small" severity="secondary" :loading="loadingValidation" @click="handleValidate" />
            <Button icon="pi pi-send" :label="t('trade.action.executeOrder')" size="small" severity="danger" :loading="loadingOrder" @click="requestOrder" />
          </div>

          <div class="trade-live-banner">
            {{ t('trade.market.liveBanner') }}
          </div>

          <div class="trade-summary-strip">
            <div v-for="stat in accountStats" :key="stat.label" class="trade-summary-strip__item">
              <span>{{ stat.label }}</span>
              <strong>{{ stat.value }}</strong>
            </div>
          </div>

          <div v-if="currentOrderable" class="trade-response-panel is-success">
            <dl>
              <dt>{{ t('trade.label.orderableCashAmount') }}</dt>
              <dd>{{ Number(currentOrderable.orderableCashAmount).toLocaleString() }}</dd>
              <dt>{{ t('trade.label.orderableQuantity') }}</dt>
              <dd>{{ Number(currentOrderable.orderableQuantity).toLocaleString() }}</dd>
            </dl>
          </div>

          <div v-if="validationResult" class="trade-response-panel" :class="validationResult.allowed ? 'is-success' : 'is-danger'">
            <dl>
              <dt>{{ t('trade.label.validationResult') }}</dt>
              <dd>{{ validationResult.allowed ? t('trade.market.validationAllowed') : t('trade.market.validationBlocked') }}</dd>
              <dt>{{ t('trade.label.requiredAmount') }}</dt>
              <dd>{{ Number(validationResult.requiredAmount).toLocaleString() }}</dd>
              <dt>{{ t('trade.label.failureReason') }}</dt>
              <dd>{{ validationResult.failureReason || '-' }}</dd>
            </dl>
          </div>

          <div v-if="orderResult" class="trade-response-panel is-success">
            <dl>
              <dt>{{ t('trade.label.orderNo') }}</dt>
              <dd>{{ orderResult.orderNo }}</dd>
              <dt>{{ t('trade.label.branchNo') }}</dt>
              <dd>{{ orderResult.branchNo }}</dd>
              <dt>{{ t('trade.label.responseCode') }}</dt>
              <dd>{{ orderResult.responseCode }}</dd>
              <dt>{{ t('trade.label.message') }}</dt>
              <dd>{{ orderResult.message }}</dd>
            </dl>
          </div>

          <TradeRecordTable :columns="eventFeedColumns" :rows="orderEventRows as unknown as Array<Record<string, unknown>>" row-key="id" />
        </TradePanel>
      </div>

      <div class="trade-workspace-grid trade-workspace-grid--equal">
        <TradePanel :title="'Account Delta / Holdings'" :description="'Selected account snapshot plus realtime balance and position patches stay in the same panel.'" >
          <TradeRecordTable :columns="positionColumns" :rows="currentPositions as unknown as Array<Record<string, unknown>>" row-key="itemCode" />
        </TradePanel>

        <TradePanel :title="'Workspace Lists'" :description="'Watchlist and frequent search items stay visible while changing symbols.'" >
          <div class="workspace-mini-grid">
            <TradeRecordTable :columns="watchlistColumns" :rows="currentWatchlistItems as unknown as Array<Record<string, unknown>>" row-key="id" />
            <TradeRecordTable :columns="frequentColumns" :rows="currentFrequentSearches as unknown as Array<Record<string, unknown>>" row-key="id" />
          </div>
        </TradePanel>
      </div>

      <TradePanel :title="'Market Context'" :description="'Events and rankings stay near the active symbol so the workspace can move from quote to market context without screen switching.'" >
        <template #actions>
          <select v-model="selectedEventTab" class="native-select">
            <option value="ALL">ALL</option>
            <option value="IPO_SUBSCRIPTION">IPO</option>
            <option value="PAR_VALUE_CHANGE">PAR VALUE</option>
            <option value="CORPORATE_ACTION">CORPORATE</option>
          </select>
          <select v-model="rankingType" class="native-select">
            <option value="volume">volume</option>
            <option value="turnover">turnover</option>
            <option value="gainers">gainers</option>
            <option value="losers">losers</option>
            <option value="market-cap">market-cap</option>
            <option value="high52">high52</option>
            <option value="low52">low52</option>
            <option value="volatility">volatility</option>
          </select>
          <select v-model="rankingMasterType" class="native-select">
            <option value="KOSPI">KOSPI</option>
            <option value="KOSDAQ">KOSDAQ</option>
            <option value="KONEX">KONEX</option>
            <option value="ETF_ETN">ETF_ETN</option>
          </select>
          <Button icon="pi pi-refresh" :label="t('common.refresh')" size="small" @click="refreshMarketInfo" />
        </template>

        <div class="trade-two-column">
          <TradeRecordTable :columns="eventColumns" :rows="marketEvents as unknown as Array<Record<string, unknown>>" row-key="id">
            <template #actions="{ row }">
              <Button
                text
                size="small"
                label="Open"
                @click="selectItem(String((row as Record<string, unknown>).itemCode ?? ''), String((row as Record<string, unknown>).title ?? '')); refreshWorkspace()"
              />
            </template>
          </TradeRecordTable>

          <TradeRecordTable :columns="rankingColumns" :rows="rankingTableRows as unknown as Array<Record<string, unknown>>" row-key="rank">
            <template #actions="{ row }">
              <Button
                text
                size="small"
                label="Open"
                @click="selectItem(String((row as Record<string, unknown>).itemCode ?? ''), String((row as Record<string, unknown>).itemName ?? '')); refreshWorkspace()"
              />
            </template>
          </TradeRecordTable>
        </div>
      </TradePanel>
    </template>

    <LiveOrderConfirmPopupView
      :visible="liveOrderPopupVisible"
      :account-no="orderForm.accountNo"
      :item-code="orderForm.itemCode"
      :side="orderForm.side"
      :quantity="Number(orderForm.quantity)"
      :price="Number(orderForm.price)"
      @update:visible="liveOrderPopupVisible = $event"
      @confirm="liveOrderPopupVisible = false; executeOrder(true)"
    />
  </div>
</template>
