<script setup lang="ts">
import BaseDataGrid from '@/components/grid/BaseDataGrid.vue'
import type { CrudPayload, DataGridColumn, PermissionFlags } from '@/types/app'

defineProps<{
  caption: string
  columns: DataGridColumn[]
  rows: Array<Record<string, unknown>>
  permissions: PermissionFlags
  rowDefaults: Record<string, unknown>
}>()

const emit = defineEmits<{
  save: [payload: CrudPayload<Record<string, unknown>>]
}>()
</script>

<template>
  <BaseDataGrid
    :caption="caption"
    :columns="columns"
    :rows="rows"
    :permissions="permissions"
    :row-defaults="rowDefaults"
    @save="emit('save', $event)"
  />
</template>
