<script setup lang="ts">
import BaseManagementGuidePopupView from '@/components/common/BaseManagementGuidePopupView.vue'

defineProps<{
  visible: boolean
  title: string
  items: string[]
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
}>()
</script>

<template>
  <BaseManagementGuidePopupView
    :visible="visible"
    :title="title"
    :items="items"
    @update:visible="emit('update:visible', $event)"
  />
</template>
