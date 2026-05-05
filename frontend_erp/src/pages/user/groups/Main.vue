<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import Button from 'primevue/button'
import Checkbox from 'primevue/checkbox'
import InputText from 'primevue/inputtext'
import Listbox from 'primevue/listbox'
import { useConfirm } from 'primevue/useconfirm'
import { useToast } from 'primevue/usetoast'

import BaseDialog from '@/components/common/BaseDialog.vue'
import BaseEmptyState from '@/components/common/BaseEmptyState.vue'
import BaseForbiddenState from '@/components/common/BaseForbiddenState.vue'
import BasePageHeader from '@/components/common/BasePageHeader.vue'
import BaseSearchForm from '@/components/form/BaseSearchForm.vue'
import { useAppI18n } from '@/composables/useAppI18n'
import { useSessionStore } from '@/stores/session'
import type { CrudPayload, DataGridOption } from '@/types/app'
import { listGroupMembers, listGroups, listUserOptions, saveGroupMembers, saveGroups, type GroupRow } from './api/api'

type DialogMode = 'create' | 'edit'

const sessionStore = useSessionStore()
const route = useRoute()
const toast = useToast()
const confirm = useConfirm()
const { t } = useAppI18n()

const permissions = computed(() => sessionStore.getPermissions(route.meta.menuId as string))
const serviceOptions = computed(() => sessionStore.adminServiceIds)
const comCd = computed(() => sessionStore.persisted.user?.comCd ?? 'COM001')

const selectedServiceId = ref(serviceOptions.value.includes('ERP') ? 'ERP' : serviceOptions.value[0] ?? 'ERP')
const keyword = ref('')
const groups = ref<GroupRow[]>([])
const selectedGroupId = ref('')
const userOptions = ref<DataGridOption[]>([])
const originalMemberIds = ref<string[]>([])
const currentMemberIds = ref<string[]>([])
const selectedAvailableUserIds = ref<string[]>([])
const selectedAssignedUserIds = ref<string[]>([])
const dialogVisible = ref(false)
const dialogMode = ref<DialogMode>('create')
const dialogLoading = ref(false)

const groupForm = reactive({
  groupId: '',
  groupName: '',
  description: '',
  enabled: true,
})

const filteredGroups = computed(() => {
  const normalized = keyword.value.trim().toLowerCase()

  if (!normalized) {
    return groups.value
  }

  return groups.value.filter((group) =>
    [group.groupId, group.groupName, group.description]
      .some((value) => String(value ?? '').toLowerCase().includes(normalized)),
  )
})

const availableUserOptions = computed(() =>
  userOptions.value.filter((option) => !currentMemberIds.value.includes(String(option.value ?? ''))),
)

const assignedUserOptions = computed(() =>
  userOptions.value.filter((option) => currentMemberIds.value.includes(String(option.value ?? ''))),
)

const selectedGroup = computed(() => groups.value.find((group) => group.groupId === selectedGroupId.value) ?? null)

const resetGroupForm = () => {
  groupForm.groupId = ''
  groupForm.groupName = ''
  groupForm.description = ''
  groupForm.enabled = true
}

const loadGroups = async () => {
  groups.value = await listGroups({
    comCd: comCd.value,
    serviceId: selectedServiceId.value,
  })

  if (!groups.value.some((group) => group.groupId === selectedGroupId.value)) {
    selectedGroupId.value = groups.value[0]?.groupId ?? ''
  }
}

const loadUsers = async () => {
  userOptions.value = await listUserOptions({
    comCd: comCd.value,
  })
}

const loadMembers = async () => {
  if (!selectedGroupId.value) {
    originalMemberIds.value = []
    currentMemberIds.value = []
    return
  }

  const rows = await listGroupMembers({
    comCd: comCd.value,
    serviceId: selectedServiceId.value,
    groupId: selectedGroupId.value,
  })

  originalMemberIds.value = rows.map((row) => row.userId)
  currentMemberIds.value = [...originalMemberIds.value]
}

const loadAll = async () => {
  await Promise.all([loadGroups(), loadUsers()])
  await loadMembers()
}

const moveToAssigned = () => {
  currentMemberIds.value = [...new Set([...currentMemberIds.value, ...selectedAvailableUserIds.value])]
  selectedAvailableUserIds.value = []
}

const moveToAvailable = () => {
  currentMemberIds.value = currentMemberIds.value.filter((userId) => !selectedAssignedUserIds.value.includes(userId))
  selectedAssignedUserIds.value = []
}

