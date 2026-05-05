<script setup lang="ts">
import BasePageHeader from '@/components/common/BasePageHeader.vue'
import { useAppI18n } from '@/composables/useAppI18n'

const { t } = useAppI18n()

const cards = [
  { label: 'dashboard.card.master', value: '9', hint: 'dashboard.card.masterHint' },
  { label: 'dashboard.card.inventory', value: '6', hint: 'dashboard.card.inventoryHint' },
  { label: 'dashboard.card.production', value: '4', hint: 'dashboard.card.productionHint' },
  { label: 'dashboard.card.print', value: '6', hint: 'dashboard.card.printHint' },
]

const apiGroups = [
  '/api/erp/items',
  '/api/erp/inventory/balances/search',
  '/api/erp/production/orders/search',
  '/api/erp/prints/barcodes/{barcodeValue}',
]
</script>

<template>
  <div class="page-stack">
    <BasePageHeader
      title="menu.dashboard"
      description="dashboard.description"
    />

    <section class="stat-grid">
      <article v-for="card in cards" :key="card.label" class="base-stat-card">
        <span>{{ t(card.label) }}</span>
        <strong>{{ card.value }}</strong>
        <p>{{ t(card.hint) }}</p>
      </article>
    </section>

    <section class="management-card">
      <div class="management-card__header">
        <div>
          <h2>{{ t('dashboard.apiTitle') }}</h2>
          <p>{{ t('dashboard.apiDescription') }}</p>
        </div>
      </div>
      <div class="erp-record-card-list">
        <article v-for="api in apiGroups" :key="api" class="erp-record-card">
          <strong>{{ api }}</strong>
        </article>
      </div>
    </section>
  </div>
</template>
