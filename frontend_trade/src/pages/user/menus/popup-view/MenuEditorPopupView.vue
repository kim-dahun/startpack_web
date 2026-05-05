<script setup lang="ts">
import Button from 'primevue/button'
import Checkbox from 'primevue/checkbox'
import InputNumber from 'primevue/inputnumber'
import InputText from 'primevue/inputtext'

import BaseDialog from '@/components/common/BaseDialog.vue'
import { useAppI18n } from '@/composables/useAppI18n'
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

const { t } = useAppI18n()
</script>

<template>
  <BaseDialog :visible="visible" :title="title" @update:visible="emit('update:visible', $event)">
    <div class="form-popup-stack">
      <label class="inline-input">
        <span>{{ t('menus.field.parentMenu') }}</span>
        <InputText :model-value="parentLabel" disabled />
      </label>
      <label class="inline-input">
        <span>{{ t('menus.field.menuLevel') }}</span>
        <InputText :model-value="String(level)" disabled />
      </label>
      <label class="inline-input">
        <span>{{ t('menus.field.menuId') }}</span>
        <InputText v-model="form.menuId" />
      </label>
      <label class="inline-input">
        <span>{{ t('menus.field.menuName') }}</span>
        <InputText v-model="form.menuName" />
      </label>
      <label class="inline-input">
        <span>{{ t('menus.field.menuUrl') }}</span>
        <InputText v-model="form.menuUrl" />
      </label>
      <label class="inline-input">
        <span>{{ t('menus.field.i18nCode') }}</span>
        <InputText v-model="form.i18nCode" />
      </label>
      <label class="inline-input">
        <span>{{ t('menus.field.icon') }}</span>
        <InputText v-model="form.icon" />
      </label>
      <label class="inline-input">
        <span>{{ t('menus.field.sortSeq') }}</span>
        <InputNumber v-model="form.sortSeq" :min="1" fluid />
      </label>
      <label class="checkbox-line">
        <Checkbox v-model="form.enabled" binary />
        <span>{{ t('common.enabled') }}</span>
      </label>
      <div class="dialog-actions">
        <Button :label="t('common.close')" severity="secondary" @click="emit('update:visible', false)" />
        <Button :label="t('common.save')" :loading="loading" @click="emit('save')" />
      </div>
    </div>
  </BaseDialog>
</template>
