<script setup lang="ts">
import SharedGuidePopupView from '@/pages/erp/_shared/popup-view/GuidePopupView.vue'
import type { ErpResourceDefinition } from '@/types/erp'

defineProps<{
  visible: boolean
  definition: ErpResourceDefinition
  endpointText: string
}>()
</script>

<template>
  <SharedGuidePopupView v-bind="$props" />
</template>
