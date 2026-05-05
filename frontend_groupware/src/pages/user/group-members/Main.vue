<script setup lang="ts">
import Button from 'primevue/button'
import BaseEmptyState from '@/components/common/BaseEmptyState.vue'
import BaseForbiddenState from '@/components/common/BaseForbiddenState.vue'
import BasePageHeader from '@/components/common/BasePageHeader.vue'
import { useManagementScreen } from '@/pages/shared/management/useManagementScreen'
import { listRows, saveRows } from './api/api'
import { pageDefinition, pageResourceKey } from './model/pageModel'
import GridView from './sub-view/GridView.vue'
import SearchView from './sub-view/SearchView.vue'
const screen = useManagementScreen({ resourceKey: pageResourceKey, definition: pageDefinition, listRows, saveRows })
</script>
<template>
  <div class="page-stack">
    <BasePageHeader :title="pageDefinition.title"><template #actions><Button icon="pi pi-refresh" label="새로고침" size="small" severity="secondary" @click="screen.loadRows" /></template></BasePageHeader>
    <BaseForbiddenState v-if="!screen.permissions.permitRead" />
    <template v-else>
      <SearchView :keyword="screen.keyword" :placeholder="pageDefinition.searchPlaceholder" :requires-user-id="pageDefinition.requiresUserId" :selected-user-id="screen.selectedUserId" :service-scoped="pageDefinition.serviceScoped" :selected-service-id="screen.selectedServiceId" :service-options="screen.serviceOptions" :selected-group-id="screen.selectedGroupId" :group-options="screen.groupOptions" @update:keyword="screen.keyword = $event" @update:selected-user-id="screen.selectedUserId = $event" @update:selected-service-id="screen.selectedServiceId = $event" @update:selected-group-id="screen.selectedGroupId = $event" @search="screen.loadRows" />
      <BaseEmptyState v-if="!screen.loading && !screen.filteredRows.length" title="조회 결과가 없습니다." description="검색 조건을 변경하거나 새로고침 후 다시 확인하십시오." />
      <GridView v-else :caption="pageDefinition.title" :columns="screen.columns" :rows="screen.filteredRows" :permissions="screen.permissions" :row-defaults="screen.rowDefaults" @save="screen.handleSave" />
    </template>
  </div>
</template>
