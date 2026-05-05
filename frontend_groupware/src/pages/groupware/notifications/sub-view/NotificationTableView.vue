<script setup lang="ts">
import Button from 'primevue/button'

import GroupwarePanel from '@/components/groupware/GroupwarePanel.vue'
import GroupwareRecordTable from '@/components/groupware/GroupwareRecordTable.vue'
import { notificationColumns } from '../model/pageModel'

defineProps<{
  rows: Array<Record<string, unknown>>
}>()

const emit = defineEmits<{
  markRead: [notificationId: string]
}>()
</script>

<template>
  <GroupwarePanel title="Notification Stream" description="Backend_groupware notification archive and read state.">
    <GroupwareRecordTable :columns="notificationColumns" :rows="rows" row-key="notificationId">
      <template #actions="{ row }">
        <Button
          size="small"
          severity="secondary"
          label="Mark Read"
          :disabled="String(row.status) === 'READ'"
          @click="emit('markRead', String(row.notificationId))"
        />
      </template>
    </GroupwareRecordTable>
  </GroupwarePanel>
</template>
