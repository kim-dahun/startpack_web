<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import { useToast } from 'primevue/usetoast'

import BaseDialog from '@/components/common/BaseDialog.vue'
import BaseEmptyState from '@/components/common/BaseEmptyState.vue'
import { useAppI18n } from '@/composables/useAppI18n'
import { fetchErpReferenceRows } from '../api/api'
import type { ErpReferenceDefinition, ErpResourceRow } from '@/types/erp'

const props = defineProps<{
  visible: boolean
  references: ErpReferenceDefinition[]
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
}>()

const toast = useToast()
const { t } = useAppI18n()
const loading = ref(false)
const selectedKey = ref('')
const rows = ref<ErpResourceRow[]>([])
const filters = reactive<Record<string, string>>({})

const selectedReference = computed(() =>
  props.references.find((reference) => reference.key === selectedKey.value) ?? props.references[0],
)

const resetFilters = () => {
  Object.keys(filters).forEach((key) => {
    delete filters[key]
  })
  Object.assign(filters, Object.fromEntries((selectedReference.value?.filters ?? []).map((filter) => [filter.key, ''])))
}

const selectReference = (key: string) => {
  selectedKey.value = key
  rows.value = []
  resetFilters()
}

const loadRows = async () => {
  if (!selectedReference.value) {
    rows.value = []
    return
  }

  loading.value = true

  try {
    rows.value = await fetchErpReferenceRows(selectedReference.value, filters)
  } catch (error) {
    rows.value = []
    toast.add({
      severity: 'error',
      summary: t('erp.queryFailed'),
      detail: error instanceof Error ? error.message : t('erp.requestFailed'),
      life: 3500,
    })
  } finally {
    loading.value = false
  }
}

watch(
  () => props.references,
  () => {
    selectedKey.value = props.references[0]?.key ?? ''
    rows.value = []
    resetFilters()
  },
  { immediate: true },
)
</script>

<template>
  <BaseDialog
    :visible="visible"
    :title="t('erp.referenceLookup')"
    @update:visible="emit('update:visible', $event)"
  >
    <div v-if="selectedReference" class="erp-reference-popup">
      <div class="erp-reference-tabs" role="tablist">
        <Button
          v-for="reference in references"
          :key="reference.key"
          :label="t(reference.title, reference.title)"
          :severity="reference.key === selectedReference.key ? 'primary' : 'secondary'"
          :outlined="reference.key !== selectedReference.key"
          size="small"
          @click="selectReference(reference.key)"
        />
      </div>

      <div class="erp-reference-search">
        <label v-for="filter in selectedReference.filters" :key="filter.key">
          <span>{{ t(filter.label, filter.label) }}</span>
          <InputText
            :model-value="filters[filter.key]"
            :placeholder="t(filter.placeholder, filter.placeholder)"
            @update:model-value="filters[filter.key] = String($event ?? '')"
            @keyup.enter="loadRows"
          />
        </label>
        <Button icon="pi pi-refresh" :label="t('search.button')" :loading="loading" @click="loadRows" />
      </div>

      <div v-if="rows.length" class="erp-table-shell">
        <table class="erp-record-table">
          <thead>
            <tr>
              <th v-for="column in selectedReference.columns" :key="column">{{ column }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(row, rowIndex) in rows" :key="String(row.id ?? row.rowId ?? rowIndex)">
              <td v-for="column in selectedReference.columns" :key="column">
                {{ row[column] ?? '-' }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <BaseEmptyState
        v-else
        title="erp.referenceEmptyTitle"
        description="erp.referenceEmptyDescription"
      />
    </div>

    <BaseEmptyState
      v-else
      title="erp.referenceNotDefinedTitle"
      description="erp.referenceNotDefinedDescription"
    />
  </BaseDialog>
</template>