const handleSaveMembers = async () => {
  if (!selectedGroupId.value) {
    return
  }

  const added = currentMemberIds.value
    .filter((userId) => !originalMemberIds.value.includes(userId))
    .map((userId) => ({
      comCd: comCd.value,
      serviceId: selectedServiceId.value,
      groupId: selectedGroupId.value,
      userId,
    }))

  const deleted = originalMemberIds.value
    .filter((userId) => !currentMemberIds.value.includes(userId))
    .map((userId) => ({
      comCd: comCd.value,
      serviceId: selectedServiceId.value,
      groupId: selectedGroupId.value,
      userId,
    }))

  const payload: CrudPayload<Record<string, unknown>> = {
    added,
    updated: [],
    deleted,
  }

  const rows = await saveGroupMembers(payload, {
    comCd: comCd.value,
    serviceId: selectedServiceId.value,
    groupId: selectedGroupId.value,
  })

  originalMemberIds.value = rows.map((row) => row.userId)
  currentMemberIds.value = [...originalMemberIds.value]

  toast.add({
    severity: 'success',
    summary: t('common.save'),
    detail: t('menu.groupMembers'),
    life: 2200,
  })
}

const openCreate = () => {
  dialogMode.value = 'create'
  resetGroupForm()
  dialogVisible.value = true
}

const openEdit = () => {
  if (!selectedGroup.value) {
    return
  }

  dialogMode.value = 'edit'
  groupForm.groupId = selectedGroup.value.groupId
  groupForm.groupName = selectedGroup.value.groupName
  groupForm.description = selectedGroup.value.description ?? ''
  groupForm.enabled = Boolean(selectedGroup.value.enabled)
  dialogVisible.value = true
}

const handleSaveGroup = async () => {
  if (!groupForm.groupId.trim() || !groupForm.groupName.trim()) {
    toast.add({
      severity: 'warn',
      summary: 'Required Fields',
      detail: 'groupId and groupName are required.',
      life: 2200,
    })
    return
  }

  dialogLoading.value = true

  try {
    groups.value = await saveGroups(dialogMode.value === 'edit'
      ? {
        added: [],
        updated: [{
          comCd: comCd.value,
          serviceId: selectedServiceId.value,
          groupId: groupForm.groupId.trim(),
          groupName: groupForm.groupName.trim(),
          description: groupForm.description.trim(),
          enabled: Boolean(groupForm.enabled),
        }],
        deleted: [],
      }
      : {
        added: [{
          comCd: comCd.value,
          serviceId: selectedServiceId.value,
          groupId: groupForm.groupId.trim(),
          groupName: groupForm.groupName.trim(),
          description: groupForm.description.trim(),
          enabled: Boolean(groupForm.enabled),
        }],
        updated: [],
        deleted: [],
      }, {
      comCd: comCd.value,
      serviceId: selectedServiceId.value,
    })

    selectedGroupId.value = groupForm.groupId.trim()
    dialogVisible.value = false
    await loadMembers()
  } finally {
    dialogLoading.value = false
  }
}

