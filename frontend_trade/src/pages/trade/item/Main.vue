<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import AutoComplete from 'primevue/autocomplete'
import Button from 'primevue/button'
import Dropdown from 'primevue/dropdown'
import InputText from 'primevue/inputtext'
import { useToast } from 'primevue/usetoast'

import BaseEmptyState from '@/components/common/BaseEmptyState.vue'
import BasePageHeader from '@/components/common/BasePageHeader.vue'
import TradePanel from '@/components/trade/TradePanel.vue'
import TradeRecordTable from '@/components/trade/TradeRecordTable.vue'
import { useAppI18n } from '@/composables/useAppI18n'
import { useTradeWorkspaceStore } from '@/stores/trade/workspace'
import type {
  TradeItemMetrics,
  TradeItemPrice,
  TradeMode,
  TradeSearchCandidate,
  TradeItemSummary,
} from '@/types/trade'

import {
  addWatchlistForCurrentUser,
  createFrequentSearchForCurrentUser,
  fetchAutocompleteItems,
  fetchItemMetrics,
  fetchItemQuote,
  fetchItemQuotes,
  fetchItems,
} from './api/api'

const toast = useToast()
const router = useRouter()
const workspaceStore = useTradeWorkspaceStore()
const { t } = useAppI18n()

const keyword = ref('')
const selectedCandidate = ref<TradeSearchCandidate | null>(null)
const autocompleteItems = ref<TradeSearchCandidate[]>([])
const quoteMap = ref<Record<string, TradeItemPrice>>({})
const searchRows = ref<TradeItemSummary[]>([])
const selectedMetrics = ref<TradeItemMetrics | null>(null)
const selectedQuote = ref<TradeItemPrice | null>(null)
const loading = ref(false)
const loadingSearch = ref(false)
const loadingDetail = ref(false)

const asArray = <T>(value: T[] | null | undefined) => (Array.isArray(value) ? value : [])
const asRecord = <T extends Record<string, unknown>>(value: T | null | undefined) =>
  (value && typeof value === 'object' && !Array.isArray(value) ? value : {} as T)

const tradeModeOptions = [
  { label: 'LIVE', value: 'LIVE' },
  { label: 'PAPER', value: 'PAPER' },
]

const tableColumns = computed(() => [
  { field: 'itemCode', title: t('trade.label.itemCode') },
  { field: 'itemName', title: t('trade.label.itemName') },
  { field: 'marketCode', title: t('trade.label.market') },
  { field: 'currentPrice', title: t('trade.label.currentPrice') },
  { field: 'changeRate', title: t('trade.label.changeRate') },
])

const previewColumns = computed(() => [
  { field: 'itemCode', title: t('trade.label.itemCode') },
  { field: 'itemName', title: t('trade.label.itemName') },
  { field: 'marketCode', title: t('trade.label.market') },
  { field: 'currentPrice', title: t('trade.label.currentPrice') },
  { field: 'changeRate', title: t('trade.label.changeRate') },
])

const previewRows = computed(() =>
  autocompleteItems.value.map((item) => ({
    itemCode: item.itemCode,
    itemName: item.itemName,
    marketCode: item.marketCode,
    currentPrice: Number(quoteMap.value[item.itemCode]?.currentPrice ?? item.currentPrice ?? 0).toLocaleString(),
    changeRate: Number(quoteMap.value[item.itemCode]?.changeRate ?? item.changeRate ?? 0).toFixed(2),
  })),
)

const searchTableRows = computed(() =>
  searchRows.value.map((item) => ({
    itemCode: item.itemCode,
    itemName: item.itemName,
    marketCode: item.marketCode,
    currentPrice: Number(quoteMap.value[item.itemCode]?.currentPrice ?? 0).toLocaleString(),
    changeRate: Number(quoteMap.value[item.itemCode]?.changeRate ?? 0).toFixed(2),
  })),
)

const selectedStats = computed(() => {
  if (!selectedMetrics.value && !selectedQuote.value) {
    return []
  }

  return [
    { label: t('trade.label.currentPrice'), value: Number(selectedQuote.value?.currentPrice ?? 0).toLocaleString() },
    { label: t('trade.label.changeAmount'), value: Number(selectedQuote.value?.changeAmount ?? 0).toLocaleString() },
    { label: t('trade.label.changeRate'), value: `${Number(selectedQuote.value?.changeRate ?? 0).toFixed(2)}%` },
    { label: t('trade.label.high52Week'), value: Number(selectedMetrics.value?.high52WeekPrice ?? 0).toLocaleString() },
    { label: t('trade.label.low52Week'), value: Number(selectedMetrics.value?.low52WeekPrice ?? 0).toLocaleString() },
    { label: t('trade.label.marketCap'), value: Number(selectedMetrics.value?.marketCap ?? 0).toLocaleString() },
  ]
})

