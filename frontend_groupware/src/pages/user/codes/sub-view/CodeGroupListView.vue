<script setup lang="ts">
import Button from 'primevue/button'

import { useAppI18n } from '@/composables/useAppI18n'
import type { CodeGroupRow } from '../api/api'

defineProps<{
  groups: CodeGroupRow[]
  selectedCodeGroupId: string
  canWrite: boolean
}>()

const emit = defineEmits<{
  select: [codeGroupId: string]
  add: []
  edit: [codeGroupId: string]
  remove: [codeGroupId: string]
  refresh: []
}>()

const { t } = useAppI18n()
</script>

<template>
  <section class="split-panel">
    <header class="split-panel__header">
      <strong>{{ t('menu.codeGroups') }}</strong>
      <div class="split-panel__actions">
        <Button icon="pi pi-plus" :label="t('common.add')" size="small" :disabled="!canWrite" @click="emit('add')" />
        <Button icon="pi pi-pencil" :label="t('common.edit')" size="small" severity="secondary" :disabled="!canWrite || !selectedCodeGroupId" @click="emit('edit', selectedCodeGroupId)" />
        <Button icon="pi pi-trash" :label="t('common.delete')" size="small" severity="danger" :disabled="!canWrite || !selectedCodeGroupId" @click="emit('remove', selectedCodeGroupId)" />
        <Button icon="pi pi-refresh" :label="t('common.refresh')" size="small" severity="secondary" @click="emit('refresh')" />
      </div>
    </header>

    <div class="selection-list">
      <button
        v-for="group in groups"
        :key="group.codeGroupId"
        type="button"
        class="selection-list__item"
        :class="{ 'is-active': group.codeGroupId === selectedCodeGroupId }"
        @click="emit('select', group.codeGroupId)"
      >
        <strong>{{ group.codeGroupName }}</strong>
        <span>{{ group.codeGroupId }}</span>
      </button>
    </div>
  </section>
</template>
