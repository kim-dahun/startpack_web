<script setup lang="ts">
import { computed } from 'vue'
import { useBreakpoints } from '@vueuse/core'

import { useAppI18n } from '@/composables/useAppI18n'

const props = defineProps<{
  columns: Array<{ field: string; title: string }>
  rows: Array<Record<string, unknown>>
  rowKey?: string
}>()

const breakpoints = useBreakpoints({
  mobile: 0,
  desktop: 920,
})
const { t } = useAppI18n()
const isMobile = breakpoints.smaller('desktop')
const resolvedRowKey = computed(() => props.rowKey ?? props.columns[0]?.field ?? 'id')
const resolveRowKey = (row: Record<string, unknown>, index: number) => String(row[resolvedRowKey.value] ?? index)
</script>

<template>
  <div class="trade-record-table-region">
    <div v-if="isMobile" class="trade-record-card-list">
      <article
        v-for="(row, index) in rows"
        :key="resolveRowKey(row, index)"
        class="trade-record-card"
      >
        <dl>
          <template v-for="column in columns" :key="column.field">
            <dt>{{ t(column.title, column.title) }}</dt>
            <dd>{{ row[column.field] ?? '-' }}</dd>
          </template>
        </dl>
        <slot name="card-actions" :row="row" />
      </article>
    </div>
    <div v-else class="trade-table-shell">
      <table class="trade-record-table">
        <thead>
          <tr>
            <th v-for="column in columns" :key="column.field">{{ t(column.title, column.title) }}</th>
            <th v-if="$slots.actions">{{ t('common.actions') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(row, index) in rows" :key="resolveRowKey(row, index)">
            <td v-for="column in columns" :key="column.field">{{ row[column.field] ?? '-' }}</td>
            <td v-if="$slots.actions" class="trade-record-table__actions">
              <slot name="actions" :row="row" />
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
