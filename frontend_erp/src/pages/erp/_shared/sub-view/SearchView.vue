<script setup lang="ts">
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'

import { useAppI18n } from '@/composables/useAppI18n'
import type { ErpResourceDefinition } from '@/types/erp'

defineProps<{
  definition: ErpResourceDefinition
  filters: Record<string, string>
  loading: boolean
  endpointText: string
}>()

const emit = defineEmits<{
  search: []
  reset: []
  'update:filter': [key: string, value: string]
}>()

const { t } = useAppI18n()
</script>

<template>
  <section class="base-search-form">
    <div class="base-search-form__header-panel">
      <div class="base-search-form__header">
        <strong>{{ t('search.title') }}</strong>
        <span class="muted">{{ endpointText }}</span>
      </div>
    </div>
    <div class="base-search-form__field-panel">
      <div class="base-search-form__fields">
        <label v-for="filter in definition.filters" :key="filter.key" class="inline-input">
          <span>{{ t(filter.label, filter.label) }}</span>
          <InputText
            :model-value="filters[filter.key]"
            :placeholder="t(filter.placeholder, filter.placeholder)"
            @update:model-value="emit('update:filter', filter.key, String($event ?? ''))"
            @keyup.enter="emit('search')"
          />
        </label>
      </div>
    </div>
    <div class="base-search-form__action-panel">
      <div class="base-search-form__actions">
        <Button :label="t('search.reset')" severity="secondary" outlined @click="emit('reset')" />
        <Button :label="t('search.button')" :loading="loading" @click="emit('search')" />
      </div>
    </div>
  </section>
</template>
