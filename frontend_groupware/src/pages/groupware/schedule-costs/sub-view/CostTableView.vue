<script setup lang="ts">
import GroupwarePanel from '@/components/groupware/GroupwarePanel.vue'
import GroupwareRecordTable from '@/components/groupware/GroupwareRecordTable.vue'
import { costColumns } from '../model/pageModel'

defineProps<{
  rows: Array<Record<string, unknown>>
  selectedProjectCode: string
  projectOptions: Array<{ label: string; value: string }>
}>()

const emit = defineEmits<{
  selectProject: [projectCode: string]
}>()
</script>

<template>
  <GroupwarePanel title="Project Cost Ledger" description="Expense rows attached to schedule activity.">
    <template #actions>
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

    <GroupwareRecordTable :columns="costColumns" :rows="rows" row-key="scheduleCostId" />
  </GroupwarePanel>
</template>
