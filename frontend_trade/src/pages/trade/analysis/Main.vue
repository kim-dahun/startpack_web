<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import Button from 'primevue/button'

import BasePageHeader from '@/components/common/BasePageHeader.vue'
import TradePanel from '@/components/trade/TradePanel.vue'
import TradeRecordTable from '@/components/trade/TradeRecordTable.vue'
import { useAppI18n } from '@/composables/useAppI18n'
import type { TradeEventItem, TradeRankingRow, TradeRankingType } from '@/types/trade'

import {
  fetchCorporateActions,
  fetchIpoSubscriptions,
  fetchParValueChanges,
  fetchRankingRows,
  fetchSectorRows,
  fetchThemeRows,
  fetchTradeEvents,
} from './api/api'

const { t } = useAppI18n()

const selectedEventTab = ref<'ALL' | 'IPO_SUBSCRIPTION' | 'PAR_VALUE_CHANGE' | 'CORPORATE_ACTION'>('ALL')
const rankingType = ref<TradeRankingType>('volume')
const rankingMasterType = ref('KOSPI')

const eventRows = ref<TradeEventItem[]>([])
const rankingRows = ref<TradeRankingRow[]>([])
const sectorRows = ref<Record<string, unknown>[]>([])
const themeRows = ref<Record<string, unknown>[]>([])

const eventColumns = computed(() => [
  { field: 'eventType', title: t('trade.analysis.eventType') },
  { field: 'itemCode', title: t('trade.label.itemCode') },
  { field: 'title', title: t('trade.analysis.title') },
  { field: 'eventDate', title: t('trade.analysis.date') },
  { field: 'description', title: t('trade.analysis.description') },
])

const rankingColumns = computed(() => [
  { field: 'rank', title: t('trade.analysis.rank') },
  { field: 'itemCode', title: t('trade.label.itemCode') },
  { field: 'itemName', title: t('trade.label.itemName') },
  { field: 'marketCode', title: t('trade.label.market') },
  { field: 'sectorName', title: t('trade.label.sector') },
  { field: 'metricValue', title: t('trade.analysis.metricValue') },
])

const groupStrengthColumns = computed(() => [
  { field: 'groupName', title: t('trade.analysis.groupName') },
  { field: 'itemCount', title: t('trade.analysis.itemCount') },
  { field: 'strengthScore', title: t('trade.analysis.strengthScore') },
])

const refreshEvents = async () => {
  if (selectedEventTab.value === 'IPO_SUBSCRIPTION') {
    eventRows.value = await fetchIpoSubscriptions()
    return
  }

  if (selectedEventTab.value === 'PAR_VALUE_CHANGE') {
    eventRows.value = await fetchParValueChanges()
    return
  }

  if (selectedEventTab.value === 'CORPORATE_ACTION') {
    eventRows.value = await fetchCorporateActions()
    return
  }

  eventRows.value = await fetchTradeEvents({})
}

const refreshRanking = async () => {
  const [nextRankings, nextSectors, nextThemes] = await Promise.allSettled([
    fetchRankingRows(rankingType.value, rankingMasterType.value),
    fetchSectorRows(),
    fetchThemeRows(),
  ])

  rankingRows.value = nextRankings.status === 'fulfilled' ? nextRankings.value : []
  sectorRows.value = nextSectors.status === 'fulfilled' ? nextSectors.value : []
  themeRows.value = nextThemes.status === 'fulfilled' ? nextThemes.value : []
}

onMounted(async () => {
  await Promise.all([refreshEvents(), refreshRanking()])
})
</script>

<template>
  <div class="page-stack">
    <BasePageHeader title="menu.analysis" :description="t('trade.analysis.pageDescription')" />

    <div class="trade-two-column">
      <TradePanel :title="t('trade.analysis.eventsTitle')" :description="t('trade.analysis.eventsDescription')">
        <template #actions>
          <select v-model="selectedEventTab" class="native-select">
            <option value="ALL">{{ t('trade.analysis.tabAll') }}</option>
            <option value="IPO_SUBSCRIPTION">{{ t('trade.analysis.tabIpo') }}</option>
            <option value="PAR_VALUE_CHANGE">{{ t('trade.analysis.tabParValue') }}</option>
            <option value="CORPORATE_ACTION">{{ t('trade.analysis.tabCorporateAction') }}</option>
          </select>
          <Button icon="pi pi-refresh" :label="t('trade.action.refreshEvents')" size="small" @click="refreshEvents" />
        </template>
        <TradeRecordTable :columns="eventColumns" :rows="eventRows as unknown as Array<Record<string, unknown>>" row-key="id" />
      </TradePanel>

      <TradePanel :title="t('trade.analysis.rankingTitle')" :description="t('trade.analysis.rankingDescription')">
        <template #actions>
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
          <Button icon="pi pi-sort-amount-down" :label="t('trade.action.refreshRanking')" size="small" @click="refreshRanking" />
        </template>
        <TradeRecordTable :columns="rankingColumns" :rows="rankingRows as unknown as Array<Record<string, unknown>>" row-key="rank" />
      </TradePanel>
    </div>

    <TradePanel :title="t('trade.analysis.groupTitle')" :description="t('trade.analysis.groupDescription')">
      <div class="trade-two-column trade-two-column--scroll">
        <TradeRecordTable
          :columns="groupStrengthColumns"
          :rows="sectorRows as unknown as Array<Record<string, unknown>>"
          row-key="groupName"
        />
        <TradeRecordTable
          :columns="groupStrengthColumns"
          :rows="themeRows as unknown as Array<Record<string, unknown>>"
          row-key="groupName"
        />
      </div>
    </TradePanel>
  </div>
</template>
