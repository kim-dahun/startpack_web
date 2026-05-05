<script setup lang="ts">
import Button from 'primevue/button'
import BaseEmptyState from '@/components/common/BaseEmptyState.vue'
import BaseForbiddenState from '@/components/common/BaseForbiddenState.vue'
import BasePageHeader from '@/components/common/BasePageHeader.vue'
import { useAppI18n } from '@/composables/useAppI18n'
import { useManagementScreen } from '@/pages/shared/management/useManagementScreen'
import { listRows, saveRows } from './api/api'
import { pageDefinition, pageResourceKey } from './model/pageModel'
import GridView from './sub-view/GridView.vue'
import SearchView from './sub-view/SearchView.vue'

const { t } = useAppI18n()
const screen = useManagementScreen({ resourceKey: pageResourceKey, definition: pageDefinition, listRows, saveRows })
</script>

<template>
  <div class="page-stack">
    <BasePageHeader :title="pageDefinition.title">
      <template #actions>
        <Button icon="pi pi-refresh" :label="t('common.refresh')" size="small" severity="secondary" @click="screen.loadRows" />
      </template>
    </BasePageHeader>
    <BaseForbiddenState v-if="!screen.permissions.permitRead" />
    <template v-else>
      <SearchView :keyword="screen.keyword" :placeholder="pageDefinition.searchPlaceholder" :requires-user-id="pageDefinition.requiresUserId" :selected-user-id="screen.selectedUserId" :service-scoped="pageDefinition.serviceScoped" :selected-service-id="screen.selectedServiceId" :service-options="screen.serviceOptions" @update:keyword="screen.keyword = $event" @update:selected-user-id="screen.selectedUserId = $event" @update:selected-service-id="screen.selectedServiceId = $event" @search="screen.loadRows" />
      <BaseEmptyState v-if="!screen.loading && !screen.filteredRows.length" title="common.emptyTitle" description="common.emptyDescription" />
      <GridView v-else :caption="pageDefinition.title" :columns="screen.columns" :rows="screen.filteredRows" :permissions="screen.permissions" :row-defaults="screen.rowDefaults" @save="screen.handleSave" />
    </template>
  </div>
</template>