const selectedFacts = computed(() => [
  { label: t('trade.label.market'), value: selectedMetrics.value?.marketCode ?? '-' },
  { label: t('trade.label.sector'), value: selectedMetrics.value?.sectorName ?? '-' },
  { label: 'PER', value: selectedMetrics.value?.per ?? '-' },
  { label: 'PBR', value: selectedMetrics.value?.pbr ?? '-' },
  { label: 'EPS', value: selectedMetrics.value?.eps ?? '-' },
  { label: 'BPS', value: selectedMetrics.value?.bps ?? '-' },
  { label: t('trade.label.salesAmount'), value: selectedMetrics.value?.salesAmount ?? '-' },
  { label: t('trade.label.operatingProfit'), value: selectedMetrics.value?.operatingProfit ?? '-' },
])

const syncSelectedItem = (itemCode: string) => {
  workspaceStore.setSelectedItem(itemCode)
}

const loadSelectedDetail = async (itemCode: string) => {
  loadingDetail.value = true
  try {
    const [metrics, quote] = await Promise.allSettled([
      fetchItemMetrics(itemCode, workspaceStore.tradeMode),
      fetchItemQuote(itemCode, workspaceStore.tradeMode),
    ])

    selectedMetrics.value = metrics.status === 'fulfilled' ? metrics.value : null
    selectedQuote.value = quote.status === 'fulfilled' ? quote.value : null
  } finally {
    loadingDetail.value = false
  }
}

const searchAutocomplete = async (event: { query: string }) => {
  const nextKeyword = event.query.trim()
  keyword.value = nextKeyword

  if (!nextKeyword) {
    autocompleteItems.value = []
    quoteMap.value = {}
    return
  }

  const result = await fetchAutocompleteItems(nextKeyword, workspaceStore.tradeMode)
  autocompleteItems.value = Object.values(asRecord(result))

  if (autocompleteItems.value.length) {
    quoteMap.value = await fetchItemQuotes(
      autocompleteItems.value.map((item) => item.itemCode),
      workspaceStore.tradeMode,
    )
  } else {
    quoteMap.value = {}
  }
}

const runSearch = async () => {
  loadingSearch.value = true
  try {
    const items = asArray(await fetchItems(keyword.value, workspaceStore.tradeMode))
    searchRows.value = items
    if (items.length) {
      quoteMap.value = await fetchItemQuotes(
        items.map((item) => item.itemCode),
        workspaceStore.tradeMode,
      )
    } else {
      quoteMap.value = {}
    }
  } finally {
    loadingSearch.value = false
  }
}

const selectItem = async (itemCode: string, itemName?: string, marketCode?: string) => {
  workspaceStore.setSelectedItem(itemCode, itemName ?? '')
  if (itemName && marketCode) {
    await createFrequentSearchForCurrentUser({ itemCode, itemName, marketCode }).catch(() => undefined)
  }
  await loadSelectedDetail(itemCode)
}

const handleCandidateSelect = async () => {
  if (!selectedCandidate.value) {
    return
  }

  await selectItem(
    selectedCandidate.value.itemCode,
    selectedCandidate.value.itemName,
    selectedCandidate.value.marketCode,
  )
}

const goToQuote = async (row: { itemCode: string; itemName?: string; marketCode?: string }) => {
  await selectItem(row.itemCode, row.itemName, row.marketCode)
  await router.push({
    name: 'realtime',
    query: { itemCode: row.itemCode },
  })
}

const addWatchlist = async (row: { itemCode: string; itemName: string }) => {
  await addWatchlistForCurrentUser({
    itemCode: row.itemCode,
    itemName: row.itemName,
  })
  toast.add({
    severity: 'success',
    summary: t('trade.item.watchlistAddedTitle'),
    detail: t('trade.item.watchlistAddedDetail', undefined, { itemName: row.itemName }),
    life: 2200,
  })
}

const openMasterAdmin = async () => {
  await router.push({ name: 'masters' })
}

