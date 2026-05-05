<script setup lang="ts">
import Dialog from 'primevue/dialog'
import { useAppI18n } from '@/composables/useAppI18n'

defineProps<{
  visible: boolean
  title: string
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
}>()

const { t } = useAppI18n()
</script>

<template>
  <Dialog
    :visible="visible"
    modal
    :header="t(title, title)"
    class="base-dialog"
    @update:visible="emit('update:visible', $event)"
  >
    <slot />
  </Dialog>
</template>
