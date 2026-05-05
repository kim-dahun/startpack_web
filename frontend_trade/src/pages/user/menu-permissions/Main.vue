<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useToast } from 'primevue/usetoast'
import type { TreeTableExpandedKeys } from 'primevue/treetable'

import BaseEmptyState from '@/components/common/BaseEmptyState.vue'
import BaseForbiddenState from '@/components/common/BaseForbiddenState.vue'
import BasePageHeader from '@/components/common/BasePageHeader.vue'
import { useAppI18n } from '@/composables/useAppI18n'
import { useSessionStore } from '@/stores/session'
import type { CrudPayload } from '@/types/app'
import { listGroups, listPermissionTree, savePermissionTree, type GroupRow, type MenuPermissionTreeRow } from './api/api'
import { buildPermissionTreeNodes, clonePermissionTree, collectExpandedKeys, filterPermissionTree, flattenPermissionTree, permissionFields, type PermissionField } from './model/pageModel'
import GroupListView from './sub-view/GroupListView.vue'
import PermissionTreeView from './sub-view/PermissionTreeView.vue'
import SearchView from './sub-view/SearchView.vue'

const sessionStore = useSessionStore()
const toast = useToast()
const { t } = useAppI18n()

const comCd = computed(() => sessionStore.persisted.user?.comCd ?? 'COM001')
const permissions = computed(() => sessionStore.getPermissions('MENU_PERMISSIONS'))
const serviceOptions = computed(() => sessionStore.adminServiceIds)

const selectedServiceId = ref(serviceOptions.value[0] ?? 'TRADE')
const selectedGroupId = ref('')
const keyword = ref('')
const loading = ref(false)
const saveLoading = ref(false)
const groups = ref<GroupRow[]>([])
const permissionRows = ref<MenuPermissionTreeRow[]>([])
const originalRows = ref<MenuPermissionTreeRow[]>([])
const expandedKeys = ref<TreeTableExpandedKeys>({})

const filteredPermissionRows = computed(() => filterPermissionTree(permissionRows.value, keyword.value))
const treeNodes = computed(() => buildPermissionTreeNodes(filteredPermissionRows.value))

const loadGroups = async () => {
  groups.value = await listGroups({
    comCd: comCd.value,
    serviceId: selectedServiceId.value,
  })

  if (!groups.value.some((group) => group.groupId === selectedGroupId.value)) {
    selectedGroupId.value = groups.value[0]?.groupId ?? ''
  }
}

const loadPermissionTree = async () => {
  if (!selectedGroupId.value) {
    permissionRows.value = []
    originalRows.value = []
    return
  }

  loading.value = true

  try {
    const rows = await listPermissionTree({
      comCd: comCd.value,
      serviceId: selectedServiceId.value,
      groupId: selectedGroupId.value,
    })
    permissionRows.value = clonePermissionTree(rows)
    originalRows.value = clonePermissionTree(rows)
    expandedKeys.value = collectExpandedKeys(rows)
  } finally {
    loading.value = false
  }
}

const cascadeDown = (rows: MenuPermissionTreeRow[], menuId: string, field: PermissionField, value: boolean): boolean => {
  for (const row of rows) {
    if (row.menuId === menuId) {
      const apply = (target: MenuPermissionTreeRow) => {
        target[field] = value
        target.children?.forEach(apply)
      }

      apply(row)
      return true
    }

    if (cascadeDown(row.children ?? [], menuId, field, value)) {
      return true
    }
  }

  return false
}

const reconcileField = (rows: MenuPermissionTreeRow[], field: PermissionField): boolean => {
  return rows.some((row) => {
    const hasChildren = Boolean(row.children?.length)

    if (!hasChildren) {
      return row[field]
    }

    const childGranted = reconcileField(row.children ?? [], field)
    row[field] = childGranted
    return row[field]
  })
}

