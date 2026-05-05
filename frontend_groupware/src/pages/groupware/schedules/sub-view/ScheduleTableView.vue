<script setup lang="ts">
import GroupwarePanel from '@/components/groupware/GroupwarePanel.vue'
import GroupwareRecordTable from '@/components/groupware/GroupwareRecordTable.vue'
import { scheduleColumns } from '../model/pageModel'

defineProps<{
  rows: Array<Record<string, unknown>>
  projectMode: boolean
  selectedProjectCode: string
  projectOptions: Array<{ label: string; value: string }>
}>()

const emit = defineEmits<{
  selectProject: [projectCode: string]
}>()
</script>

<template>
  <GroupwarePanel title="Schedule Feed" description="Connected to Groupware schedule APIs.">
    <template v-if="projectMode" #actions>
      <select
        :value="selectedProjectCode"
        class="native-select"
        @change="emit('selectProject', String(($event.target as HTMLSelectElement).value))"
      >
        <option value="">Select Project</option>
        <option v-for="project in projectOptions" :key="project.value" :value="project.value">
          {{ project.label }}
        </option>
      </select>
    </template>

    <GroupwareRecordTable :columns="scheduleColumns" :rows="rows" row-key="scheduleId" />
  </GroupwarePanel>
</template>
