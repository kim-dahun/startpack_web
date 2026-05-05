<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import { useConfirm } from 'primevue/useconfirm'
import { useToast } from 'primevue/usetoast'

import BaseDialog from '@/components/common/BaseDialog.vue'
import BaseEmptyState from '@/components/common/BaseEmptyState.vue'
import BasePageHeader from '@/components/common/BasePageHeader.vue'
import { useAppI18n } from '@/composables/useAppI18n'
import GroupwarePanel from '@/components/groupware/GroupwarePanel.vue'
import GroupwareRecordTable from '@/components/groupware/GroupwareRecordTable.vue'
import { useRealtimeStore } from '@/stores/realtime'
import { useSessionStore } from '@/stores/session'
import type { GroupwareChatMessage, GroupwareChatRoom, GroupwareChatRoomMember, GroupwareDirectoryUser } from '@/types/groupware'
import {
  addChatRoomMember,
  createChatRoom,
  createChatRoomMessage,
  deleteChatRoom,
  deleteChatRoomMessage,
  getChatRoomUnreadCount,
  leaveChatRoom,
  listChatRoomMembers,
  listChatRoomMessages,
  listChatRooms,
  listDirectoryUsers,
  markChatRoomRead,
  removeChatRoomMember,
  searchChatRoomMessages,
  updateChatRoom,
  updateChatRoomMessage,
} from './api/api'

const props = defineProps<{
  title: string
  description: string
  roomType: 'DIRECT' | 'GROUP'
}>()

const sessionStore = useSessionStore()
const realtimeStore = useRealtimeStore()
const toast = useToast()
const confirm = useConfirm()
const { t } = useAppI18n()

const permissions = computed(() => sessionStore.getPermissions(sessionStore.persisted.currentMenuId))
const currentUserId = computed(() => sessionStore.persisted.user?.userId ?? '')

const loading = ref(false)
const rooms = ref<GroupwareChatRoom[]>([])
const selectedRoomId = ref('')
const messages = ref<GroupwareChatMessage[]>([])
const members = ref<GroupwareChatRoomMember[]>([])
const unreadByRoom = ref<Record<string, number>>({})
const directoryUsers = ref<GroupwareDirectoryUser[]>([])
const roomDialogVisible = ref(false)
const memberDialogVisible = ref(false)
const renameDialogVisible = ref(false)
const editingMessageId = ref('')
const searchKeyword = ref('')
const roomForm = reactive({
  roomName: '',
  memberUserIds: [] as string[],
})
const memberUserId = ref('')
const messageDraft = ref('')
const renameRoomName = ref('')

const filteredRooms = computed(() => rooms.value.filter((room) => room.roomType === props.roomType))
const selectedRoom = computed(() => filteredRooms.value.find((room) => room.roomId === selectedRoomId.value) ?? null)
const availableUsers = computed(() =>
  directoryUsers.value.filter((user) => user.userId !== currentUserId.value),
)
const memberRows = computed(() => members.value.map((member) => ({
  userId: member.userId,
  joinedAt: member.joinedAt ? member.joinedAt.replace('T', ' ').slice(0, 16) : '-',
  lastReadMessageId: member.lastReadMessageId || '-',
})))
const messageRows = computed(() => messages.value.map((message) => ({
  messageId: message.messageId,
  author: message.createdByUserId,
  content: message.content,
  createdAt: message.createdAt.replace('T', ' ').slice(0, 16),
})))

const roomColumns = [
  { field: 'userId', title: 'common.user' },
  { field: 'joinedAt', title: 'common.createdAt' },
  { field: 'lastReadMessageId', title: 'Last Read' },
]

const messageColumns = [
  { field: 'author', title: 'common.author' },
  { field: 'content', title: 'common.message' },
  { field: 'createdAt', title: 'common.createdAt' },
]

function resetRoomForm() {
  roomForm.roomName = ''
  roomForm.memberUserIds = []
}

async function loadDirectoryUsers() {
  directoryUsers.value = await listDirectoryUsers()
}

async function loadRooms() {
  loading.value = true

  try {
    rooms.value = await listChatRooms()
    if (!filteredRooms.value.some((room) => room.roomId === selectedRoomId.value)) {
      selectedRoomId.value = filteredRooms.value[0]?.roomId ?? ''
    }

    const unreadEntries = await Promise.all(
      filteredRooms.value.map(async (room) => [room.roomId, await getChatRoomUnreadCount(room.roomId)] as const),
    )
    unreadByRoom.value = Object.fromEntries(unreadEntries)
  } finally {
    loading.value = false
  }
}

