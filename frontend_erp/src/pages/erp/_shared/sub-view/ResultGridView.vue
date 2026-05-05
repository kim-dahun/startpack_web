<script setup lang="ts">
import BaseEmptyState from '@/components/common/BaseEmptyState.vue'
import { useAppI18n } from '@/composables/useAppI18n'
import type { ErpResourceDefinition, ErpResourceRow } from '@/types/erp'

defineProps<{
  definition: ErpResourceDefinition
  rows: ErpResourceRow[]
}>()

const { t } = useAppI18n()
</script>

<template>
  <section class="management-card">
    <div class="management-card__header">
      <div>
        <h2>{{ t('erp.result.title') }}</h2>
        <p>{{ t('erp.result.count', undefined, { count: rows.length }) }}</p>
      </div>
    </div>

    <div v-if="rows.length" class="erp-table-shell">
      <table class="erp-record-table">
        <thead>
          <tr>
            <th v-for="column in definition.columns" :key="column.field">{{ column.title }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(row, rowIndex) in rows" :key="String(row.id ?? row.rowId ?? rowIndex)">
            <td v-for="column in definition.columns" :key="column.field">
              {{ row[column.field] ?? '-' }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <BaseEmptyState
      v-else
      title="erp.result.emptyTitle"
      description="erp.result.emptyDescription"
    />
  </section>
</template>
