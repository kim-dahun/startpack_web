<script setup lang="ts">
import Button from 'primevue/button'
import Checkbox from 'primevue/checkbox'
import InputNumber from 'primevue/inputnumber'
import InputText from 'primevue/inputtext'

import BaseDialog from '@/components/common/BaseDialog.vue'
import type { MenuEditorForm } from '../model/pageModel'

defineProps<{
  visible: boolean
  title: string
  form: MenuEditorForm
  parentLabel: string
  level: number
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
        <span>Parent Menu</span>
        <InputText :model-value="parentLabel" disabled />
      </label>
      <label class="inline-input">
        <span>Menu Level</span>
        <InputText :model-value="String(level)" disabled />
      </label>
      <label class="inline-input">
        <span>Menu ID</span>
        <InputText v-model="form.menuId" />
      </label>
      <label class="inline-input">
        <span>Menu Name</span>
        <InputText v-model="form.menuName" />
      </label>
      <label class="inline-input">
        <span>Menu URL</span>
        <InputText v-model="form.menuUrl" />
      </label>
      <label class="inline-input">
        <span>i18n Code</span>
        <InputText v-model="form.i18nCode" />
      </label>
      <label class="inline-input">
        <span>Icon</span>
        <InputText v-model="form.icon" />
      </label>
      <label class="inline-input">
        <span>Sort Seq</span>
        <InputNumber v-model="form.sortSeq" :min="1" fluid />
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