const handleDeleteGroup = () => {
  if (!selectedGroup.value) {
    return
  }

  confirm.require({
    message: `${selectedGroup.value.groupName} will be deleted.`,
    header: t('common.delete'),
    icon: 'pi pi-exclamation-triangle',
    acceptLabel: t('common.delete'),
    rejectLabel: t('common.close'),
    accept: async () => {
      groups.value = await saveGroups({
        added: [],
        updated: [],
        deleted: [selectedGroup.value as unknown as Record<string, unknown>],
      }, {
        comCd: comCd.value,
        serviceId: selectedServiceId.value,
      })
      selectedGroupId.value = groups.value[0]?.groupId ?? ''
      await loadMembers()
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

watch(selectedServiceId, async () => {
  await loadAll()
}, { immediate: true })

watch(selectedGroupId, async () => {
  await loadMembers()
})
</script>

<template>
  <div class="page-stack">
    <BasePageHeader title="menu.groups" />

    <BaseForbiddenState v-if="!permissions.permitRead" />
    <template v-else>
      <BaseSearchForm
        :model-value="keyword"
        placeholder="groupId, groupName"
        title="search.title"
        @update:model-value="keyword = String($event ?? '')"
        @search="loadGroups"
      >
        <label class="inline-input">
          <span>serviceId</span>
          <select :value="selectedServiceId" class="native-select" @change="selectedServiceId = String(($event.target as HTMLSelectElement).value)">
            <option v-for="serviceId in serviceOptions" :key="serviceId" :value="serviceId">
              {{ serviceId }}
            </option>
          </select>
        </label>
        <label class="inline-input">
          <span>{{ t('common.keyword') }}</span>
          <InputText :model-value="keyword" placeholder="groupId, groupName" @update:model-value="keyword = String($event ?? '')" />
        </label>
      </BaseSearchForm>

      <div class="admin-split-layout">
        <section class="split-panel">
          <header class="split-panel__header">
            <strong>{{ t('menu.groups') }}</strong>
            <div class="split-panel__actions">
              <Button icon="pi pi-plus" :label="t('common.add')" size="small" :disabled="!permissions.permitWrite" @click="openCreate" />
              <Button icon="pi pi-pencil" :label="t('common.edit')" size="small" severity="secondary" :disabled="!permissions.permitWrite || !selectedGroupId" @click="openEdit" />
              <Button icon="pi pi-trash" :label="t('common.delete')" size="small" severity="danger" :disabled="!permissions.permitDelete || !selectedGroupId" @click="handleDeleteGroup" />
              <Button icon="pi pi-refresh" :label="t('common.refresh')" size="small" severity="secondary" @click="loadAll" />
            </div>
          </header>
          <div class="selection-list">
            <button
              v-for="group in filteredGroups"
              :key="group.groupId"
              type="button"
              class="selection-list__item"
              :class="{ 'is-active': group.groupId === selectedGroupId }"
              @click="selectedGroupId = group.groupId"
            >
              <strong>{{ group.groupName }}</strong>
              <span>{{ group.groupId }}</span>
            </button>
          </div>
        </section>

        <BaseEmptyState
          v-if="!selectedGroupId"
          :title="t('menu.groups')"
          description="Select a group to manage members."
        />

        <section v-else class="split-panel">
          <header class="split-panel__header">
            <strong>{{ t('menu.groupMembers') }}</strong>
            <Button icon="pi pi-save" :label="t('common.save')" size="small" :disabled="!permissions.permitWrite" @click="handleSaveMembers" />
          </header>
          <div class="dual-list-layout">
            <div class="dual-list-layout__list">
              <strong>{{ t('common.add') }}</strong>
              <Listbox
                v-model="selectedAvailableUserIds"
                :options="availableUserOptions"
                option-label="label"
                option-value="value"
                multiple
                filter
                list-style="height:100%"
              />
            </div>
            <div class="dual-list-layout__actions">
              <Button icon="pi pi-angle-right" :disabled="!permissions.permitWrite || !selectedAvailableUserIds.length" @click="moveToAssigned" />
              <Button icon="pi pi-angle-left" severity="secondary" :disabled="!permissions.permitWrite || !selectedAssignedUserIds.length" @click="moveToAvailable" />
            </div>
            <div class="dual-list-layout__list">
              <strong>{{ t('menu.groupMembers') }}</strong>
              <Listbox
                v-model="selectedAssignedUserIds"
                :options="assignedUserOptions"
                option-label="label"
                option-value="value"
                multiple
                filter
                list-style="height:100%"
              />
            </div>
          </div>
        </section>
      </div>
    </template>

    <BaseDialog :visible="dialogVisible" :title="dialogMode === 'create' ? `${t('common.add')} ${t('menu.groups')}` : `${t('common.edit')} ${t('menu.groups')}`" @update:visible="dialogVisible = $event">
      <div class="form-popup-stack">
        <label class="inline-input">
          <span>Group ID</span>
          <InputText v-model="groupForm.groupId" />
        </label>
        <label class="inline-input">
          <span>Group Name</span>
          <InputText v-model="groupForm.groupName" />
        </label>
        <label class="inline-input">
          <span>Description</span>
          <InputText v-model="groupForm.description" />
        </label>
        <label class="checkbox-line">
          <Checkbox v-model="groupForm.enabled" binary />
          <span>{{ t('common.enabled') }}</span>
        </label>
        <div class="dialog-actions">
          <Button :label="t('common.close')" severity="secondary" @click="dialogVisible = false" />
          <Button :label="t('common.save')" :loading="dialogLoading" @click="handleSaveGroup" />
        </div>
      </div>
    </BaseDialog>
  </div>
</template>