const refreshInitial = async () => {
  loading.value = true
  try {
    if (workspaceStore.selectedItemCode) {
      await loadSelectedDetail(workspaceStore.selectedItemCode)
    } else {
      selectedMetrics.value = null
      selectedQuote.value = null
    }
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await refreshInitial()
})
</script>

<template>
  <div class="page-stack">
    <BasePageHeader title="menu.items" :description="t('trade.item.pageDescription')" />

    <TradePanel :title="t('trade.item.searchTitle')" :description="t('trade.item.searchDescription')">
      <template #actions>
        <Dropdown
          :model-value="workspaceStore.tradeMode"
          :options="tradeModeOptions"
          option-label="label"
          option-value="value"
          class="trade-panel__dropdown"
          @update:model-value="workspaceStore.setTradeMode($event as TradeMode)"
        />
        <Button icon="pi pi-database" :label="t('trade.action.openMasterAdmin')" size="small" severity="secondary" @click="openMasterAdmin" />
      </template>

      <div class="trade-form-grid">
        <label class="inline-input trade-filter-form__wide">
          <span>{{ t('trade.item.autocomplete') }}</span>
          <AutoComplete
            v-model="selectedCandidate"
            :suggestions="autocompleteItems"
            option-label="itemName"
            dropdown
            force-selection
            :placeholder="t('trade.item.autocompletePlaceholder')"
            @complete="searchAutocomplete"
            @item-select="handleCandidateSelect"
          >
            <template #option="{ option }">
              <div class="selection-list__item">
                <strong>{{ option.itemName }}</strong>
                <span>{{ option.itemCode }} / {{ option.marketCode }}</span>
              </div>
            </template>
          </AutoComplete>
        </label>
        <label class="inline-input">
          <span>{{ t('trade.item.keywordSearch') }}</span>
          <InputText v-model="keyword" :placeholder="t('trade.item.keywordPlaceholder')" @keyup.enter="runSearch" />
        </label>
      </div>

      <div class="trade-inline-actions">
        <Button icon="pi pi-search" :label="t('search.button')" size="small" :loading="loadingSearch" @click="runSearch" />
      </div>

      <TradeRecordTable :columns="previewColumns" :rows="previewRows" row-key="itemCode">
        <template #actions="{ row }">
          <Button text size="small" :label="t('trade.action.openQuote')" @click="goToQuote(row as { itemCode: string; itemName?: string; marketCode?: string })" />
          <Button text size="small" :label="t('trade.action.addWatchlist')" @click="addWatchlist(row as { itemCode: string; itemName: string })" />
        </template>
      </TradeRecordTable>
    </TradePanel>

    <TradePanel :title="t('trade.item.resultsTitle')" :description="t('trade.item.resultsDescription')">
      <TradeRecordTable :columns="tableColumns" :rows="searchTableRows" row-key="itemCode">
        <template #actions="{ row }">
          <Button text size="small" :label="t('trade.action.openQuote')" @click="goToQuote(row as { itemCode: string; itemName?: string; marketCode?: string })" />
          <Button text size="small" :label="t('trade.action.addWatchlist')" @click="addWatchlist(row as { itemCode: string; itemName: string })" />
        </template>
      </TradeRecordTable>
    </TradePanel>

    <BaseEmptyState
      v-if="!workspaceStore.selectedItemCode"
      :title="t('trade.item.emptyTitle')"
      :description="t('trade.item.emptyDescription')"
    />

    <TradePanel v-else :title="t('trade.item.selectedTitle')" :description="t('trade.item.selectedDescription')">
      <template #actions>
        <Button icon="pi pi-arrow-right" :label="t('trade.action.openQuoteChart')" size="small" :loading="loading" @click="goToQuote({ itemCode: workspaceStore.selectedItemCode })" />
      </template>

      <div class="trade-summary-strip">
        <div v-for="stat in selectedStats" :key="stat.label" class="trade-summary-strip__item">
          <span>{{ stat.label }}</span>
          <strong>{{ stat.value }}</strong>
        </div>
      </div>

      <div class="trade-record-card">
        <dl class="trade-detail-list">
          <template v-for="fact in selectedFacts" :key="fact.label">
            <dt>{{ fact.label }}</dt>
            <dd>{{ fact.value }}</dd>
          </template>
        </dl>
      </div>
    </TradePanel>
  </div>
</template>
