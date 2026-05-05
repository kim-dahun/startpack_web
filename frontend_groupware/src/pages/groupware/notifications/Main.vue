<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import Button from 'primevue/button'
import InputNumber from 'primevue/inputnumber'
import InputText from 'primevue/inputtext'
import { useToast } from 'primevue/usetoast'

import BaseDialog from '@/components/common/BaseDialog.vue'
import BasePageHeader from '@/components/common/BasePageHeader.vue'
import BaseStatCard from '@/components/common/BaseStatCard.vue'
import { useAppI18n } from '@/composables/useAppI18n'
import GroupwarePanel from '@/components/groupware/GroupwarePanel.vue'
import GroupwareRecordTable from '@/components/groupware/GroupwareRecordTable.vue'
import { useRealtimeStore } from '@/stores/realtime'
import { useSessionStore } from '@/stores/session'
import type { GroupwareDirectoryUser, GroupwareNotificationItem } from '@/types/groupware'
import {
  archiveAllNotifications,
  archiveNotification,
  createNotification,
  deleteNotification,
  fetchNotificationUnreadCount,
  listArchivedNotifications,
  listDirectoryUsers,
  listNotifications,
  markAllNotificationsRead,
  markNotificationRead,
  purgeExpiredNotifications,
  searchNotifications,
} from './api/api'

type NotificationTab = 'active' | 'archive'

const sessionStore = useSessionStore()
const realtimeStore = useRealtimeStore()
const toast = useToast()
const { t } = useAppI18n()

const permissions = computed(() => sessionStore.getPermissions(sessionStore.persisted.currentMenuId))

const activeTab = ref<NotificationTab>('active')
const notifications = ref<GroupwareNotificationItem[]>([])
const archiveRows = ref<GroupwareNotificationItem[]>([])
const unreadCount = ref(0)
const directoryUsers = ref<GroupwareDirectoryUser[]>([])
const dialogVisible = ref(false)
const retentionDays = ref(30)

const form = reactive({
  targetUserId: '',
  title: '',
  content: '',
  referenceType: 'GENERAL',
  referenceId: '',
})

const searchForm = reactive({
  status: '',
  referenceType: '',
  from: '',
  to: '',
})

const currentRows = computed(() => (activeTab.value === 'active' ? notifications.value : archiveRows.value).map((item) => ({
  notificationId: item.notificationId,
  title: item.title,
  status: item.status,
  referenceType: item.referenceType || '-',
  createdAt: item.createdAt.replace('T', ' ').slice(0, 16),
})))
const userOptions = computed(() => directoryUsers.value.map((user) => ({
  label: `${user.userName} (${user.userId})`,
  value: user.userId,
})))

const columns = [
  { field: 'title', title: 'common.title' },
  { field: 'status', title: 'common.status' },
  { field: 'referenceType', title: 'common.type' },
  { field: 'createdAt', title: 'common.createdAt' },
]

async function loadRows() {
  const [list, unread, archived] = await Promise.all([
    listNotifications(),
    fetchNotificationUnreadCount(),
    listArchivedNotifications(),
  ])

  notifications.value = list
  unreadCount.value = unread
  archiveRows.value = archived
}

async function handleSearch() {
  notifications.value = await searchNotifications({
    status: searchForm.status || undefined,
    referenceType: searchForm.referenceType.trim() || undefined,
    from: searchForm.from || undefined,
    to: searchForm.to || undefined,
  })
  unreadCount.value = await fetchNotificationUnreadCount()
}

async function loadDirectoryUsers() {
  directoryUsers.value = await listDirectoryUsers()
}

function resetForm() {
  form.targetUserId = ''
  form.title = ''
  form.content = ''
  form.referenceType = 'GENERAL'
  form.referenceId = ''
}

async function handleCreateNotification() {
  if (!permissions.value.permitWrite || !form.targetUserId || !form.title.trim()) {
    return
  }

  await createNotification({
    targetUserId: form.targetUserId,
    title: form.title.trim(),
    content: form.content.trim(),
    referenceType: form.referenceType.trim(),
    referenceId: form.referenceId.trim(),
  })
  dialogVisible.value = false
  resetForm()
  await loadRows()
  toast.add({ severity: 'success', summary: t('common.summary.created'), detail: t('common.detail.created'), life: 2200 })
}

async function handleMarkRead(notificationId: string) {
  await markNotificationRead(notificationId)
  await loadRows()
}

async function handleMarkAllRead() {
  if (!permissions.value.permitWrite) {
    return
  }

  const result = await markAllNotificationsRead()
  await loadRows()
  toast.add({
    severity: 'success',
    summary: t('common.summary.updated'),
    detail: `${result?.updatedCount ?? 0}`,
    life: 2200,
  })
}

async function handleArchive(notificationId: string) {
  if (!permissions.value.permitDelete) {
    return
  }

  await archiveNotification(notificationId, Number(retentionDays.value ?? 30))
  await loadRows()
}

async function handleArchiveAll() {
  if (!permissions.value.permitDelete) {
    return
  }

  const result = await archiveAllNotifications(Number(retentionDays.value ?? 30))
  await loadRows()
  toast.add({
    severity: 'success',
    summary: t('common.summary.applied'),
    detail: `${result?.updatedCount ?? 0}`,
    life: 2200,
  })
}

async function handleDelete(notificationId: string) {
  if (!permissions.value.permitDelete) {
    return
  }

  await deleteNotification(notificationId)
  await loadRows()
  toast.add({ severity: 'success', summary: t('common.summary.deleted'), detail: t('common.detail.deleted'), life: 2200 })
}

