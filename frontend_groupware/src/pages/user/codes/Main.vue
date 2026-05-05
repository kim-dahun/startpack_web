<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import Button from 'primevue/button'
import { useConfirm } from 'primevue/useconfirm'
import { useToast } from 'primevue/usetoast'

import BaseEmptyState from '@/components/common/BaseEmptyState.vue'
import BaseForbiddenState from '@/components/common/BaseForbiddenState.vue'
import BasePageHeader from '@/components/common/BasePageHeader.vue'
import BaseDataGrid from '@/components/grid/BaseDataGrid.vue'
import { useSessionStore } from '@/stores/session'
import type { CrudPayload } from '@/types/app'
import { listCodeDropdownOptions, listCodeGroups, listCodes, saveCodeGroups, saveCodes, type CodeGroupRow } from './api/api'
import type { CodeRow } from './api/api'
import { buildCodeColumns, createEmptyCodeGroupForm, pageTitle } from './model/pageModel'
import type { CodeGroupForm } from './model/pageModel'
import CodeGroupPopupView from './popup-view/CodeGroupPopupView.vue'
import CodeGroupListView from './sub-view/CodeGroupListView.vue'
import SearchView from './sub-view/SearchView.vue'

type DialogMode = 'create' | 'edit'

const sessionStore = useSessionStore()
const toast = useToast()
const confirm = useConfirm()

const permissions = computed(() => sessionStore.getPermissions('CODES'))
const serviceOptions = computed(() => sessionStore.adminServiceIds)
const comCd = computed(() => sessionStore.persisted.user?.comCd ?? 'COM001')

const selectedServiceId = ref(serviceOptions.value[0] ?? 'GROUPWARE')
const selectedCodeGroupId = ref('')
const keyword = ref('')
const groups = ref<CodeGroupRow[]>([])
const codeRows = ref<CodeRow[]>([])
const columns = ref(buildCodeColumns())
const loading = ref(false)
const groupSaveLoading = ref(false)
const dialogVisible = ref(false)
const dialogMode = ref<DialogMode>('create')
const groupForm = reactive<CodeGroupForm>(createEmptyCodeGroupForm())

const filteredCodeRows = computed(() => {
  const normalized = keyword.value.trim().toLowerCase()

  if (!normalized) {
    return codeRows.value
  }

  return codeRows.value.filter((row) =>
    [row.codeId, row.codeName, row.subInfo1, row.subInfo2, row.subInfo3]
      .some((value) => String(value ?? '').toLowerCase().includes(normalized)),
  )
})

const rowDefaults = computed(() => ({
  comCd: comCd.value,
  serviceId: selectedServiceId.value,
  codeGroupId: selectedCodeGroupId.value,
  parentCodeGroupId: selectedCodeGroupId.value,
  enabled: true,
  sortSeq: 1,
}))

const syncCodeParentOptions = async () => {
  const options = selectedCodeGroupId.value
    ? await listCodeDropdownOptions({
      comCd: comCd.value,
      serviceId: selectedServiceId.value,
      codeGroupId: selectedCodeGroupId.value,
    }).catch(() => [])
    : []

  columns.value = buildCodeColumns().map((column) => {
    if (column.field === 'parentCodeId') {
      return {
        ...column,
        editorOptions: [{ label: 'Not Selected', value: '' }, ...options],
      }
    }

    return column
  })
}

const loadGroups = async () => {
  groups.value = await listCodeGroups({
    comCd: comCd.value,
    serviceId: selectedServiceId.value,
  })

  if (!groups.value.some((group) => group.codeGroupId === selectedCodeGroupId.value)) {
    selectedCodeGroupId.value = groups.value[0]?.codeGroupId ?? ''
  }
}

const loadCodes = async () => {
  if (!selectedCodeGroupId.value) {
    codeRows.value = []
    await syncCodeParentOptions()
    return
  }

  loading.value = true

  try {
    codeRows.value = await listCodes({
      comCd: comCd.value,
      serviceId: selectedServiceId.value,
      codeGroupId: selectedCodeGroupId.value,
    })
    await syncCodeParentOptions()
  } finally {
    loading.value = false
  }
}

const loadAll = async () => {
  await loadGroups()
  await loadCodes()
}

const openCreateGroup = () => {
  dialogMode.value = 'create'
  Object.assign(groupForm, createEmptyCodeGroupForm())
  dialogVisible.value = true
}

const openEditGroup = (codeGroupId: string) => {
  const target = groups.value.find((group) => group.codeGroupId === codeGroupId)

  if (!target) {
    return
  }

  dialogMode.value = 'edit'
  Object.assign(groupForm, {
    codeGroupId: target.codeGroupId,
    codeGroupName: target.codeGroupName,
    description: target.description,
    enabled: Boolean(target.enabled),
  })
  dialogVisible.value = true
}

