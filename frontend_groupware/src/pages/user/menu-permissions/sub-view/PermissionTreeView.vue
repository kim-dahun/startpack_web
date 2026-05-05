<script setup lang="ts">
import Button from 'primevue/button'
import Checkbox from 'primevue/checkbox'
import Column from 'primevue/column'
import TreeTable from 'primevue/treetable'
import type { TreeTableExpandedKeys } from 'primevue/treetable'

import { useAppI18n } from '@/composables/useAppI18n'
import type { PermissionField, PermissionTreeNode } from '../model/pageModel'

const permissionColumnList: PermissionField[] = ['permitRead', 'permitWrite', 'permitDelete', 'permitExcel']

defineProps<{
  nodes: PermissionTreeNode[]
  expandedKeys: TreeTableExpandedKeys
  disabled: boolean
}>()

const emit = defineEmits<{
  'update:expandedKeys': [value: TreeTableExpandedKeys]
  toggle: [payload: { menuId: string; field: PermissionField; value: boolean }]
  save: []
  refresh: []
}>()

const { t } = useAppI18n()
</script>

<template>
  <section class="tree-panel">
    <header class="split-panel__header">
      <strong>{{ t('menu.menuPermissions') }}</strong>
      <div class="split-panel__actions">
        <Button icon="pi pi-save" :label="t('common.save')" size="small" :disabled="disabled" @click="emit('save')" />
        <Button icon="pi pi-refresh" :label="t('common.refresh')" size="small" severity="secondary" @click="emit('refresh')" />
      </div>
    </header>
    <TreeTable
      :value="nodes"
      :expanded-keys="expandedKeys"
      class="permission-tree"
      @update:expanded-keys="emit('update:expandedKeys', $event)"
    >
      <Column field="menuName" :header="t('menu.menus')" expander>
        <template #body="slotProps">
          <div class="permission-tree-row__menu">
            <strong>{{ slotProps.node.data.menuName }}</strong>
            <span>{{ slotProps.node.data.menuId }}</span>
          </div>
        </template>
      </Column>
      <Column v-for="field in permissionColumnList" :key="field" :header="field.replace('permit', '')">
        <template #body="slotProps">
          <label class="checkbox-line compact">
            <Checkbox
              :model-value="slotProps.node.data[field]"
              binary
              :disabled="disabled"
              @update:model-value="emit('toggle', { menuId: slotProps.node.data.menuId, field, value: Boolean($event) })"
            />
          </label>
        </template>
      </Column>
    </TreeTable>
  </section>
</template>