async function loadMessages() {
  if (!selectedRoomId.value) {
    messages.value = []
    return
  }

  messages.value = searchKeyword.value.trim()
    ? await searchChatRoomMessages(selectedRoomId.value, searchKeyword.value.trim())
    : await listChatRoomMessages(selectedRoomId.value)

  const lastMessageId = messages.value.at(-1)?.messageId
  if (lastMessageId) {
    await markChatRoomRead(selectedRoomId.value, { lastReadMessageId: lastMessageId })
  }
}

async function loadMembers() {
  if (!selectedRoomId.value) {
    members.value = []
    return
  }

  members.value = await listChatRoomMembers(selectedRoomId.value)
}

async function refreshCurrentRoom() {
  await Promise.all([loadMessages(), loadMembers(), loadRooms()])
}

async function handleCreateRoom() {
  if (!permissions.value.permitWrite || !roomForm.memberUserIds.length) {
    return
  }

  const created = await createChatRoom({
    roomType: props.roomType,
    roomName: props.roomType === 'GROUP' ? roomForm.roomName.trim() || null : null,
    memberUserIds: roomForm.memberUserIds,
  })

  roomDialogVisible.value = false
  resetRoomForm()
  await loadRooms()
  selectedRoomId.value = created?.roomId ?? ''
  await refreshCurrentRoom()
}

async function handleSendMessage() {
  if (!permissions.value.permitWrite || !selectedRoomId.value || !messageDraft.value.trim()) {
    return
  }

  if (editingMessageId.value) {
    await updateChatRoomMessage(selectedRoomId.value, editingMessageId.value, {
      messageType: 'TEXT',
      content: messageDraft.value.trim(),
    })
    editingMessageId.value = ''
  } else {
    await createChatRoomMessage(selectedRoomId.value, {
      messageType: 'TEXT',
      content: messageDraft.value.trim(),
    })
  }

  messageDraft.value = ''
  await refreshCurrentRoom()
}

function startEditMessage(rowMessageId: string) {
  const match = messages.value.find((message) => message.messageId === rowMessageId)
  if (!match || match.createdByUserId !== currentUserId.value || !permissions.value.permitWrite) {
    return
  }

  editingMessageId.value = match.messageId
  messageDraft.value = match.content
}

async function handleDeleteMessage(rowMessageId: string) {
  if (!selectedRoomId.value || !permissions.value.permitDelete) {
    return
  }

  await deleteChatRoomMessage(selectedRoomId.value, rowMessageId)
  if (editingMessageId.value === rowMessageId) {
    editingMessageId.value = ''
    messageDraft.value = ''
  }
  await refreshCurrentRoom()
}

async function handleAddMember() {
  if (!permissions.value.permitWrite || !selectedRoomId.value || !memberUserId.value) {
    return
  }

  await addChatRoomMember(selectedRoomId.value, { userId: memberUserId.value })
  memberDialogVisible.value = false
  memberUserId.value = ''
  await refreshCurrentRoom()
}

async function handleRemoveMember(userId: string) {
  if (!permissions.value.permitDelete || !selectedRoomId.value) {
    return
  }

  await removeChatRoomMember(selectedRoomId.value, userId)
  await refreshCurrentRoom()
}

function openRenameDialog() {
  renameRoomName.value = selectedRoom.value?.roomName ?? ''
  renameDialogVisible.value = true
}

async function handleRenameRoom() {
  if (!permissions.value.permitWrite || !selectedRoomId.value) {
    return
  }

  await updateChatRoom(selectedRoomId.value, {
    roomName: renameRoomName.value.trim() || null,
  })
  renameDialogVisible.value = false
  await refreshCurrentRoom()
}

function handleDeleteRoom() {
  if (!permissions.value.permitDelete || !selectedRoom.value) {
    return
  }

  confirm.require({
    message: t('common.confirm.deleteMessage', undefined, { name: selectedRoom.value.roomName || selectedRoom.value.roomId }),
    header: t('common.confirm.deleteTitle'),
    icon: 'pi pi-exclamation-triangle',
    acceptLabel: t('common.delete'),
    rejectLabel: t('common.cancel'),
    accept: async () => {
      await deleteChatRoom(selectedRoom.value!.roomId)
      selectedRoomId.value = ''
      await refreshCurrentRoom()
    },
  })
}