const handleSaveGroup = async () => {
  if (!groupForm.codeGroupId.trim() || !groupForm.codeGroupName.trim()) {
    toast.add({
      severity: 'warn',
      summary: 'Required Fields',
      detail: 'codeGroupId and codeGroupName are required.',
      life: 2400,
    })
    return
  }

  groupSaveLoading.value = true

  try {
    groups.value = await saveCodeGroups(dialogMode.value === 'edit'
      ? {
        added: [],
        updated: [{
          comCd: comCd.value,
          serviceId: selectedServiceId.value,
          codeGroupId: groupForm.codeGroupId.trim(),
          codeGroupName: groupForm.codeGroupName.trim(),
          description: groupForm.description.trim(),
          enabled: Boolean(groupForm.enabled),
        }],
        deleted: [],
      }
      : {
        added: [{
          comCd: comCd.value,
          serviceId: selectedServiceId.value,
          codeGroupId: groupForm.codeGroupId.trim(),
          codeGroupName: groupForm.codeGroupName.trim(),
          description: groupForm.description.trim(),
          enabled: Boolean(groupForm.enabled),
        }],
        updated: [],
        deleted: [],
      }, {
      comCd: comCd.value,
      serviceId: selectedServiceId.value,
    })
    selectedCodeGroupId.value = groupForm.codeGroupId.trim()
    dialogVisible.value = false
    await loadCodes()
    toast.add({
      severity: 'success',
      summary: 'Saved',
      detail: 'Code group changes were saved.',
      life: 2200,
    })
  } finally {
    groupSaveLoading.value = false
  }
}

const handleDeleteGroup = (codeGroupId: string) => {
  const target = groups.value.find((group) => group.codeGroupId === codeGroupId)

  if (!target) {
    return
  }

  if (codeRows.value.length) {
    toast.add({
      severity: 'warn',
      summary: 'Delete Blocked',
      detail: 'Delete codes in the selected group first.',
      life: 2400,
    })
    return
  }

  confirm.require({
    message: `${target.codeGroupName} will be deleted.`,
    header: 'Delete Code Group',
    icon: 'pi pi-exclamation-triangle',
    acceptLabel: 'Delete',
    rejectLabel: 'Cancel',
    accept: async () => {
      groups.value = await saveCodeGroups({
        added: [],
        updated: [],
        deleted: [target],
      }, {
        comCd: comCd.value,
        serviceId: selectedServiceId.value,
      })
      selectedCodeGroupId.value = groups.value[0]?.codeGroupId ?? ''
      await loadCodes()
      toast.add({
        severity: 'success',
        summary: 'Deleted',
        detail: 'Code group was deleted.',
        life: 2200,
      })
    },
  })
}

const handleSaveCodes = async (payload: CrudPayload<Record<string, unknown>>) => {
  codeRows.value = await saveCodes(payload, {
    comCd: comCd.value,
    serviceId: selectedServiceId.value,
    codeGroupId: selectedCodeGroupId.value,
  })
  await syncCodeParentOptions()
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

watch(selectedCodeGroupId, async () => {
  await loadCodes()
})
</script>

<template>
  <div class="page-stack">
    <BasePageHeader :title="pageTitle" />

    <BaseForbiddenState v-if="!permissions.permitRead" />
    <template v-else>
      <SearchView
        :keyword="keyword"
        :selected-service-id="selectedServiceId"
        :service-options="serviceOptions"
        @update:keyword="keyword = $event"
        @update:selected-service-id="selectedServiceId = $event"
        @search="loadCodes"
      />

      <div class="admin-split-layout">
        <CodeGroupListView
          :groups="groups"
          :selected-code-group-id="selectedCodeGroupId"
          :can-write="permissions.permitWrite"
          @select="selectedCodeGroupId = $event"
          @add="openCreateGroup"
          @edit="openEditGroup"
          @remove="handleDeleteGroup"
          @refresh="loadAll"
        />

        <BaseEmptyState
          v-if="!selectedCodeGroupId"
          title="No Code Group Selected"
          description="Select or create a code group first."
        />

        <BaseDataGrid
          v-else
          caption="Codes"
          :columns="columns"
          :rows="filteredCodeRows as unknown as Array<Record<string, unknown>>"
          :permissions="permissions"
          :row-defaults="rowDefaults"
          @save="handleSaveCodes"
        />
      </div>
    </template>

    <CodeGroupPopupView
      :visible="dialogVisible"
      :title="dialogMode === 'create' ? 'Add Code Group' : 'Edit Code Group'"
      :form="groupForm"
      :loading="groupSaveLoading"
      @update:visible="dialogVisible = $event"
      @save="handleSaveGroup"
    />
  </div>
</template>
