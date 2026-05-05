<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import Button from 'primevue/button'
import { useConfirm } from 'primevue/useconfirm'
import { useToast } from 'primevue/usetoast'
import type { TreeTableExpandedKeys } from 'primevue/treetable'

import BaseEmptyState from '@/components/common/BaseEmptyState.vue'
import BaseForbiddenState from '@/components/common/BaseForbiddenState.vue'
import BasePageHeader from '@/components/common/BasePageHeader.vue'
import { useAppI18n } from '@/composables/useAppI18n'
import { useSessionStore } from '@/stores/session'
import type { CrudPayload } from '@/types/app'
import { listMenuTree, saveMenuTree, type UserMenuRow } from './api/api'
import { buildMenuTreeNodes, collectExpandedKeys, createEmptyMenuForm, filterMenuTree, flattenMenus } from './model/pageModel'
import type { MenuEditorForm } from './model/pageModel'
import MenuEditorPopupView from './popup-view/MenuEditorPopupView.vue'
import SearchView from './sub-view/SearchView.vue'
import MenuTreeView from './sub-view/MenuTreeView.vue'

type MenuDialogMode = 'create-root' | 'create-child' | 'edit'

const sessionStore = useSessionStore()
const toast = useToast()
const confirm = useConfirm()
const { t } = useAppI18n()

const comCd = computed(() => sessionStore.persisted.user?.comCd ?? 'COM001')
const permissions = computed(() => sessionStore.getPermissions('MENUS'))
const serviceOptions = computed(() => sessionStore.adminServiceIds)

const selectedServiceId = ref(serviceOptions.value[0] ?? 'TRADE')
const keyword = ref('')
const loading = ref(false)
const saveLoading = ref(false)
const menuRows = ref<UserMenuRow[]>([])
const expandedKeys = ref<TreeTableExpandedKeys>({})

const dialogVisible = ref(false)
const dialogMode = ref<MenuDialogMode>('create-root')
const dialogParentId = ref('')
const editingMenuId = ref('')
const menuForm = reactive<MenuEditorForm>(createEmptyMenuForm())

const flatMenus = computed(() => flattenMenus(menuRows.value))
const filteredRows = computed(() => filterMenuTree(menuRows.value, keyword.value))
const treeNodes = computed(() => buildMenuTreeNodes(filteredRows.value))

const findMenuById = (menuId: string) => flatMenus.value.find((row) => row.menuId === menuId) ?? null

const dialogTitle = computed(() => {
  if (dialogMode.value === 'create-root') {
    return t('menus.dialog.createRoot')
  }
  if (dialogMode.value === 'create-child') {
    return t('menus.dialog.createChild')
  }
  return t('menus.dialog.editTitle')
})

const dialogParentLabel = computed(() => {
  if (dialogMode.value === 'create-root') {
    return t('menus.rootLabel')
  }

  const parentMenu = findMenuById(dialogParentId.value)
  return parentMenu ? `${parentMenu.menuName} (${parentMenu.menuId})` : t('menus.rootLabel')
})

const dialogLevel = computed(() => {
  if (dialogMode.value === 'create-root') {
    return 1
  }

  if (dialogMode.value === 'create-child') {
    return (findMenuById(dialogParentId.value)?.menuLevel ?? 0) + 1
  }

  return findMenuById(editingMenuId.value)?.menuLevel ?? 1
})

const resetForm = () => {
  Object.assign(menuForm, createEmptyMenuForm())
}

const loadRows = async () => {
  if (!selectedServiceId.value) {
    menuRows.value = []
    return
  }

  loading.value = true

  try {
    const rows = await listMenuTree({
      comCd: comCd.value,
      serviceId: selectedServiceId.value,
    })
    menuRows.value = rows
    expandedKeys.value = collectExpandedKeys(rows)
  } finally {
    loading.value = false
  }
}

const openCreateRoot = () => {
  dialogMode.value = 'create-root'
  dialogParentId.value = ''
  editingMenuId.value = ''
  resetForm()
  dialogVisible.value = true
}

const openCreateChild = (menuId: string) => {
  const parent = findMenuById(menuId)

  if (!parent || parent.menuLevel >= 3) {
    toast.add({
      severity: 'warn',
      summary: t('menus.addBlockedTitle'),
      detail: t('menus.addBlockedDetail'),
      life: 2400,
    })
    return
  }

  dialogMode.value = 'create-child'
  dialogParentId.value = menuId
  editingMenuId.value = ''
  resetForm()
  menuForm.sortSeq = (parent.children?.length ?? 0) + 1
  dialogVisible.value = true
}

