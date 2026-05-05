<script setup lang="ts">
import GroupwarePanel from '@/components/groupware/GroupwarePanel.vue'
import GroupwareRecordTable from '@/components/groupware/GroupwareRecordTable.vue'
import { approvalLineColumns } from '../model/pageModel'

defineProps<{
  title: string
  rows: Array<Record<string, unknown>>
  templateLabels: string[]
  showTemplates: boolean
}>()
</script>

<template>
  <GroupwarePanel :title="title" description="Document lines plus reusable templates.">
    <GroupwareRecordTable :columns="approvalLineColumns" :rows="rows" row-key="approvalLineId" />

    <div v-if="showTemplates" class="plain-list">
      <strong>Templates</strong>
      <span v-for="label in templateLabels" :key="label">{{ label }}</span>
    </div>
  </GroupwarePanel>
</template>
