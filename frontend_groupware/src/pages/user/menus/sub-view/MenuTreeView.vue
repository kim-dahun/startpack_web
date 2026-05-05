<script setup lang="ts">
import Button from 'primevue/button'
import Column from 'primevue/column'
import TreeTable from 'primevue/treetable'
import type { TreeTableExpandedKeys } from 'primevue/treetable'

import { useAppI18n } from '@/composables/useAppI18n'
import type { UserMenuTreeNode } from '../model/pageModel'

defineProps<{
  nodes: UserMenuTreeNode[]
  expandedKeys: TreeTableExpandedKeys
  canWrite: boolean
}>()

const emit = defineEmits<{
  'update:expandedKeys': [value: TreeTableExpandedKeys]
  addChild: [menuId: string]
  edit: [menuId: string]
  remove: [menuId: string]
  addRoot: []
  refresh: []
}>()

const { t } = useAppI18n()
</script>

<template>
  <section class="tree-panel">
    <header class="split-panel__header">
      <strong>{{ t('menu.menus') }}</strong>
      <div class="split-panel__actions">
        <Button icon="pi pi-plus" :label="t('common.add')" size="small" :disabled="!canWrite" @click="emit('addRoot')" />
        <Button icon="pi pi-refresh" :label="t('common.refresh')" size="small" severity="secondary" @click="emit('refresh')" />
      </div>
    </header>
    <TreeTable
      :value="nodes"
      :expanded-keys="expandedKeys"
      class="menu-admin-tree"
      @update:expanded-keys="emit('update:expandedKeys', $event)"
    >
      <Column field="menuName" :header="t('menu.menus')" expander>
        <template #body="slotProps">
          <div class="tree-node-row__text">
            <strong>{{ slotProps.node.data.menuName }}</strong>
            <span>{{ slotProps.node.data.menuId }}</span>
          </div>
        </template>
      </Column>
      <Column field="menuUrl" header="URL">
        <template #body="slotProps">
          <span>{{ slotProps.node.data.menuUrl || 'group menu' }}</span>
        </template>
      </Column>
      <Column field="menuLevel" header="Level" />
      <Column field="sortSeq" header="Sort" />
      <Column :header="t('common.actions')">
        <template #body="slotProps">
          <div class="tree-node-row__actions">
            <Button
              v-if="slotProps.node.data.menuLevel < 3"
              icon="pi pi-plus"
              text
              rounded
              size="small"
              :disabled="!canWrite"
              @click.stop="emit('addChild', slotProps.node.data.menuId)"
            />
            <Button
              icon="pi pi-pencil"
              text
              rounded
              size="small"
              :disabled="!canWrite"
              @click.stop="emit('edit', slotProps.node.data.menuId)"
            />
            <Button
              icon="pi pi-trash"
              text
              rounded
              severity="danger"
              size="small"
              :disabled="!canWrite"
              @click.stop="emit('remove', slotProps.node.data.menuId)"
            />
          </div>
        </template>
      </Column>
    </TreeTable>
  </section>
</template>
