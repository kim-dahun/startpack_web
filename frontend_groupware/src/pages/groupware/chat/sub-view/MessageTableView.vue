<script setup lang="ts">
import BaseEmptyState from '@/components/common/BaseEmptyState.vue'
import GroupwarePanel from '@/components/groupware/GroupwarePanel.vue'
import GroupwareRecordTable from '@/components/groupware/GroupwareRecordTable.vue'

defineProps<{
  title: string
  rows: Array<Record<string, unknown>>
  rowKey?: string
}>()
</script>

<template>
  <GroupwarePanel :title="title" description="Recent messages from the current room.">
    <GroupwareRecordTable
      v-if="rows.length"
      :columns="[
        { field: 'author', title: 'Author' },
        { field: 'content', title: 'Message' },
        { field: 'createdAt', title: 'Created At' },
      ]"
      :rows="rows"
      :row-key="rowKey"
    />
    <BaseEmptyState
      v-else
      title="No Messages"
      description="Select a room with available message history."
    />
  </GroupwarePanel>
</template>
