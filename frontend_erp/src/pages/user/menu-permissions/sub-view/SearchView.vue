<script setup lang="ts">
import InputText from 'primevue/inputtext'

import BaseSearchForm from '@/components/form/BaseSearchForm.vue'

defineProps<{
  keyword: string
  selectedServiceId: string
  serviceOptions: string[]
}>()

const emit = defineEmits<{
  'update:keyword': [value: string]
  'update:selectedServiceId': [value: string]
  search: []
}>()
</script>

<template>
  <BaseSearchForm
    :model-value="keyword"
    placeholder="menuId, menuName"
    title="Search Conditions"
    @update:model-value="emit('update:keyword', String($event ?? ''))"
    @search="emit('search')"
  >
    <label class="inline-input">
      <span>serviceId</span>
      <select
        :value="selectedServiceId"
        class="native-select"
        @change="emit('update:selectedServiceId', String(($event.target as HTMLSelectElement).value))"
      >
        <option v-for="serviceId in serviceOptions" :key="serviceId" :value="serviceId">
          {{ serviceId }}
        </option>
      </select>
    </label>
    <label class="inline-input">
      <span>Keyword</span>
      <InputText
        :model-value="keyword"
        placeholder="menuId, menuName"
        @update:model-value="emit('update:keyword', String($event ?? ''))"
      />
    </label>
  </BaseSearchForm>
</template>
