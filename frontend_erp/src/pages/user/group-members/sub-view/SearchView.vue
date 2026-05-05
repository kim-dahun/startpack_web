<script setup lang="ts">
import BaseManagementSearchView from '@/components/common/BaseManagementSearchView.vue'

defineProps<{
  keyword: string
  placeholder: string
  requiresUserId?: boolean
  selectedUserId: string
  serviceScoped?: boolean
  selectedServiceId: string
  serviceOptions: string[]
  selectedGroupId: string
  groupOptions: string[]
}>()

const emit = defineEmits<{
  'update:keyword': [value: string]
  'update:selectedUserId': [value: string]
  'update:selectedServiceId': [value: string]
  'update:selectedGroupId': [value: string]
  search: []
  openGuide: []
}>()
</script>

<template>
  <BaseManagementSearchView
    :keyword="keyword"
    :placeholder="placeholder"
    :requires-user-id="requiresUserId"
    :selected-user-id="selectedUserId"
    :service-scoped="serviceScoped"
    :selected-service-id="selectedServiceId"
    :service-options="serviceOptions"
    :selected-group-id="selectedGroupId"
    :group-options="groupOptions"
    @update:keyword="emit('update:keyword', $event)"
    @update:selected-user-id="emit('update:selectedUserId', $event)"
    @update:selected-service-id="emit('update:selectedServiceId', $event)"
    @update:selected-group-id="emit('update:selectedGroupId', $event)"
    @search="emit('search')"
    @open-guide="emit('openGuide')"
  />
</template>
