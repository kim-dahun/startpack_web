<script setup lang="ts">
import Button from 'primevue/button'
import Tree from 'primevue/tree'
import type { TreeExpandedKeys } from 'primevue/tree'

import { useAppI18n } from '@/composables/useAppI18n'

defineProps<{
  nodes: Array<Record<string, unknown>>
  selectedKey: string
  expandedKeys: TreeExpandedKeys
  canWrite: boolean
  canDelete: boolean
}>()

const emit = defineEmits<{
  select: [departmentId: string]
  'update:expandedKeys': [keys: TreeExpandedKeys]
  create: []
  edit: []
  remove: []
  refresh: []
  openOrgChart: []
}>()

const { t } = useAppI18n()
</script>

<template>
  <section class="split-panel">
    <header class="split-panel__header">
      <strong>{{ t('menu.departments') }}</strong>
      <div class="split-panel__actions">
        <Button icon="pi pi-sitemap" :label="t('departments.tree.openOrgChart')" size="small" severity="secondary" @click="emit('openOrgChart')" />
        <Button icon="pi pi-plus" :label="t('common.add')" size="small" :disabled="!canWrite" @click="emit('create')" />
        <Button icon="pi pi-pencil" :label="t('common.edit')" size="small" severity="secondary" :disabled="!canWrite || !selectedKey" @click="emit('edit')" />
        <Button icon="pi pi-trash" :label="t('common.delete')" size="small" severity="danger" :disabled="!canDelete || !selectedKey" @click="emit('remove')" />
        <Button icon="pi pi-refresh" :label="t('common.refresh')" size="small" severity="secondary" @click="emit('refresh')" />
      </div>
    </header>

    <div class="tree-panel department-tree-panel">
      <Tree
        :value="nodes as any"
        :expanded-keys="expandedKeys"
        selection-mode="single"
        :selection-keys="selectedKey ? { [selectedKey]: true } : {}"
        class="department-tree"
        @update:expanded-keys="emit('update:expandedKeys', $event)"
        @node-select="emit('select', String($event.key ?? ''))"
      >
        <template #default="slotProps">
          <div class="tree-node-row department-tree-node">
            <div class="tree-node-row__text">
              <strong>{{ slotProps.node.label }}</strong>
              <span>{{ slotProps.node.key }}</span>
            </div>
          </div>
        </template>
      </Tree>
    </div>
  </section>
</template>
