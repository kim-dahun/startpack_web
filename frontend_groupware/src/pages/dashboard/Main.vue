<script setup lang="ts">
import { computed } from 'vue'

import BasePageHeader from '@/components/common/BasePageHeader.vue'
import { useAppI18n } from '@/composables/useAppI18n'
import { useRealtimeStore } from '@/stores/realtime'
import { useSessionStore } from '@/stores/session'

import { buildDashboardStats, selectTopQuotes } from './api/api'
import QuoteSummaryView from './sub-view/QuoteSummaryView.vue'
import StatsView from './sub-view/StatsView.vue'

const sessionStore = useSessionStore()
const realtimeStore = useRealtimeStore()
const { t } = useAppI18n()

const stats = computed(() => buildDashboardStats(sessionStore, realtimeStore, t))
const topQuotes = computed(() => selectTopQuotes(realtimeStore.quotes))
</script>

<template>
  <div class="page-stack">
    <BasePageHeader title="dashboard.title" />
    <StatsView :stats="stats" />
    <QuoteSummaryView :quotes="topQuotes" />
  </div>
</template>
