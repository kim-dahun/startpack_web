<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import { useConfirm } from 'primevue/useconfirm'
import { useToast } from 'primevue/usetoast'

import BaseDialog from '@/components/common/BaseDialog.vue'
import BaseEmptyState from '@/components/common/BaseEmptyState.vue'
import BaseForbiddenState from '@/components/common/BaseForbiddenState.vue'
import BasePageHeader from '@/components/common/BasePageHeader.vue'
import BaseSearchForm from '@/components/form/BaseSearchForm.vue'
import { useAppI18n } from '@/composables/useAppI18n'
import { useSessionStore } from '@/stores/session'
import type { DataGridOption } from '@/types/app'
import { listAffiliations, listJobGradeOptions, listUsers, savePrimaryYn, saveUsers, type UserRow } from './api/api'
import type { UserPositionRow } from '@/api/modules/user'

type DialogMode = 'create' | 'edit'

const sessionStore = useSessionStore()
const toast = useToast()
const confirm = useConfirm()
const { t } = useAppI18n()

const permissions = computed(() => sessionStore.getPermissions('USERS'))
const comCd = computed(() => sessionStore.persisted.user?.comCd ?? 'COM001')

const keyword = ref('')
const users = ref<UserRow[]>([])
const selectedUserId = ref('')
const affiliations = ref<UserPositionRow[]>([])
const jobGradeOptions = ref<DataGridOption[]>([])
const dialogVisible = ref(false)
const dialogMode = ref<DialogMode>('create')
const dialogLoading = ref(false)

const userForm = reactive({
  userId: '',
  userName: '',
  jobGradeId: '',
  status: 'ACTIVE',
  email: '',
  phone: '',
  address: '',
  password: '',
})

const filteredUsers = computed(() => {
  const normalized = keyword.value.trim().toLowerCase()
  if (!normalized) {
    return users.value
  }
  return users.value.filter((user) =>
    [user.userId, user.userName, user.email, user.phone]
      .some((value) => String(value ?? '').toLowerCase().includes(normalized)),
  )
})

const selectedUser = computed(() => users.value.find((user) => user.userId === selectedUserId.value) ?? null)

const loadUsers = async () => {
  users.value = await listUsers({ comCd: comCd.value })
  if (!users.value.some((user) => user.userId === selectedUserId.value)) {
    selectedUserId.value = users.value[0]?.userId ?? ''
  }
}

const loadJobGrades = async () => {
  jobGradeOptions.value = await listJobGradeOptions({ comCd: comCd.value })
}

const loadAffiliations = async () => {
  if (!selectedUserId.value) {
    affiliations.value = []
    return
  }
  affiliations.value = await listAffiliations({ comCd: comCd.value, userId: selectedUserId.value })
}

const loadAll = async () => {
  await Promise.all([loadUsers(), loadJobGrades()])
  await loadAffiliations()
}

const resetForm = () => {
  userForm.userId = ''
  userForm.userName = ''
  userForm.jobGradeId = ''
  userForm.status = 'ACTIVE'
  userForm.email = ''
  userForm.phone = ''
  userForm.address = ''
  userForm.password = ''
}

const openCreate = () => {
  dialogMode.value = 'create'
  resetForm()
  dialogVisible.value = true
}

const openEdit = () => {
  if (!selectedUser.value) {
    return
  }
  dialogMode.value = 'edit'
  userForm.userId = selectedUser.value.userId
  userForm.userName = selectedUser.value.userName
  userForm.jobGradeId = selectedUser.value.jobGradeId
  userForm.status = selectedUser.value.status
  userForm.email = selectedUser.value.email ?? ''
  userForm.phone = selectedUser.value.phone ?? ''
  userForm.address = selectedUser.value.address ?? ''
  userForm.password = ''
  dialogVisible.value = true
}

const handleSaveUser = async () => {
  if (!userForm.userId.trim() || !userForm.userName.trim()) {
    toast.add({
      severity: 'warn',
      summary: t('users.requiredTitle'),
      detail: t('users.requiredDetail'),
      life: 2200,
    })
    return
  }

  const row: Record<string, unknown> = {
    comCd: comCd.value,
    userId: userForm.userId.trim(),
    userName: userForm.userName.trim(),
    jobGradeId: userForm.jobGradeId,
    status: userForm.status,
    email: userForm.email.trim(),
    phone: userForm.phone.trim(),
    address: userForm.address.trim(),
  }

  if (userForm.password.trim()) {
    row.password = userForm.password.trim()
  }

  dialogLoading.value = true
  try {
    users.value = await saveUsers(dialogMode.value === 'edit'
      ? { added: [], updated: [row], deleted: [] }
      : { added: [row], updated: [], deleted: [] }, { comCd: comCd.value })
    selectedUserId.value = userForm.userId.trim()
    dialogVisible.value = false
    await loadAffiliations()
  } finally {
    dialogLoading.value = false
  }
}

const handleDeleteUser = () => {
  if (!selectedUser.value) {
    return
  }
  confirm.require({
    message: t('users.deleteConfirm', undefined, { name: selectedUser.value.userName }),
    header: t('common.delete'),
    icon: 'pi pi-exclamation-triangle',
    acceptLabel: t('common.delete'),
    rejectLabel: t('common.close'),
    accept: async () => {
      users.value = await saveUsers({ added: [], updated: [], deleted: [selectedUser.value as unknown as Record<string, unknown>] }, { comCd: comCd.value })
      selectedUserId.value = users.value[0]?.userId ?? ''
      await loadAffiliations()
    },
  })
}

