<script setup lang="ts">
import InputText from 'primevue/inputtext'

import BaseSearchForm from '@/components/form/BaseSearchForm.vue'

defineProps<{
  keyword: string
  selectedType: string
}>()

const emit = defineEmits<{
  'update:keyword': [value: string]
  'update:selectedType': [value: string]
  search: []
}>()
</script>

<template>
  <BaseSearchForm
    :model-value="keyword"
    placeholder="jobGradeId, jobGradeName"
    title="Search Conditions"
    @update:model-value="emit('update:keyword', String($event ?? ''))"
    @search="emit('search')"
  >
    <label class="inline-input">
      <span>jobGradeType</span>
      <select
        :value="selectedType"
        class="native-select"
        @change="emit('update:selectedType', String(($event.target as HTMLSelectElement).value))"
      >
        <option value="">Select Type</option>
        <option value="GENERAL">GENERAL</option>
        <option value="RESEARCH">RESEARCH</option>
        <option value="CUSTOM">CUSTOM</option>
      </select>
    </label>
    <label class="inline-input">
      <span>Keyword</span>
      <InputText
        :model-value="keyword"
        placeholder="jobGradeId, jobGradeName"
        @update:model-value="emit('update:keyword', String($event ?? ''))"
      />
    </label>
  </BaseSearchForm>
</template>
