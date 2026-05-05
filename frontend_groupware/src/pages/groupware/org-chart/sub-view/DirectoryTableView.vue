<script setup lang="ts">
import InputText from 'primevue/inputtext'

import GroupwarePanel from '@/components/groupware/GroupwarePanel.vue'
import GroupwareRecordTable from '@/components/groupware/GroupwareRecordTable.vue'
import { directoryColumns } from '../model/pageModel'

defineProps<{
  keyword: string
  rows: Array<Record<string, unknown>>
}>()

const emit = defineEmits<{
  updateKeyword: [keyword: string]
  search: []
}>()
</script>

<template>
  <GroupwarePanel title="Directory" description="Primary department, position, and user source.">
    <template #actions>
      <InputText
        :model-value="keyword"
        placeholder="keyword"
        @update:model-value="emit('updateKeyword', String($event ?? ''))"
        @keyup.enter="emit('search')"
      />
    </template>

    <GroupwareRecordTable :columns="directoryColumns" :rows="rows" row-key="userId" />
  </GroupwarePanel>
</template>
