<script setup lang="ts">
import Button from 'primevue/button'
import Checkbox from 'primevue/checkbox'
import InputText from 'primevue/inputtext'

import BaseDialog from '@/components/common/BaseDialog.vue'
import { useAppI18n } from '@/composables/useAppI18n'
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

const { t } = useAppI18n()
</script>

<template>
  <BaseDialog :visible="visible" :title="title" @update:visible="emit('update:visible', $event)">
    <div class="form-popup-stack">
      <label class="inline-input">
        <span>{{ t('codes.field.codeGroupId') }}</span>
        <InputText v-model="form.codeGroupId" />
      </label>
      <label class="inline-input">
        <span>{{ t('codes.field.codeGroupName') }}</span>
        <InputText v-model="form.codeGroupName" />
      </label>
      <label class="inline-input">
        <span>{{ t('codes.field.description') }}</span>
        <InputText v-model="form.description" />
      </label>
      <label class="checkbox-line">
        <Checkbox v-model="form.enabled" binary />
        <span>{{ t('codes.field.enabled') }}</span>
      </label>
      <div class="dialog-actions">
        <Button :label="t('common.close')" severity="secondary" @click="emit('update:visible', false)" />
        <Button :label="t('common.save')" :loading="loading" @click="emit('save')" />
      </div>
    </div>
  </BaseDialog>
</template>