const handleSetPrimary = async (row: UserPositionRow) => {
  await savePrimaryYn({
    comCd: row.comCd,
    userId: row.userId,
    departmentId: row.departmentId,
    positionId: row.positionId,
    primaryYn: true,
  })
  await loadAffiliations()
}

watch(selectedUserId, async () => {
  await loadAffiliations()
})

watch(() => comCd.value, async () => {
  await loadAll()
}, { immediate: true })
</script>

<template>
  <div class="page-stack">
    <BasePageHeader title="menu.users" />
    <BaseForbiddenState v-if="!permissions.permitRead" />
    <template v-else>
      <BaseSearchForm
        :model-value="keyword"
        placeholder="userId, userName, email"
        title="search.title"
        @update:model-value="keyword = String($event ?? '')"
        @search="loadUsers"
      >
        <label class="inline-input">
          <span>{{ t('common.keyword') }}</span>
          <InputText :model-value="keyword" placeholder="userId, userName, email" @update:model-value="keyword = String($event ?? '')" />
        </label>
      </BaseSearchForm>

      <div class="admin-split-layout">
        <section class="split-panel">
          <header class="split-panel__header">
            <strong>{{ t('menu.users') }}</strong>
            <div class="split-panel__actions">
              <Button icon="pi pi-plus" :label="t('common.add')" size="small" :disabled="!permissions.permitWrite" @click="openCreate" />
              <Button icon="pi pi-pencil" :label="t('common.edit')" size="small" severity="secondary" :disabled="!permissions.permitWrite || !selectedUserId" @click="openEdit" />
              <Button icon="pi pi-trash" :label="t('common.delete')" size="small" severity="danger" :disabled="!permissions.permitDelete || !selectedUserId" @click="handleDeleteUser" />
              <Button icon="pi pi-refresh" :label="t('common.refresh')" size="small" severity="secondary" @click="loadAll" />
            </div>
          </header>
          <div class="selection-list">
            <button
              v-for="user in filteredUsers"
              :key="user.userId"
              type="button"
              class="selection-list__item"
              :class="{ 'is-active': user.userId === selectedUserId }"
              @click="selectedUserId = user.userId"
            >
              <strong>{{ user.userName }}</strong>
              <span>{{ user.userId }} / {{ user.jobGradeId }}</span>
            </button>
          </div>
        </section>

        <BaseEmptyState
          v-if="!selectedUserId"
          :title="t('menu.users')"
          :description="t('users.noSelectionDescription')"
        />

        <section v-else class="split-panel">
          <header class="split-panel__header">
            <strong>{{ t('common.department') }}</strong>
          </header>
          <div class="simple-admin-table-wrap">
            <table class="simple-admin-table">
              <thead>
                <tr>
                  <th>{{ t('common.department') }}</th>
                  <th>{{ t('common.position') }}</th>
                  <th>{{ t('common.primary') }}</th>
                  <th>{{ t('common.actions') }}</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="row in affiliations" :key="row.userPositionId">
                  <td>{{ row.departmentId }}</td>
                  <td>{{ row.positionId }}</td>
                  <td>{{ row.primaryYn ? t('common.yes') : t('common.no') }}</td>
                  <td>
                    <Button :label="t('common.save')" size="small" :disabled="row.primaryYn || !permissions.permitWrite" @click="handleSetPrimary(row)" />
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>
      </div>
    </template>

    <BaseDialog :visible="dialogVisible" :title="dialogMode === 'create' ? `${t('common.add')} ${t('menu.users')}` : `${t('common.edit')} ${t('menu.users')}`" @update:visible="dialogVisible = $event">
      <div class="form-popup-stack">
        <label class="inline-input"><span>{{ t('users.field.userId') }}</span><InputText v-model="userForm.userId" /></label>
        <label class="inline-input"><span>{{ t('users.field.userName') }}</span><InputText v-model="userForm.userName" /></label>
        <label class="inline-input">
          <span>{{ t('users.field.jobGrade') }}</span>
          <select v-model="userForm.jobGradeId" class="native-select">
            <option value="">{{ t('common.notSelected') }}</option>
            <option v-for="option in jobGradeOptions" :key="String(option.value)" :value="String(option.value ?? '')">{{ option.label }}</option>
          </select>
        </label>
        <label class="inline-input">
          <span>{{ t('common.status') }}</span>
          <select v-model="userForm.status" class="native-select">
            <option value="ACTIVE">{{ t('users.status.active') }}</option>
            <option value="LOCKED">{{ t('users.status.locked') }}</option>
            <option value="INACTIVE">{{ t('users.status.inactive') }}</option>
          </select>
        </label>
        <label class="inline-input"><span>{{ t('users.field.email') }}</span><InputText v-model="userForm.email" /></label>
        <label class="inline-input"><span>{{ t('users.field.phone') }}</span><InputText v-model="userForm.phone" /></label>
        <label class="inline-input"><span>{{ t('users.field.address') }}</span><InputText v-model="userForm.address" /></label>
        <label class="inline-input"><span>{{ t('users.field.password') }}</span><InputText v-model="userForm.password" type="password" /></label>
        <div class="dialog-actions">
          <Button :label="t('common.close')" severity="secondary" @click="dialogVisible = false" />
          <Button :label="t('common.save')" :loading="dialogLoading" @click="handleSaveUser" />
        </div>
      </div>
    </BaseDialog>
  </div>
</template>