async function handlePurgeExpired() {
  if (!permissions.value.permitDelete) {
    return
  }

  const result = await purgeExpiredNotifications()
  await loadRows()
  toast.add({
    severity: 'success',
    summary: t('common.summary.purged'),
    detail: `Deleted ${result?.deletedCount ?? 0}`,
    life: 2200,
  })
}

onMounted(async () => {
  await Promise.all([loadRows(), loadDirectoryUsers()])
})

watch(
  () => realtimeStore.latestEvent?.occurredAt,
  async () => {
    if (realtimeStore.latestEvent?.channel === 'notifications') {
      await loadRows()
    }
  },
)
</script>

<template>
  <div class="page-stack">
    <BasePageHeader title="groupware.notifications" description="Notification create, search, read, archive, delete, and purge flows wired to backend_groupware." />

    <section class="stats-grid">
      <BaseStatCard :label="t('groupware.status.unreadCount')" :value="String(unreadCount)" />
      <BaseStatCard :label="t('groupware.status.activeNotifications')" :value="String(notifications.length)" />
      <BaseStatCard :label="t('groupware.status.archived')" :value="String(archiveRows.length)" />
    </section>

    <GroupwarePanel title="groupware.notificationStream" description="Permission-aware create, search, read, archive, delete, and archive purge controls.">
      <template #actions>
        <div class="trade-inline-actions">
          <Button
            v-for="tab in ['active', 'archive']"
            :key="tab"
            size="small"
            :severity="activeTab === tab ? 'contrast' : 'secondary'"
            :label="t(`groupware.${tab}`)"
            @click="activeTab = tab as NotificationTab"
          />
          <Button v-if="permissions.permitWrite" size="small" icon="pi pi-plus" :label="t('common.create')" @click="dialogVisible = true" />
          <Button v-if="permissions.permitWrite" size="small" severity="secondary" :label="t('groupware.markAllRead')" @click="handleMarkAllRead" />
          <Button v-if="permissions.permitDelete" size="small" severity="secondary" :label="t('groupware.archiveAll')" @click="handleArchiveAll" />
          <Button v-if="permissions.permitDelete" size="small" severity="danger" :label="t('groupware.purgeExpired')" @click="handlePurgeExpired" />
        </div>
      </template>

      <div class="base-search-form__body">
        <div class="base-search-form__fields">
          <label class="inline-input">
            <span>{{ t('common.status') }}</span>
            <select v-model="searchForm.status" class="native-select">
              <option value="">{{ t('common.all') }}</option>
              <option value="UNREAD">UNREAD</option>
              <option value="READ">READ</option>
            </select>
          </label>
          <label class="inline-input">
            <span>{{ t('common.type') }}</span>
            <InputText v-model="searchForm.referenceType" />
          </label>
          <label class="inline-input">
            <span>{{ t('common.from') }}</span>
            <input v-model="searchForm.from" class="native-select" type="datetime-local">
          </label>
          <label class="inline-input">
            <span>{{ t('common.to') }}</span>
            <input v-model="searchForm.to" class="native-select" type="datetime-local">
          </label>
          <label class="inline-input">
            <span>{{ t('common.retentionDays') }}</span>
            <InputNumber v-model="retentionDays" :min="1" fluid />
          </label>
        </div>
        <div class="base-search-form__actions">
          <Button :label="t('common.search')" severity="secondary" @click="handleSearch" />
          <Button :label="t('common.refresh')" @click="loadRows" />
        </div>
      </div>

      <GroupwareRecordTable :columns="columns" :rows="currentRows" row-key="notificationId">
        <template #actions="{ row }">
          <div class="trade-inline-actions">
            <Button
              v-if="activeTab === 'active'"
              size="small"
              severity="secondary"
              :label="t('groupware.markRead')"
              :disabled="String(row.status) === 'READ'"
              @click="handleMarkRead(String(row.notificationId))"
            />
            <Button
              v-if="activeTab === 'active' && permissions.permitDelete"
              size="small"
              severity="secondary"
              :label="t('groupware.archive')"
              @click="handleArchive(String(row.notificationId))"
            />
            <Button
              v-if="permissions.permitDelete"
              size="small"
              severity="danger"
              :label="t('common.delete')"
              @click="handleDelete(String(row.notificationId))"
            />
          </div>
        </template>
      </GroupwareRecordTable>
    </GroupwarePanel>

    <BaseDialog :visible="dialogVisible" :title="t('common.create')" @update:visible="dialogVisible = $event">
      <div class="form-popup-stack">
        <label class="inline-input">
          <span>{{ t('common.user') }}</span>
          <select v-model="form.targetUserId" class="native-select">
            <option value="">{{ t('common.notSelected') }}</option>
            <option v-for="user in userOptions" :key="user.value" :value="user.value">{{ user.label }}</option>
          </select>
        </label>
        <label class="inline-input">
          <span>{{ t('common.title') }}</span>
          <InputText v-model="form.title" />
        </label>
        <label class="inline-input">
          <span>{{ t('common.content') }}</span>
          <textarea v-model="form.content" class="trade-textarea"></textarea>
        </label>
        <label class="inline-input">
          <span>{{ t('common.type') }}</span>
          <InputText v-model="form.referenceType" />
        </label>
        <label class="inline-input">
          <span>Reference Id</span>
          <InputText v-model="form.referenceId" />
        </label>
        <div class="dialog-actions">
          <Button :label="t('common.close')" severity="secondary" @click="dialogVisible = false" />
          <Button :label="t('common.create')" :disabled="!permissions.permitWrite" @click="handleCreateNotification" />
        </div>
      </div>
    </BaseDialog>
  </div>
</template>
