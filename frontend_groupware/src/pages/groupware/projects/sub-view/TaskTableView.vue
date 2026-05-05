<script setup lang="ts">
import BaseEmptyState from '@/components/common/BaseEmptyState.vue'
import GroupwarePanel from '@/components/groupware/GroupwarePanel.vue'
import GroupwareRecordTable from '@/components/groupware/GroupwareRecordTable.vue'
import { taskColumns } from '../model/pageModel'

defineProps<{
  title: string
  stats: Array<{ label: string; value: string }>
  rows: Array<Record<string, unknown>>
}>()
</script>

<template>
  <GroupwarePanel :title="title" description="Tasks for the selected project.">
    <template v-if="stats.length">
      <div class="trade-summary-strip">
        <div v-for="item in stats" :key="item.label" class="trade-summary-strip__item">
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
        </div>
      </div>

      <GroupwareRecordTable :columns="taskColumns" :rows="rows" row-key="taskId" />
    </template>
    <BaseEmptyState
      v-else
      title="No Project"
      description="Select a project to inspect related tasks."
    />
  </GroupwarePanel>
</template>
