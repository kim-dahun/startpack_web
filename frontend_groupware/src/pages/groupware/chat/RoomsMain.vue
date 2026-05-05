<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import { useConfirm } from 'primevue/useconfirm'
import { useToast } from 'primevue/usetoast'

import BaseDialog from '@/components/common/BaseDialog.vue'
import BasePageHeader from '@/components/common/BasePageHeader.vue'
import BaseStatCard from '@/components/common/BaseStatCard.vue'
import { useAppI18n } from '@/composables/useAppI18n'
import GroupwarePanel from '@/components/groupware/GroupwarePanel.vue'
import GroupwareRecordTable from '@/components/groupware/GroupwareRecordTable.vue'
import { useRealtimeStore } from '@/stores/realtime'
import { useSessionStore } from '@/stores/session'
import type { GroupwareChatRoom, GroupwareDirectoryUser } from '@/types/groupware'
import { createChatRoom, deleteChatRoom, leaveChatRoom, listChatRooms, listDirectoryUsers } from './api/api'

const sessionStore = useSessionStore()
const realtimeStore = useRealtimeStore()
const toast = useToast()
const confirm = useConfirm()
const { t } = useAppI18n()

const permissions = computed(() => sessionStore.getPermissions(sessionStore.persisted.currentMenuId))
const currentUserId = computed(() => sessionStore.persisted.user?.userId ?? '')

const rooms = ref<GroupwareChatRoom[]>([])
const directoryUsers = ref<GroupwareDirectoryUser[]>([])
const dialogVisible = ref(false)

const form = reactive({
  roomType: 'GROUP' as 'DIRECT' | 'GROUP',
  roomName: '',
  memberUserIds: [] as string[],
})

const stats = computed(() => {
  const directRooms = rooms.value.filter((room) => room.roomType === 'DIRECT').length
  const groupRooms = rooms.value.filter((room) => room.roomType === 'GROUP').length

  return [
    { label: t('groupware.status.totalRooms'), value: String(rooms.value.length) },
    { label: t('groupware.status.directRooms'), value: String(directRooms) },
    { label: t('groupware.status.groupRooms'), value: String(groupRooms) },
  ]
})

const roomRows = computed(() => rooms.value.map((room) => ({
  roomId: room.roomId,
  roomType: room.roomType,
  roomName: room.roomName || '-',
  createdByUserId: room.createdByUserId,
})))

const roomColumns = [
  { field: 'roomId', title: 'Room ID' },
  { field: 'roomType', title: 'common.type' },
  { field: 'roomName', title: 'groupware.roomName' },
  { field: 'createdByUserId', title: 'common.owner' },
]

const userOptions = computed(() =>
  directoryUsers.value
    .filter((user) => user.userId !== currentUserId.value)
    .map((user) => ({
      label: `${user.userName} (${user.userId})`,
      value: user.userId,
    })),
)

async function loadRows() {
  rooms.value = await listChatRooms()
}

async function loadDirectoryUsers() {
  directoryUsers.value = await listDirectoryUsers()
}

function resetForm() {
  form.roomType = 'GROUP'
  form.roomName = ''
  form.memberUserIds = []
}

async function handleCreateRoom() {
  if (!permissions.value.permitWrite || !form.memberUserIds.length) {
    return
  }

  await createChatRoom({
    roomType: form.roomType,
    roomName: form.roomType === 'GROUP' ? form.roomName.trim() || null : null,
    memberUserIds: form.memberUserIds,
  })
  dialogVisible.value = false
  resetForm()
  await loadRows()
  toast.add({
    severity: 'success',
    summary: t('common.summary.created'),
    detail: t('common.detail.created'),
    life: 2200,
  })
}

function handleDeleteRoom(roomId: string) {
  if (!permissions.value.permitDelete) {
    return
  }

  confirm.require({
    message: `${roomId} will be deleted.`,
    header: t('common.confirm.deleteTitle'),
    icon: 'pi pi-exclamation-triangle',
    acceptLabel: t('common.delete'),
    rejectLabel: t('common.cancel'),
    accept: async () => {
      await deleteChatRoom(roomId)
      await loadRows()
    },
  })
}

async function handleLeaveRoom(roomId: string) {
  if (!permissions.value.permitWrite) {
    return
  }

  await leaveChatRoom(roomId)
  await loadRows()
}

onMounted(async () => {
  await Promise.all([loadRows(), loadDirectoryUsers()])
})

watch(
  () => realtimeStore.latestEvent?.occurredAt,
  async () => {
    if (realtimeStore.latestEvent?.channel === 'messages') {
      await loadRows()
    }
  },
)
</script>

<template>
  <div class="page-stack">
    <BasePageHeader title="groupware.rooms" description="Room registry with create, leave, and delete actions." />

    <section class="stats-grid">
      <BaseStatCard v-for="item in stats" :key="item.label" :label="item.label" :value="item.value" />
    </section>

    <GroupwarePanel title="groupware.roomInventory" description="Connected to groupware chat room APIs.">
      <template #actions>
        <Button v-if="permissions.permitWrite" size="small" icon="pi pi-plus" :label="t('groupware.createRoom')" @click="dialogVisible = true" />
      </template>

      <GroupwareRecordTable :columns="roomColumns" :rows="roomRows" row-key="roomId">
        <template #actions="{ row }">
          <div class="trade-inline-actions">
            <Button size="small" severity="secondary" :label="t('groupware.leave')" :disabled="!permissions.permitWrite" @click="handleLeaveRoom(String(row.roomId))" />
            <Button size="small" severity="danger" :label="t('common.delete')" :disabled="!permissions.permitDelete" @click="handleDeleteRoom(String(row.roomId))" />
          </div>
        </template>
      </GroupwareRecordTable>
    </GroupwarePanel>

    <BaseDialog :visible="dialogVisible" :title="t('groupware.createRoom')" @update:visible="dialogVisible = $event">
      <div class="form-popup-stack">
        <label class="inline-input">
          <span>{{ t('groupware.roomType') }}</span>
          <select v-model="form.roomType" class="native-select">
            <option value="DIRECT">DIRECT</option>
            <option value="GROUP">GROUP</option>
          </select>
        </label>
        <label v-if="form.roomType === 'GROUP'" class="inline-input">
          <span>{{ t('groupware.roomName') }}</span>
          <InputText v-model="form.roomName" />
        </label>
        <label class="inline-input">
          <span>{{ t('common.members') }}</span>
          <select
            multiple
            class="native-select"
            :value="form.memberUserIds"
            @change="form.memberUserIds = Array.from(($event.target as HTMLSelectElement).selectedOptions).map((option) => option.value)"
          >
            <option v-for="user in userOptions" :key="user.value" :value="user.value">{{ user.label }}</option>
          </select>
        </label>
        <div class="dialog-actions">
          <Button :label="t('common.close')" severity="secondary" @click="dialogVisible = false" />
          <Button :label="t('common.create')" :disabled="!permissions.permitWrite" @click="handleCreateRoom" />
        </div>
      </div>
    </BaseDialog>
  </div>
</template>