const openEdit = (menuId: string) => {
  const target = findMenuById(menuId)

  if (!target) {
    return
  }

  dialogMode.value = 'edit'
  dialogParentId.value = target.menuParentId ?? ''
  editingMenuId.value = menuId
  Object.assign(menuForm, {
    menuId: target.menuId,
    menuName: target.menuName,
    menuUrl: target.menuUrl,
    i18nCode: target.i18nCode,
    icon: target.icon,
    sortSeq: target.sortSeq,
    enabled: target.enabled,
  })
  dialogVisible.value = true
}

const buildSavePayload = (): CrudPayload<Record<string, unknown>> => {
  const menuLevel = dialogLevel.value
  const row = {
    comCd: comCd.value,
    serviceId: selectedServiceId.value,
    menuId: menuForm.menuId.trim(),
    menuParentId: dialogMode.value === 'create-root' ? '' : dialogParentId.value,
    menuName: menuForm.menuName.trim(),
    menuUrl: menuForm.menuUrl.trim(),
    i18nCode: menuForm.i18nCode.trim(),
    icon: menuForm.icon.trim(),
    menuLevel,
    sortSeq: Number(menuForm.sortSeq ?? 1),
    enabled: Boolean(menuForm.enabled),
  }

  return dialogMode.value === 'edit'
    ? { added: [], updated: [row], deleted: [] }
    : { added: [row], updated: [], deleted: [] }
}

const handleSave = async () => {
  if (!menuForm.menuId.trim() || !menuForm.menuName.trim()) {
    toast.add({
      severity: 'warn',
      summary: t('menus.requiredTitle'),
      detail: t('menus.requiredDetail'),
      life: 2400,
    })
    return
  }

  saveLoading.value = true

  try {
    menuRows.value = await saveMenuTree(buildSavePayload(), {
      comCd: comCd.value,
      serviceId: selectedServiceId.value,
    })
    expandedKeys.value = collectExpandedKeys(menuRows.value)
    dialogVisible.value = false
    toast.add({
      severity: 'success',
      summary: t('menus.savedTitle'),
      detail: t('menus.savedDetail'),
      life: 2200,
    })
  } finally {
    saveLoading.value = false
  }
}

const handleDelete = (menuId: string) => {
  const target = findMenuById(menuId)

  if (!target) {
    return
  }

  if (target.children?.length) {
    toast.add({
      severity: 'warn',
      summary: t('menus.deleteBlockedTitle'),
      detail: t('menus.deleteBlockedDetail'),
      life: 2400,
    })
    return
  }

  confirm.require({
    message: t('menus.deleteConfirmMessage', undefined, { name: target.menuName }),
    header: t('menus.deleteConfirmTitle'),
    icon: 'pi pi-exclamation-triangle',
    acceptLabel: t('common.delete'),
    rejectLabel: t('common.close'),
    accept: async () => {
      menuRows.value = await saveMenuTree({
        added: [],
        updated: [],
        deleted: [{
          comCd: target.comCd,
          serviceId: target.serviceId,
          menuId: target.menuId,
          menuParentId: target.menuParentId,
          menuName: target.menuName,
          menuUrl: target.menuUrl,
          i18nCode: target.i18nCode,
          icon: target.icon,
          menuLevel: target.menuLevel,
          sortSeq: target.sortSeq,
          enabled: target.enabled,
        }],
      }, {
        comCd: comCd.value,
        serviceId: selectedServiceId.value,
      })
      expandedKeys.value = collectExpandedKeys(menuRows.value)
      toast.add({
        severity: 'success',
        summary: t('menus.deletedTitle'),
        detail: t('menus.deletedDetail'),
        life: 2200,
      })
    },
  })
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

watch(selectedServiceId, () => {
  void loadRows()
}, { immediate: true })
</script>

<template>
  <div class="page-stack">
    <BasePageHeader title="menu.menus" />

    <BaseForbiddenState v-if="!permissions.permitRead" />
    <template v-else>
      <SearchView
        :keyword="keyword"
        :selected-service-id="selectedServiceId"
        :service-options="serviceOptions"
        @update:keyword="keyword = $event"
        @update:selected-service-id="selectedServiceId = $event"
        @search="loadRows"
      />

      <BaseEmptyState
        v-if="!loading && !treeNodes.length"
        :title="t('menus.emptyTitle')"
        :description="t('menus.emptyDescription')"
      />

      <MenuTreeView
        v-else
        :nodes="treeNodes"
        :expanded-keys="expandedKeys"
        :can-write="permissions.permitWrite"
        @update:expanded-keys="expandedKeys = $event"
        @add-root="openCreateRoot"
        @refresh="loadRows"
        @add-child="openCreateChild"
        @edit="openEdit"
        @remove="handleDelete"
      />
    </template>

    <MenuEditorPopupView
      :visible="dialogVisible"
      :title="dialogTitle"
      :form="menuForm"
      :parent-label="dialogParentLabel"
      :level="dialogLevel"
      :loading="saveLoading"
      @update:visible="dialogVisible = $event"
      @save="handleSave"
    />
  </div>
</template>
