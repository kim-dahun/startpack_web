<script setup lang="ts">
import { computed } from 'vue'
import BaseDialog from '@/components/common/BaseDialog.vue'
import { useAppI18n } from '@/composables/useAppI18n'

const props = defineProps<{
  visible: boolean
  title: string
  items: string[]
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
}>()

const { t } = useAppI18n()
const dialogTitle = computed(() => `${t(props.title, props.title)} ${t('guide.structureSuffix')}`)
</script>

<template>
  <BaseDialog :visible="visible" :title="dialogTitle" @update:visible="emit('update:visible', $event)">
    <ul class="plain-list">
      <li v-for="item in items" :key="item">{{ item }}</li>
    </ul>
  </BaseDialog>
</template>