async function handleLeaveRoom() {
  if (!permissions.value.permitWrite || !selectedRoom.value) {
    return
  }

  await leaveChatRoom(selectedRoom.value.roomId)
  selectedRoomId.value = ''
  await refreshCurrentRoom()
}

onMounted(async () => {
  await Promise.all([loadDirectoryUsers(), loadRooms()])
  await refreshCurrentRoom()
})

watch(selectedRoomId, () => {
  void Promise.all([loadMessages(), loadMembers()])
})

watch(
  () => realtimeStore.latestEvent?.occurredAt,
  async () => {
    if (realtimeStore.latestEvent?.channel === 'messages') {
      await refreshCurrentRoom()
    }
  },
)
</script>

<template>
  <div class="page-stack">
    <BasePageHeader :title="title" :description="description" />

    <div class="admin-split-layout">
      <GroupwarePanel title="groupware.rooms" description="Create rooms, inspect unread counts, and select a conversation.">
        <template #actions>
          <Button
            v-if="permissions.permitWrite"
            icon="pi pi-plus"
            size="small"
            :label="t('groupware.createRoom')"
            @click="roomDialogVisible = true"
          />
        </template>

        <div v-if="filteredRooms.length" class="selection-list">
          <button
            v-for="room in filteredRooms"
            :key="room.roomId"
            type="button"
            class="selection-list__item"
            :class="{ 'is-active': room.roomId === selectedRoomId }"
            @click="selectedRoomId = room.roomId"
          >
            <strong>{{ room.roomName || room.roomId }}</strong>
            <span>{{ room.roomType }} / unread {{ unreadByRoom[room.roomId] ?? 0 }}</span>
          </button>
        </div>
        <BaseEmptyState
          v-else-if="!loading"
          :title="t('groupware.noRooms')"
          :description="t('groupware.noRoomsDesc')"
        />
      </GroupwarePanel>

      <div class="page-stack">
        <GroupwarePanel :title="selectedRoom?.roomName || t('groupware.conversation')" description="Send, search, edit, and delete messages.">
          <template #actions>
            <div class="trade-inline-actions">
              <Button
                v-if="selectedRoom?.roomType === 'GROUP' && permissions.permitWrite"
                size="small"
                severity="secondary"
                :label="t('groupware.addMember')"
                :disabled="!selectedRoomId"
                @click="memberDialogVisible = true"
              />
              <Button
                v-if="selectedRoom?.roomType === 'GROUP' && permissions.permitWrite"
                size="small"
                severity="secondary"
                :label="t('groupware.renameRoom')"
                :disabled="!selectedRoomId"
                @click="openRenameDialog"
              />
              <Button
                v-if="permissions.permitWrite"
                size="small"
                severity="secondary"
                :label="t('groupware.leave')"
                :disabled="!selectedRoomId"
                @click="handleLeaveRoom"
              />
              <Button
                v-if="permissions.permitDelete"
                size="small"
                severity="danger"
                :label="t('common.delete')"
                :disabled="!selectedRoomId"
                @click="handleDeleteRoom"
              />
            </div>
          </template>

          <div class="base-search-form__body">
            <div class="base-search-form__fields">
              <label class="inline-input">
                <span>{{ t('groupware.searchMessages') }}</span>
                <InputText v-model="searchKeyword" />
              </label>
            </div>
            <div class="base-search-form__actions">
              <Button :label="t('common.search')" severity="secondary" @click="loadMessages" />
              <Button :label="t('common.refresh')" @click="refreshCurrentRoom" />
            </div>
          </div>

          <GroupwareRecordTable
            v-if="messageRows.length"
            :columns="messageColumns"
            :rows="messageRows"
            row-key="messageId"
          >
            <template #actions="{ row }">
              <div class="trade-inline-actions">
                <Button
                  size="small"
                  severity="secondary"
                  :label="t('common.edit')"
                  :disabled="String(row.author) !== currentUserId || !permissions.permitWrite"
                  @click="startEditMessage(String(row.messageId))"
                />
                <Button
                  size="small"
                  severity="danger"
                  :label="t('common.delete')"
                  :disabled="String(row.author) !== currentUserId || !permissions.permitDelete"
                  @click="handleDeleteMessage(String(row.messageId))"
                />
              </div>
            </template>
          </GroupwareRecordTable>
          <BaseEmptyState
            v-else
            :title="t('groupware.noMessages')"
            :description="t('groupware.noMessagesDesc')"
          />

          <div class="trade-dialog-stack">
            <label class="inline-input">
              <span>{{ t('common.message') }}</span>
              <textarea v-model="messageDraft" class="trade-textarea"></textarea>
            </label>
            <div class="dialog-actions">
              <Button :label="t('common.refresh')" severity="secondary" @click="refreshCurrentRoom" />
              <Button :label="editingMessageId ? t('common.edit') : t('common.create')" :disabled="!permissions.permitWrite || !selectedRoomId" @click="handleSendMessage" />
            </div>
          </div>
        </GroupwarePanel>

        <GroupwarePanel title="groupware.members" description="Current room members and their read state.">
          <GroupwareRecordTable :columns="roomColumns" :rows="memberRows" row-key="userId">
            <template #actions="{ row }">
              <Button
                v-if="selectedRoom?.roomType === 'GROUP'"
                size="small"
                severity="danger"
                :label="t('groupware.removeMember')"
                :disabled="!permissions.permitDelete || String(row.userId) === currentUserId"
                @click="handleRemoveMember(String(row.userId))"
              />
            </template>
          </GroupwareRecordTable>
        </GroupwarePanel>
      </div>
    </div>

    <BaseDialog :visible="roomDialogVisible" :title="t('groupware.createRoom')" @update:visible="roomDialogVisible = $event">
      <div class="form-popup-stack">
        <label v-if="roomType === 'GROUP'" class="inline-input">
          <span>{{ t('groupware.roomName') }}</span>
          <InputText v-model="roomForm.roomName" />
        </label>
        <label class="inline-input">
          <span>{{ t('common.members') }}</span>
          <select
            multiple
            class="native-select"
            :value="roomForm.memberUserIds"
            @change="roomForm.memberUserIds = Array.from(($event.target as HTMLSelectElement).selectedOptions).map((option) => option.value)"
          >
            <option v-for="user in availableUsers" :key="user.userId" :value="user.userId">
              {{ user.userName }} ({{ user.userId }})
            </option>
          </select>
        </label>
        <div class="dialog-actions">
          <Button :label="t('common.close')" severity="secondary" @click="roomDialogVisible = false" />
          <Button :label="t('common.create')" :disabled="!permissions.permitWrite" @click="handleCreateRoom" />
        </div>
      </div>
    </BaseDialog>

    <BaseDialog :visible="memberDialogVisible" :title="t('groupware.addMember')" @update:visible="memberDialogVisible = $event">
      <div class="form-popup-stack">
        <label class="inline-input">
          <span>{{ t('common.user') }}</span>
          <select v-model="memberUserId" class="native-select">
            <option value="">{{ t('common.notSelected') }}</option>
            <option
              v-for="user in availableUsers.filter((candidate) => !members.some((member) => member.userId === candidate.userId))"
              :key="user.userId"
              :value="user.userId"
            >
              {{ user.userName }} ({{ user.userId }})
            </option>
          </select>
        </label>
        <div class="dialog-actions">
          <Button :label="t('common.close')" severity="secondary" @click="memberDialogVisible = false" />
          <Button :label="t('common.add')" :disabled="!permissions.permitWrite" @click="handleAddMember" />
        </div>
      </div>
    </BaseDialog>

    <BaseDialog :visible="renameDialogVisible" :title="t('groupware.renameRoom')" @update:visible="renameDialogVisible = $event">
      <div class="form-popup-stack">
        <label class="inline-input">
          <span>{{ t('groupware.roomName') }}</span>
          <InputText v-model="renameRoomName" />
        </label>
        <div class="dialog-actions">
          <Button :label="t('common.close')" severity="secondary" @click="renameDialogVisible = false" />
          <Button :label="t('common.save')" :disabled="!permissions.permitWrite" @click="handleRenameRoom" />
        </div>
      </div>
    </BaseDialog>
  </div>
</template>
