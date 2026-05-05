<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import Button from 'primevue/button'
import Dropdown from 'primevue/dropdown'
import InputText from 'primevue/inputtext'
import { useToast } from 'primevue/usetoast'

import BasePageHeader from '@/components/common/BasePageHeader.vue'
import TradePanel from '@/components/trade/TradePanel.vue'
import TradeRecordTable from '@/components/trade/TradeRecordTable.vue'
import { useAppI18n } from '@/composables/useAppI18n'
import { useTradeWorkspaceStore } from '@/stores/trade/workspace'
import type { TradeMasterImportHistory, TradeMasterStatus, TradeMasterTypeOption, TradeSearchCandidate, TradeMode } from '@/types/trade'

import {
  addWatchlistForCurrentUser,
  fetchMasterImportHistories,
  fetchMasterStatuses,
  fetchMasterTypes,
  fetchSearchCandidates,
  runDefaultMasterImport,
  runMasterDownloadImport,
} from './api/api'

const router = useRouter()
const toast = useToast()
const workspaceStore = useTradeWorkspaceStore()
const { t } = useAppI18n()

const loading = ref(false)
const loadingImport = ref(false)
const statuses = ref<TradeMasterStatus[]>([])
const masterTypes = ref<TradeMasterTypeOption[]>([])
const importHistories = ref<TradeMasterImportHistory[]>([])
const candidates = ref<TradeSearchCandidate[]>([])

const masterForm = reactive({
  keyword: '',
  masterType: '',
  sourceUrl: '',
  sourceVersion: new Date().toISOString().slice(0, 10),
})

const tradeModeOptions = [
  { label: 'LIVE', value: 'LIVE' },
  { label: 'PAPER', value: 'PAPER' },
]

const masterTypeOptions = computed(() =>
  masterTypes.value.map((type) => ({
    label: type.displayName,
    value: type.masterType,
  })),
)

const statusColumns = computed(() => [
  { field: 'masterType', title: t('trade.label.masterType') },
  { field: 'itemCount', title: t('trade.label.itemCount') },
  { field: 'lastImportedAt', title: t('trade.label.lastImportedAt') },
  { field: 'lastSourceFileName', title: t('trade.label.lastSourceFileName') },
  { field: 'lastSourceVersion', title: t('trade.label.lastSourceVersion') },
  { field: 'lastImportSuccess', title: t('trade.label.lastImportSuccess') },
])

const historyColumns = computed(() => [
  { field: 'historyId', title: t('trade.label.historyId') },
  { field: 'masterType', title: t('trade.label.type') },
  { field: 'sourceFileName', title: t('trade.label.lastSourceFileName') },
  { field: 'sourceVersion', title: t('trade.label.sourceVersion') },
  { field: 'importedCount', title: t('trade.label.importedCount') },
  { field: 'finishedAt', title: t('trade.label.finishedAt') },
  { field: 'success', title: t('trade.label.success') },
])

const candidateColumns = computed(() => [
  { field: 'itemCode', title: t('trade.label.itemCode') },
  { field: 'itemName', title: t('trade.label.itemName') },
  { field: 'marketCode', title: t('trade.label.market') },
  { field: 'masterType', title: t('trade.label.masterType') },
  { field: 'currentPrice', title: t('trade.label.currentPrice') },
  { field: 'changeRate', title: t('trade.label.changeRate') },
])

const statusCards = computed(() =>
  statuses.value.map((status) => ({
    label: status.masterType,
    value: Number(status.itemCount ?? 0).toLocaleString(),
  })),
)

const candidateRows = computed(() =>
  candidates.value.map((candidate) => ({
    itemCode: candidate.itemCode,
    itemName: candidate.itemName,
    marketCode: candidate.marketCode,
    masterType: candidate.masterType ?? '-',
    currentPrice: Number(candidate.currentPrice ?? 0).toLocaleString(),
    changeRate: Number(candidate.changeRate ?? 0).toFixed(2),
  })),
)

const historyRows = computed(() =>
  importHistories.value.map((row) => ({
    ...row,
    historyId: row.historyId ?? row.id,
  })),
)

const syncSelectedMasterType = () => {
  if (!masterTypes.value.length) {
    masterForm.masterType = ''
    return
  }

  if (!masterTypes.value.some((type) => type.masterType === masterForm.masterType)) {
    masterForm.masterType = masterTypes.value[0]?.masterType ?? ''
  }
}

const refreshMasterTypes = async () => {
  masterTypes.value = await fetchMasterTypes()
  syncSelectedMasterType()
}

const refreshStatuses = async () => {
  statuses.value = await fetchMasterStatuses()
}

const refreshHistories = async () => {
  importHistories.value = await fetchMasterImportHistories(masterForm.masterType)
}

const refreshAll = async () => {
  loading.value = true
  try {
    await Promise.allSettled([refreshMasterTypes(), refreshStatuses()])
    await refreshHistories()
  } finally {
    loading.value = false
  }
}

const searchCandidates = async () => {
  const rows = await fetchSearchCandidates(masterForm.keyword, workspaceStore.tradeMode)
  candidates.value = rows.filter((row) => !masterForm.masterType || row.masterType === masterForm.masterType)
}

const handleDefaultImport = async () => {
  loadingImport.value = true
  try {
    await runDefaultMasterImport()
    await refreshAll()
    toast.add({
      severity: 'success',
      summary: t('trade.masters.defaultImportTitle'),
      detail: t('trade.masters.defaultImportTitle'),
      life: 2200,
    })
  } finally {
    loadingImport.value = false
  }
}

