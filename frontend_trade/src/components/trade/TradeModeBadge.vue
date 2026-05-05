<script setup lang="ts">
import Tag from 'primevue/tag'

import type { TradeMode } from '@/types/trade'

defineProps<{
  tradeMode: TradeMode
}>()
</script>

<template>
  <Tag :value="tradeMode" :severity="tradeMode === 'LIVE' ? 'danger' : 'info'" />
</template>
