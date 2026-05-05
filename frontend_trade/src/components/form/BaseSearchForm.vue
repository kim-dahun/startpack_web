<script setup lang="ts">
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'

import { useAppI18n } from '@/composables/useAppI18n'

defineProps<{
  modelValue: string
  placeholder: string
  title?: string
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string]
  search: []
}>()

const { t } = useAppI18n()
</script>

<template>
  <section class="base-search-form">
    <div class="base-search-form__header-panel">
      <div class="base-search-form__header">
        <strong>{{ t(title ?? 'search.title', title ?? 'Search Conditions') }}</strong>
      </div>
    </div>
    <div class="base-search-form__field-panel">
      <div class="base-search-form__fields">
        <slot>
          <InputText
            :model-value="modelValue"
            :placeholder="t(placeholder, placeholder)"
            @update:model-value="emit('update:modelValue', String($event ?? ''))"
            @keyup.enter="emit('search')"
          />
        </slot>
      </div>
    </div>
    <div class="base-search-form__action-panel">
      <div class="base-search-form__actions">
        <Button icon="pi pi-search" :label="t('search.button')" @click="emit('search')" />
      </div>
    </div>
  </section>
</template>
