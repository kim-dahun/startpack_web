<script setup lang="ts">
import { formatCurrency } from '@/utils/formatUtils'

defineProps<{
  quotes: Array<{ symbol: string; price: number; changeRate: number }>
}>()
</script>

<template>
  <section class="info-panel">
    <h2>실시간 요약</h2>
    <div class="quote-grid">
      <article v-for="quote in quotes" :key="quote.symbol" class="quote-card">
        <span>{{ quote.symbol }}</span>
        <strong>{{ formatCurrency(quote.price, quote.symbol === 'AAPL' ? 'USD' : 'KRW') }}</strong>
        <small>{{ quote.changeRate > 0 ? '+' : '' }}{{ quote.changeRate.toFixed(2) }}%</small>
      </article>
    </div>
  </section>
</template>
