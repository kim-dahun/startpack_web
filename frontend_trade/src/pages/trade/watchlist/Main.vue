<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import Button from 'primevue/button'
import Dropdown from 'primevue/dropdown'
import InputText from 'primevue/inputtext'
import { useToast } from 'primevue/usetoast'

import BasePageHeader from '@/components/common/BasePageHeader.vue'
import TradePanel from '@/components/trade/TradePanel.vue'
import TradeRecordTable from '@/components/trade/TradeRecordTable.vue'
import { useAppI18n } from '@/composables/useAppI18n'
import { useSessionStore } from '@/stores/session'
import { useTradeWorkspaceStore } from '@/stores/trade/workspace'
import type { TradeFrequentSearchItem, TradeWatchlistItem } from '@/types/trade'

import {
  createCurrentUserWatchlistGroup,
  createCurrentUserWatchlistItem,
  getCurrentUserFrequentSearches,
  getCurrentUserWatchlist,
  getCurrentUserWatchlistGroups,
  removeWatchlistGroup,
  removeWatchlistItem,
  updateWatchlistMetadata,
} from './api/api'

const router = useRouter()
const toast = useToast()
const sessionStore = useSessionStore()
const workspaceStore = useTradeWorkspaceStore()
const { t } = useAppI18n()
const permissions = computed(() => sessionStore.getPermissions('WATCHLIST'))

const groups = ref<Array<{ id: number; userId: string; groupName: string; createdAt?: string }>>([])
const items = ref<TradeWatchlistItem[]>([])
const frequentItems = ref<TradeFrequentSearchItem[]>([])
const groupName = ref('')
const loading = ref(false)

const createForm = reactive({
  itemCode: '',
  itemName: '',
  groupId: null as number | null,
  memo: '',
  tagsText: '',
})

const groupOptions = computed(() =>
  groups.value.map((group) => ({
    label: group.groupName,
    value: group.id,
  })),
)

const watchlistColumns = computed(() => [
  { field: 'itemCode', title: t('trade.label.itemCode') },
  { field: 'itemName', title: t('trade.label.itemName') },
  { field: 'groupName', title: t('trade.label.group') },
  { field: 'memo', title: t('trade.label.memo') },
  { field: 'tags', title: t('trade.label.tags') },
])

const frequentColumns = computed(() => [
  { field: 'itemCode', title: t('trade.label.itemCode') },
  { field: 'itemName', title: t('trade.label.itemName') },
  { field: 'marketCode', title: t('trade.label.market') },
  { field: 'searchCount', title: t('trade.label.searchCount') },
  { field: 'lastSearchedAt', title: t('trade.label.lastSearchedAt') },
])

const parseTags = (value: string) =>
  value.split(',').map((tag) => tag.trim()).filter(Boolean)

const refreshGroups = async () => {
  groups.value = await getCurrentUserWatchlistGroups()
  if (
    workspaceStore.selectedWatchlistGroupId
    && !groups.value.some((group) => group.id === workspaceStore.selectedWatchlistGroupId)
  ) {
    workspaceStore.setSelectedWatchlistGroupId(null)
  }
}

const refreshItems = async () => {
  items.value = await getCurrentUserWatchlist(workspaceStore.selectedWatchlistGroupId)
}

const refreshFrequentItems = async () => {
  frequentItems.value = await getCurrentUserFrequentSearches()
}

const refreshAll = async () => {
  loading.value = true
  try {
    await Promise.allSettled([refreshGroups(), refreshItems(), refreshFrequentItems()])
  } finally {
    loading.value = false
  }
}

const handleCreateGroup = async () => {
  if (!groupName.value.trim()) {
    return
  }
  await createCurrentUserWatchlistGroup(groupName.value.trim())
  groupName.value = ''
  await refreshGroups()
}

const handleDeleteGroup = async () => {
  if (!workspaceStore.selectedWatchlistGroupId) {
    return
  }
  await removeWatchlistGroup(workspaceStore.selectedWatchlistGroupId)
  workspaceStore.setSelectedWatchlistGroupId(null)
  await refreshAll()
}

const handleCreateItem = async () => {
  await createCurrentUserWatchlistItem({
    itemCode: createForm.itemCode,
    itemName: createForm.itemName,
    groupId: createForm.groupId,
    memo: createForm.memo,
    tags: parseTags(createForm.tagsText),
  })

  createForm.itemCode = ''
  createForm.itemName = ''
  createForm.groupId = null
  createForm.memo = ''
  createForm.tagsText = ''

  await refreshItems()
  toast.add({
    severity: 'success',
    summary: t('trade.watchlist.savedTitle'),
    detail: t('trade.watchlist.savedDetail'),
    life: 2200,
  })
}

const handleApplyFrequent = async (row: TradeFrequentSearchItem) => {
  createForm.itemCode = row.itemCode
  createForm.itemName = row.itemName
  await moveToQuote(row.itemCode)
}

const handleDeleteItem = async (row: TradeWatchlistItem) => {
  await removeWatchlistItem(row.id)
  await refreshItems()
}

const handleSaveMemo = async (row: TradeWatchlistItem) => {
  await updateWatchlistMetadata(row.id, {
    groupId: row.groupId ?? null,
    memo: row.memo ?? '',
    tags: row.tags ?? [],
  })
  await refreshItems()
}

const moveToQuote = async (itemCode: string) => {
  workspaceStore.setSelectedItemCode(itemCode)
  await router.push({
    name: 'realtime',
    query: { itemCode },
  })
}

