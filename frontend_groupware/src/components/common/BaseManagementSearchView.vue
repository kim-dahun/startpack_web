<script setup lang="ts">
import InputText from 'primevue/inputtext'

import BaseSearchForm from '@/components/form/BaseSearchForm.vue'

defineProps<{
  keyword: string
  placeholder: string
  requiresUserId?: boolean
  selectedUserId: string
  serviceScoped?: boolean
  selectedServiceId: string
  serviceOptions: string[]
  selectedGroupId?: string
  groupOptions?: string[]
  selectedCodeGroupId?: string
  codeGroupOptions?: string[]
}>()

const emit = defineEmits<{
  'update:keyword': [value: string]
  'update:selectedUserId': [value: string]
  'update:selectedServiceId': [value: string]
  'update:selectedGroupId': [value: string]
  'update:selectedCodeGroupId': [value: string]
  search: []
  openGuide: []
}>()
</script>

<template>
  <BaseSearchForm
    :model-value="keyword"
    :placeholder="placeholder"
    title="Search Conditions"
    @update:model-value="emit('update:keyword', String($event ?? ''))"
    @search="emit('search')"
  >
    <InputText
      :model-value="keyword"
      :placeholder="placeholder"
      @update:model-value="emit('update:keyword', String($event ?? ''))"
    />
    <label v-if="requiresUserId" class="inline-input">
      <span>userId</span>
      <InputText
        :model-value="selectedUserId"
        placeholder="Target userId"
        @update:model-value="emit('update:selectedUserId', String($event ?? ''))"
      />
    </label>
    <label v-if="serviceScoped" class="inline-input">
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
    <label v-if="groupOptions?.length" class="inline-input">
      <span>groupId</span>
      <select
        :value="selectedGroupId"
        class="native-select"
        @change="emit('update:selectedGroupId', String(($event.target as HTMLSelectElement).value))"
      >
        <option v-for="groupId in groupOptions" :key="groupId" :value="groupId">
          {{ groupId }}
        </option>
      </select>
    </label>
    <label v-if="codeGroupOptions?.length" class="inline-input">
      <span>codeGroupId</span>
      <select
        :value="selectedCodeGroupId"
        class="native-select"
        @change="emit('update:selectedCodeGroupId', String(($event.target as HTMLSelectElement).value))"
      >
        <option v-for="codeGroupId in codeGroupOptions" :key="codeGroupId" :value="codeGroupId">
          {{ codeGroupId }}
        </option>
      </select>
    </label>
  </BaseSearchForm>
</template>
