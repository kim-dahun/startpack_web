<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import Button from 'primevue/button'

import BasePageHeader from '@/components/common/BasePageHeader.vue'
import TradePanel from '@/components/trade/TradePanel.vue'
import TradeRecordTable from '@/components/trade/TradeRecordTable.vue'
import { useAppI18n } from '@/composables/useAppI18n'
import type { TradeKisCallLog, TradeRealtimeStatus, TradeReconnectHistory } from '@/types/trade'

import { fetchKisCallLogs, fetchRealtimeStatus, fetchReconnectHistories } from './api/api'

const { t } = useAppI18n()
const status = ref<TradeRealtimeStatus | null>(null)
const reconnectHistories = ref<TradeReconnectHistory[]>([])
const callLogs = ref<TradeKisCallLog[]>([])

const reconnectColumns = computed(() => [
  { field: 'attemptedAt', title: t('trade.label.attemptedAt') },
  { field: 'success', title: t('trade.label.success') },
  { field: 'subscriptionCount', title: t('trade.label.subscriptionCount') },
  { field: 'failureReason', title: t('trade.label.failureReason') },
])

const callLogColumns = computed(() => [
  { field: 'calledAt', title: t('trade.label.calledAt') },
  { field: 'endpoint', title: t('trade.label.endpoint') },
  { field: 'method', title: t('trade.label.method') },
  { field: 'statusCode', title: t('trade.label.statusCode') },
  { field: 'elapsedMillis', title: t('trade.label.elapsedMillis') },
])

const refreshAll = async () => {
  const [nextStatus, nextReconnect, nextCallLogs] = await Promise.allSettled([
    fetchRealtimeStatus(),
    fetchReconnectHistories({ limit: 20 }),
    fetchKisCallLogs(),
  ])
  status.value = nextStatus.status === 'fulfilled' ? nextStatus.value : null
  reconnectHistories.value = nextReconnect.status === 'fulfilled' ? nextReconnect.value : []
  callLogs.value = nextCallLogs.status === 'fulfilled' ? nextCallLogs.value : []
}

onMounted(async () => {
  await refreshAll()
})
</script>

<template>
  <div class="page-stack">
    <BasePageHeader title="menu.ops" :description="t('trade.ops.pageDescription')" />

    <TradePanel :title="t('trade.ops.statusTitle')" :description="t('trade.ops.statusDescription')">
      <template #actions>
        <Button icon="pi pi-refresh" :label="t('trade.action.refreshOps')" size="small" @click="refreshAll" />
      </template>
      <div class="trade-summary-strip">
        <div class="trade-summary-strip__item">
          <span>{{ t('trade.ops.kisConnection') }}</span>
          <strong>{{ status?.kisConnected ? t('trade.status.connected') : t('trade.status.disconnected') }}</strong>
        </div>
        <div class="trade-summary-strip__item">
          <span>{{ t('trade.label.sessionCount') }}</span>
          <strong>{{ status?.sessionCount ?? 0 }}</strong>
        </div>
        <div class="trade-summary-strip__item">
          <span>{{ t('trade.label.subscriptionCount') }}</span>
          <strong>{{ status?.subscriptionCount ?? 0 }}</strong>
        </div>
        <div class="trade-summary-strip__item">
          <span>{{ t('trade.label.cachedEventCount') }}</span>
          <strong>{{ status?.cachedEventCount ?? 0 }}</strong>
        </div>
      </div>
    </TradePanel>

    <div class="trade-two-column">
      <TradePanel :title="t('trade.ops.reconnectTitle')" :description="t('trade.ops.reconnectDescription')">
        <TradeRecordTable :columns="reconnectColumns" :rows="reconnectHistories as unknown as Array<Record<string, unknown>>" row-key="id" />
      </TradePanel>

      <TradePanel :title="t('trade.ops.callLogTitle')" :description="t('trade.ops.callLogDescription')">
        <TradeRecordTable :columns="callLogColumns" :rows="callLogs as unknown as Array<Record<string, unknown>>" row-key="id" />
      </TradePanel>
    </div>
  </div>
</template>
