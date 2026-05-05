<script setup lang="ts">
import BaseEmptyState from '@/components/common/BaseEmptyState.vue'
import GroupwarePanel from '@/components/groupware/GroupwarePanel.vue'
import type { GroupwareChatRoom } from '@/types/groupware'

defineProps<{
  rooms: GroupwareChatRoom[]
  selectedRoomId: string
  unreadByRoom: Record<string, number>
  loading?: boolean
}>()

const emit = defineEmits<{
  select: [roomId: string]
}>()
</script>

<template>
  <GroupwarePanel title="Rooms" description="Select a room to inspect the latest conversation slice.">
    <div v-if="rooms.length" class="selection-list">
      <button
        v-for="room in rooms"
        :key="room.roomId"
        type="button"
        class="selection-list__item"
        :class="{ 'is-active': room.roomId === selectedRoomId }"
        @click="emit('select', room.roomId)"
      >
        <strong>{{ room.roomName || room.roomId }}</strong>
        <span>{{ room.roomType }} / unread {{ unreadByRoom[room.roomId] ?? 0 }}</span>
      </button>
    </div>
    <BaseEmptyState
      v-else-if="!loading"
      title="No Rooms"
      description="No chat rooms were returned for the selected category."
    />
  </GroupwarePanel>
</template>
