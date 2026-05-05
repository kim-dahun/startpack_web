<script setup lang="ts">
import Button from 'primevue/button'
import Checkbox from 'primevue/checkbox'
import InputText from 'primevue/inputtext'

import BaseDialog from '@/components/common/BaseDialog.vue'
import type { CodeGroupForm } from '../model/pageModel'

defineProps<{
  visible: boolean
  title: string
  form: CodeGroupForm
  loading: boolean
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  save: []
}>()
</script>

<template>
  <BaseDialog :visible="visible" :title="title" @update:visible="emit('update:visible', $event)">
    <div class="form-popup-stack">
      <label class="inline-input">
        <span>Code Group ID</span>
        <InputText v-model="form.codeGroupId" />
      </label>
      <label class="inline-input">
        <span>Code Group Name</span>
        <InputText v-model="form.codeGroupName" />
      </label>
      <label class="inline-input">
        <span>Description</span>
        <InputText v-model="form.description" />
      </label>
      <label class="checkbox-line">
        <Checkbox v-model="form.enabled" binary />
        <span>Enabled</span>
      </label>
      <div class="dialog-actions">
        <Button label="Close" severity="secondary" @click="emit('update:visible', false)" />
        <Button label="Save" :loading="loading" @click="emit('save')" />
      </div>
    </div>
  </BaseDialog>
</template>
