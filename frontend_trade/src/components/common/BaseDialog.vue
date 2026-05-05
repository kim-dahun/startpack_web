<script setup lang="ts">
import Dialog from 'primevue/dialog'

defineProps<{
  visible: boolean
  title: string
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
}>()
</script>

<template>
  <Dialog
    :visible="visible"
    modal
    :header="title"
    class="base-dialog"
    @update:visible="emit('update:visible', $event)"
  >
    <slot />
  </Dialog>
</template>
