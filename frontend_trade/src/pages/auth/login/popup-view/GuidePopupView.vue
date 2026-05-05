<script setup lang="ts">
import BaseDialog from '@/components/common/BaseDialog.vue'

defineProps<{
  visible: boolean
  items: string[]
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
}>()
</script>

<template>
  <BaseDialog :visible="visible" title="로그인 구조" @update:visible="emit('update:visible', $event)">
    <ul class="plain-list">
      <li v-for="item in items" :key="item">{{ item }}</li>
    </ul>
  </BaseDialog>
</template>