const handleDownloadImport = async () => {
  loadingImport.value = true
  try {
    await runMasterDownloadImport({
      masterType: masterForm.masterType,
      sourceUrl: masterForm.sourceUrl || null,
      sourceVersion: masterForm.sourceVersion,
    })
    await refreshAll()
    toast.add({
      severity: 'success',
      summary: t('trade.masters.downloadImportTitle'),
      detail: t('trade.masters.downloadImportDetail', undefined, { masterType: masterForm.masterType }),
      life: 2200,
    })
  } finally {
    loadingImport.value = false
  }
}

const addWatchlist = async (row: { itemCode: string; itemName: string }) => {
  await addWatchlistForCurrentUser({
    itemCode: row.itemCode,
    itemName: row.itemName,
  })
  toast.add({
    severity: 'success',
    summary: t('trade.watchlist.savedTitle'),
    detail: t('trade.market.watchlistAddedDetail', undefined, { itemName: row.itemName }),
    life: 2200,
  })
}

const moveToQuote = async (itemCode: string) => {
  workspaceStore.setSelectedItemCode(itemCode)
  await router.push({
    name: 'realtime',
    query: { itemCode },
  })
}

onMounted(async () => {
  await refreshAll()
})
</script>

<template>
  <div class="page-stack">
    <BasePageHeader :title="t('trade.masters.pageTitle')" :description="t('trade.masters.pageDescription')" />

    <TradePanel :title="t('trade.masters.statusTitle')" :description="t('trade.masters.statusDescription')">
      <template #actions>
        <Dropdown
          :model-value="workspaceStore.tradeMode"
          :options="tradeModeOptions"
          option-label="label"
          option-value="value"
          class="trade-panel__dropdown"
          @update:model-value="workspaceStore.setTradeMode($event as TradeMode)"
        />
        <Button icon="pi pi-refresh" :label="t('trade.action.refreshMasters')" size="small" severity="secondary" :loading="loading" @click="refreshAll" />
<!--        <Button icon="pi pi-download" :label="t('trade.action.defaultImport')" size="small" :loading="loadingImport" @click="handleDefaultImport" />-->
      </template>

      <div class="trade-summary-strip">
        <div v-for="stat in statusCards" :key="stat.label" class="trade-summary-strip__item">
          <span>{{ stat.label }}</span>
          <strong>{{ stat.value }}</strong>
        </div>
      </div>

      <TradeRecordTable :columns="statusColumns" :rows="statuses as unknown as Array<Record<string, unknown>>" row-key="masterType" />
    </TradePanel>

    <TradePanel :title="t('trade.masters.downloadTitle')" :description="t('trade.masters.downloadDescription')">
      <div class="trade-form-grid">
        <label class="inline-input">
          <span>{{ t('trade.label.masterType') }}</span>
          <Dropdown v-model="masterForm.masterType" :options="masterTypeOptions" option-label="label" option-value="value" />
        </label>
        <label class="inline-input">
          <span>{{ t('trade.label.sourceUrl') }}</span>
          <InputText v-model="masterForm.sourceUrl" :placeholder="t('trade.masters.sourceUrlPlaceholder')" />
        </label>
        <label class="inline-input">
          <span>{{ t('trade.label.sourceVersion') }}</span>
          <InputText v-model="masterForm.sourceVersion" />
        </label>
      </div>

      <div class="trade-inline-actions">
        <Button icon="pi pi-cloud-download" :label="t('trade.action.downloadImport')" size="small" :loading="loadingImport" @click="handleDownloadImport" />
      </div>
    </TradePanel>

    <TradePanel :title="t('trade.masters.searchTitle')" :description="t('trade.masters.searchDescription')">
      <template #actions>
        <Dropdown
          :model-value="masterForm.masterType"
          :options="masterTypeOptions"
          option-label="label"
          option-value="value"
          class="trade-panel__dropdown"
          @update:model-value="masterForm.masterType = String($event ?? '')"
        />
      </template>

      <div class="trade-form-grid">
        <label class="inline-input trade-filter-form__wide">
          <span>{{ t('trade.action.searchItems') }}</span>
          <InputText v-model="masterForm.keyword" :placeholder="t('trade.masters.keywordPlaceholder')" @keyup.enter="searchCandidates" />
        </label>
      </div>

      <div class="trade-inline-actions">
        <Button icon="pi pi-search" :label="t('trade.action.searchCandidates')" size="small" @click="searchCandidates" />
      </div>

      <TradeRecordTable :columns="candidateColumns" :rows="candidateRows" row-key="itemCode">
        <template #actions="{ row }">
          <Button text size="small" :label="t('trade.action.openQuote')" @click="moveToQuote((row as { itemCode: string }).itemCode)" />
          <Button text size="small" :label="t('trade.action.addWatchlist')" @click="addWatchlist(row as { itemCode: string; itemName: string })" />
        </template>
      </TradeRecordTable>
    </TradePanel>

    <TradePanel :title="t('trade.masters.historyTitle')" :description="t('trade.masters.historyDescription')">
      <TradeRecordTable :columns="historyColumns" :rows="historyRows as unknown as Array<Record<string, unknown>>" row-key="historyId" />
    </TradePanel>
  </div>
</template>
