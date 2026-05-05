<script setup lang="ts">
import { computed } from 'vue'

import BasePageHeader from '@/components/common/BasePageHeader.vue'
import { useRealtimeStore } from '@/stores/realtime'
import { useSessionStore } from '@/stores/session'

import { buildDashboardStats, selectTopQuotes } from './api/api'
import QuoteSummaryView from './sub-view/QuoteSummaryView.vue'
import StatsView from './sub-view/StatsView.vue'

const sessionStore = useSessionStore()
const realtimeStore = useRealtimeStore()

const stats = computed(() => buildDashboardStats(sessionStore, realtimeStore))
const topQuotes = computed(() => selectTopQuotes(realtimeStore.quotes))
</script>

<template>
  <div class="page-stack">
    <BasePageHeader title="대시보드" />
    <StatsView :stats="stats" />
    <QuoteSummaryView :quotes="topQuotes" />
  </div>
</template>
