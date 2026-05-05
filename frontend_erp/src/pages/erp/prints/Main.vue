<script setup lang="ts">
import ErpResourceMain from '@/pages/erp/_shared/Main.vue'

defineProps<{
  resourceKey: string
}>()
</script>

<template>
  <ErpResourceMain :resource-key="resourceKey" />
</template>
