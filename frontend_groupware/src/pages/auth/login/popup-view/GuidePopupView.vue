<script setup lang="ts">
import BaseDialog from '@/components/common/BaseDialog.vue'
import { useAppI18n } from '@/composables/useAppI18n'

defineProps<{
  visible: boolean
  items: string[]
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
}>()

const { t } = useAppI18n()
</script>

<template>
  <BaseDialog :visible="visible" :title="t('guide.login')" @update:visible="emit('update:visible', $event)">
    <ul class="plain-list">
      <li v-for="item in items" :key="item">{{ item }}</li>
    </ul>
  </BaseDialog>
</template>