const groupNameById = (groupId?: number | null) =>
  groups.value.find((group) => group.id === groupId)?.groupName ?? '-'

const watchlistRows = computed(() =>
  items.value.map((row) => ({
    ...row,
    groupName: groupNameById(row.groupId),
    tags: (row.tags ?? []).join(', '),
  })),
)

onMounted(async () => {
  await refreshAll()
})
</script>

<template>
  <div class="page-stack">
    <BasePageHeader title="menu.watchlist" :description="t('trade.watchlist.pageDescription')" />

    <div class="trade-two-column">
      <TradePanel :title="t('trade.watchlist.groupTitle')" :description="t('trade.watchlist.groupDescription')">
        <template #actions>
          <InputText :model-value="groupName" :placeholder="t('trade.watchlist.newGroupPlaceholder')" @update:model-value="groupName = String($event ?? '')" />
          <Button icon="pi pi-plus" :label="t('trade.action.createGroup')" size="small" :disabled="!permissions.permitWrite" @click="handleCreateGroup" />
          <Button
            icon="pi pi-trash"
            :label="t('trade.action.deleteGroup')"
            size="small"
            severity="danger"
            :disabled="!permissions.permitDelete || !workspaceStore.selectedWatchlistGroupId"
            @click="handleDeleteGroup"
          />
        </template>

        <div class="selection-list">
          <button
            type="button"
            class="selection-list__item"
            :class="{ 'is-active': workspaceStore.selectedWatchlistGroupId === null }"
            @click="workspaceStore.setSelectedWatchlistGroupId(null); refreshItems()"
          >
            <strong>{{ t('trade.watchlist.allGroup') }}</strong>
            <span>{{ t('trade.watchlist.allGroupDescription') }}</span>
          </button>
          <button
            v-for="group in groups"
            :key="group.id"
            type="button"
            class="selection-list__item"
            :class="{ 'is-active': workspaceStore.selectedWatchlistGroupId === group.id }"
            @click="workspaceStore.setSelectedWatchlistGroupId(group.id); refreshItems()"
          >
            <strong>{{ group.groupName }}</strong>
            <span>{{ group.createdAt ?? '-' }}</span>
          </button>
        </div>
      </TradePanel>

      <TradePanel :title="t('trade.watchlist.frequentTitle')" :description="t('trade.watchlist.frequentDescription')">
        <template #actions>
          <Button icon="pi pi-refresh" :label="t('common.refresh')" size="small" severity="secondary" :loading="loading" @click="refreshFrequentItems" />
        </template>

        <TradeRecordTable :columns="frequentColumns" :rows="frequentItems as unknown as Array<Record<string, unknown>>" row-key="id">
          <template #actions="{ row }">
            <Button text size="small" :label="t('trade.action.openQuote')" @click="moveToQuote((row as unknown as TradeFrequentSearchItem).itemCode)" />
            <Button text size="small" :label="t('trade.action.applyFrequent')" @click="handleApplyFrequent(row as unknown as TradeFrequentSearchItem)" />
          </template>
        </TradeRecordTable>
      </TradePanel>
    </div>

    <TradePanel :title="t('trade.watchlist.createTitle')" :description="t('trade.watchlist.createDescription')">
      <template #actions>
        <Dropdown
          :model-value="createForm.groupId"
          :options="groupOptions"
          option-label="label"
          option-value="value"
          :placeholder="t('trade.watchlist.groupSelect')"
          class="trade-panel__dropdown"
          @update:model-value="createForm.groupId = Number($event ?? 0) || null"
        />
      </template>

      <div class="trade-form-grid">
        <label class="inline-input">
          <span>{{ t('trade.label.itemCode') }}</span>
          <InputText v-model="createForm.itemCode" />
        </label>
        <label class="inline-input">
          <span>{{ t('trade.label.itemName') }}</span>
          <InputText v-model="createForm.itemName" />
        </label>
        <label class="inline-input">
          <span>{{ t('trade.label.memo') }}</span>
          <InputText v-model="createForm.memo" />
        </label>
        <label class="inline-input">
          <span>{{ t('trade.label.tags') }}</span>
          <InputText v-model="createForm.tagsText" :placeholder="t('trade.watchlist.tagPlaceholder')" />
        </label>
      </div>

      <div class="trade-inline-actions">
        <Button icon="pi pi-save" :label="t('trade.action.saveWatchlist')" size="small" :disabled="!permissions.permitWrite" @click="handleCreateItem" />
      </div>
    </TradePanel>

    <TradePanel :title="t('trade.watchlist.listTitle')" :description="t('trade.watchlist.listDescription')">
      <TradeRecordTable :columns="watchlistColumns" :rows="watchlistRows as unknown as Array<Record<string, unknown>>" row-key="id">
        <template #actions="{ row }">
          <Button text size="small" :label="t('trade.action.openQuote')" @click="moveToQuote((row as unknown as TradeWatchlistItem).itemCode)" />
          <Button text size="small" :label="t('common.save')" :disabled="!permissions.permitWrite" @click="handleSaveMemo(row as unknown as TradeWatchlistItem)" />
          <Button text size="small" severity="danger" :label="t('common.delete')" :disabled="!permissions.permitDelete" @click="handleDeleteItem(row as unknown as TradeWatchlistItem)" />
        </template>
      </TradeRecordTable>
    </TradePanel>
  </div>
</template>
