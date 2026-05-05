<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import Button from 'primevue/button'
import { useToast } from 'primevue/usetoast'
import { useRoute } from 'vue-router'

import BaseEmptyState from '@/components/common/BaseEmptyState.vue'
import BasePageHeader from '@/components/common/BasePageHeader.vue'
import BaseDataGrid from '@/components/grid/BaseDataGrid.vue'
import { useAppI18n } from '@/composables/useAppI18n'
import { useSessionStore } from '@/stores/session'
import { fetchErpResourceRows, saveErpResourceRows } from './api/api'
import {
  createEmptyFilters,
  getEndpointLabel,
  getErpResourceDefinition,
} from './model/pageModel'
import GuidePopupView from './popup-view/GuidePopupView.vue'
import ReferenceLookupPopupView from './popup-view/ReferenceLookupPopupView.vue'
import SearchView from './sub-view/SearchView.vue'
import type { CrudPayload, PermissionFlags } from '@/types/app'
import type { ErpResourceRow } from '@/types/erp'

const props = defineProps<{
  resourceKey: string
}>()

const toast = useToast()
const { t } = useAppI18n()
const route = useRoute()
const sessionStore = useSessionStore()
const loading = ref(false)
const saving = ref(false)
const guideVisible = ref(false)
const referenceVisible = ref(false)
const rows = ref<ErpResourceRow[]>([])
const filters = reactive<Record<string, string>>({})

const definition = computed(() => getErpResourceDefinition(props.resourceKey))
const visibleRows = computed(() => rows.value.slice(0, 100))
const endpointText = computed(() => getEndpointLabel(definition.value))
const canDeleteDefinition = computed(() => Boolean(
  definition.value?.save?.deleteEndpoint
    || (definition.value?.save?.updateEndpoint && definition.value?.save?.softDeleteField),
))
const gridPermissions = computed<PermissionFlags>(() => {
  const basePermissions = sessionStore.getPermissions(route.meta.menuId as string | undefined)
  const canSave = Boolean(definition.value?.save && !definition.value.save.readonly)

  return {
    permitRead: basePermissions.permitRead,
    permitWrite: basePermissions.permitWrite && canSave,
    permitDelete: basePermissions.permitDelete && canSave && canDeleteDefinition.value,
    permitExcel: basePermissions.permitExcel,
  }
})

const resetFilters = () => {
  Object.keys(filters).forEach((key) => {
    delete filters[key]
  })
  Object.assign(filters, createEmptyFilters(definition.value))
}

const loadRows = async () => {
  if (!definition.value) {
    rows.value = []
    return
  }

  loading.value = true

  try {
    rows.value = await fetchErpResourceRows(definition.value, filters)
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

const handleSave = async (payload: CrudPayload<Record<string, unknown>>) => {
  if (!definition.value) {
    return
  }

  saving.value = true

  try {
    rows.value = await saveErpResourceRows(definition.value, payload, filters)
    toast.add({
      severity: 'success',
      summary: t('erp.saveSuccess'),
      detail: t('erp.saveSuccessDetail'),
      life: 2500,
    })
  } catch (error) {
    toast.add({
      severity: 'error',
      summary: t('erp.saveFailed'),
      detail: error instanceof Error ? error.message : t('erp.saveFailedDetail'),
      life: 4000,
    })
  } finally {
    saving.value = false
  }
}

watch(
  () => props.resourceKey,
  () => {
    resetFilters()
    rows.value = []
  },
  { immediate: true },
)
</script>

<template>
  <div v-if="definition" class="page-stack">
    <BasePageHeader :title="definition.title" :description="definition.description">
      <template #actions>
        <Button icon="pi pi-info-circle" :label="t('erp.api')" severity="secondary" outlined @click="guideVisible = true" />
        <Button
          v-if="definition.references?.length"
          icon="pi pi-search"
          :label="t('erp.references')"
          severity="secondary"
          outlined
          @click="referenceVisible = true"
        />
        <Button icon="pi pi-refresh" :label="t('search.button')" :loading="loading || saving" @click="loadRows" />
      </template>
    </BasePageHeader>

    <SearchView
      :definition="definition"
      :filters="filters"
      :loading="loading"
      :endpoint-text="endpointText"
      @update:filter="(key, value) => { filters[key] = value }"
      @reset="resetFilters"
      @search="loadRows"
    />

    <BaseDataGrid
      :caption="definition.title"
      :columns="definition.columns"
      :rows="visibleRows"
      :permissions="gridPermissions"
      :row-defaults="definition.rowDefaults ?? {}"
      :group-by="definition.groupBy"
      @save="handleSave"
    />
    <GuidePopupView v-model:visible="guideVisible" :definition="definition" :endpoint-text="endpointText" />
    <ReferenceLookupPopupView
      v-model:visible="referenceVisible"
      :references="definition.references ?? []"
    />
  </div>

  <BaseEmptyState
    v-else
    title="erp.noDefinitionTitle"
    description="erp.noDefinitionDescription"
  />
</template>
