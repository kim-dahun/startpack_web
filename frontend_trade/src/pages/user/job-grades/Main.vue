<script setup lang="ts">
import { computed, ref } from 'vue'
import Button from 'primevue/button'
import { useToast } from 'primevue/usetoast'

import BaseEmptyState from '@/components/common/BaseEmptyState.vue'
import BaseForbiddenState from '@/components/common/BaseForbiddenState.vue'
import BasePageHeader from '@/components/common/BasePageHeader.vue'
import { useSessionStore } from '@/stores/session'
import type { CrudPayload } from '@/types/app'
import { listRows, saveRows } from './api/api'
import { pageDefinition } from './model/pageModel'
import GridView from './sub-view/GridView.vue'
import SearchView from './sub-view/SearchView.vue'

const sessionStore = useSessionStore()
const toast = useToast()
const permissions = computed(() => sessionStore.getPermissions('JOB_GRADES'))
const rows = ref<Array<Record<string, unknown>>>([])
const keyword = ref('')
const selectedType = ref('')
const loading = ref(false)

const filteredRows = computed(() => {
  if (!selectedType.value) {
    return []
  }

  const normalized = keyword.value.trim().toLowerCase()

  return rows.value
    .filter((row) => String(row.jobGradeType ?? '') === selectedType.value)
    .filter((row) => !normalized || [row.jobGradeId, row.jobGradeName].some((value) => String(value ?? '').toLowerCase().includes(normalized)))
})

const loadRows = async () => {
  if (!selectedType.value) {
    toast.add({
      severity: 'warn',
      summary: 'Select Type',
      detail: 'Select jobGradeType first.',
      life: 2200,
    })
    rows.value = []
    return
  }

  loading.value = true
  try {
    rows.value = await listRows({
      comCd: sessionStore.persisted.user?.comCd ?? 'COM001',
    })
  } finally {
    loading.value = false
  }
}

const handleSave = async (payload: CrudPayload<Record<string, unknown>>) => {
  rows.value = await saveRows(payload, {
    comCd: sessionStore.persisted.user?.comCd ?? 'COM001',
  })
}
</script>

<template>
  <div class="page-stack">
    <BasePageHeader :title="pageDefinition.title">
      <template #actions>
        <Button icon="pi pi-refresh" label="Refresh" size="small" severity="secondary" @click="loadRows" />
      </template>
    </BasePageHeader>
    <BaseForbiddenState v-if="!permissions.permitRead" />
    <template v-else>
      <SearchView :keyword="keyword" :selected-type="selectedType" @update:keyword="keyword = $event" @update:selected-type="selectedType = $event" @search="loadRows" />
      <BaseEmptyState v-if="!loading && !filteredRows.length" title="No Data" description="Select jobGradeType and search." />
      <GridView v-else :caption="pageDefinition.title" :columns="pageDefinition.columns" :rows="filteredRows" :permissions="permissions" :row-defaults="{ comCd: sessionStore.persisted.user?.comCd ?? 'COM001', jobGradeType: selectedType }" @save="handleSave" />
    </template>
  </div>
</template>