const handleToggle = ({ menuId, field, value }: { menuId: string; field: PermissionField; value: boolean }) => {
  cascadeDown(permissionRows.value, menuId, field, value)
  permissionFields.forEach((permissionField) => {
    reconcileField(permissionRows.value, permissionField)
  })
}

const buildSavePayload = (): CrudPayload<Record<string, unknown>> => {
  const currentMap = new Map(flattenPermissionTree(permissionRows.value).map((row) => [row.menuId, row]))
  const originalMap = new Map(flattenPermissionTree(originalRows.value).map((row) => [row.menuId, row]))
  const payload: CrudPayload<Record<string, unknown>> = {
    added: [],
    updated: [],
    deleted: [],
  }

  currentMap.forEach((row, menuId) => {
    const original = originalMap.get(menuId)
    const currentFlags = permissionFields.map((field) => row[field])
    const hasGrantedPermission = currentFlags.some(Boolean)

    const requestRow = {
      comCd: row.comCd,
      serviceId: row.serviceId,
      groupId: row.groupId,
      menuId: row.menuId,
      permitRead: row.permitRead,
      permitWrite: row.permitWrite,
      permitDelete: row.permitDelete,
      permitExcel: row.permitExcel,
    }

    if (!original) {
      if (hasGrantedPermission) {
        payload.added.push(requestRow)
      }
      return
    }

    const changed = permissionFields.some((field) => row[field] !== original[field])

    if (!changed) {
      return
    }

    if (!hasGrantedPermission) {
      payload.deleted.push(requestRow)
      return
    }

    payload.updated.push(requestRow)
  })

  return payload
}

const handleSave = async () => {
  saveLoading.value = true

  try {
    const rows = await savePermissionTree(buildSavePayload(), {
      comCd: comCd.value,
      serviceId: selectedServiceId.value,
      groupId: selectedGroupId.value,
    })
    permissionRows.value = clonePermissionTree(rows)
    originalRows.value = clonePermissionTree(rows)
    expandedKeys.value = collectExpandedKeys(rows)
    toast.add({
      severity: 'success',
      summary: t('menuPermissions.savedTitle'),
      detail: t('menuPermissions.savedDetail'),
      life: 2200,
    })
  } finally {
    saveLoading.value = false
  }
}

const loadAll = async () => {
  await loadGroups()
  await loadPermissionTree()
}

watch(
  serviceOptions,
  (nextOptions) => {
    if (!nextOptions.length) {
      selectedServiceId.value = ''
      return
    }

    if (!nextOptions.includes(selectedServiceId.value)) {
      selectedServiceId.value = nextOptions[0]
    }
  },
  { immediate: true },
)

watch(selectedServiceId, async () => {
  await loadAll()
}, { immediate: true })

watch(selectedGroupId, async () => {
  await loadPermissionTree()
})
</script>

<template>
  <div class="page-stack">
    <BasePageHeader title="menu.menuPermissions" />

    <BaseForbiddenState v-if="!permissions.permitRead" />
    <template v-else>
      <SearchView
        :keyword="keyword"
        :selected-service-id="selectedServiceId"
        :service-options="serviceOptions"
        @update:keyword="keyword = $event"
        @update:selected-service-id="selectedServiceId = $event"
        @search="loadPermissionTree"
      />

      <div class="admin-split-layout">
        <GroupListView :groups="groups" :selected-group-id="selectedGroupId" @select="selectedGroupId = $event" />

        <BaseEmptyState
          v-if="!loading && !treeNodes.length"
          :title="t('menuPermissions.emptyTitle')"
          :description="t('menuPermissions.emptyDescription')"
        />

        <PermissionTreeView
          v-else
          :nodes="treeNodes"
          :expanded-keys="expandedKeys"
          :disabled="!permissions.permitWrite"
          @update:expanded-keys="expandedKeys = $event"
          @save="handleSave"
          @refresh="loadAll"
          @toggle="handleToggle"
        />
      </div>
    </template>
  </div>
</template>
