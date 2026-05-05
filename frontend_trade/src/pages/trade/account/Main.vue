<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import Button from 'primevue/button'
import Checkbox from 'primevue/checkbox'
import Dropdown from 'primevue/dropdown'
import InputText from 'primevue/inputtext'
import { useConfirm } from 'primevue/useconfirm'
import { useToast } from 'primevue/usetoast'

import BaseLineChart from '@/components/chart/BaseLineChart.vue'
import BaseDialog from '@/components/common/BaseDialog.vue'
import BaseEmptyState from '@/components/common/BaseEmptyState.vue'
import BasePageHeader from '@/components/common/BasePageHeader.vue'
import { useAppI18n } from '@/composables/useAppI18n'
import TradePanel from '@/components/trade/TradePanel.vue'
import TradeRecordTable from '@/components/trade/TradeRecordTable.vue'
import { useTradeWorkspaceStore } from '@/stores/trade/workspace'
import type {
  TradeAccountBalance,
  TradeAccountPosition,
  TradeAccountSummary,
  TradeDailyBalance,
  TradeHistoryRecord,
  TradeMode,
  TradePerformanceRecord,
  TradeRegisteredAccount,
} from '@/types/trade'

import {
  fetchAccountBalances,
  fetchAccountPositions,
  fetchAccounts,
  fetchDailyBalances,
  fetchPerformanceHistories,
  fetchRegisteredAccounts,
  fetchTradeHistories,
  removeRegisteredAccount,
  saveRegisteredAccount,
} from './api/api'

type AccountDialogMode = 'create' | 'edit'

const today = new Date().toISOString().slice(0, 10)
const monthAgo = new Date(Date.now() - 1000 * 60 * 60 * 24 * 30).toISOString().slice(0, 10)

const workspaceStore = useTradeWorkspaceStore()
const toast = useToast()
const confirm = useConfirm()
const { t } = useAppI18n()

const loading = ref(false)
const dialogLoading = ref(false)
const registeredAccounts = ref<TradeRegisteredAccount[]>([])
const accountSummaries = ref<TradeAccountSummary[]>([])
const balance = ref<TradeAccountBalance | null>(null)
const positions = ref<TradeAccountPosition[]>([])
const dailyBalances = ref<TradeDailyBalance[]>([])
const performanceRows = ref<TradePerformanceRecord[]>([])
const histories = ref<TradeHistoryRecord[]>([])
const performanceFrom = ref(monthAgo)
const performanceTo = ref(today)
const balanceBaseDate = ref(today)

const dialogVisible = ref(false)
const dialogMode = ref<AccountDialogMode>('create')

const accountForm = reactive({
  id: null as number | null,
  accountNo: '',
  accountName: '',
  productCode: '01',
  aliasName: '',
  memo: '',
  active: true,
})

const normalizeAccountPart = (value: string | null | undefined) => String(value ?? '').trim()

const buildLookupAccountNo = (account: Pick<TradeRegisteredAccount, 'accountNo' | 'productCode'> | null) => {
  if (!account) {
    return ''
  }

  const accountNo = normalizeAccountPart(account.accountNo)
  const productCode = normalizeAccountPart(account.productCode)

  if (!accountNo) {
    return ''
  }

  if (accountNo.includes('-') || !productCode) {
    return accountNo
  }

  return `${accountNo}-${productCode}`
}

const selectedRegisteredAccount = computed(() =>
  registeredAccounts.value.find((account) => account.accountNo === workspaceStore.selectedAccountNo) ?? null,
)

const selectedLookupAccountNo = computed(() => {
  const lookupAccountNo = buildLookupAccountNo(selectedRegisteredAccount.value)
  return lookupAccountNo || normalizeAccountPart(workspaceStore.selectedAccountNo)
})

const accountOptions = computed(() =>
  registeredAccounts.value
    .filter((account) => account.active)
    .map((account) => ({
      label: `${buildLookupAccountNo(account)} / ${account.accountName}`,
      value: account.accountNo,
    })),
)

const tradeModeOptions = computed(() => ([
  { label: 'LIVE', value: 'LIVE' },
  { label: 'PAPER', value: 'PAPER' },
]))

const registeredAccountColumns = computed(() => ([
  { field: 'accountNo', title: t('trade.label.accountNo') },
  { field: 'accountName', title: t('trade.account.field.accountName') },
  { field: 'productCode', title: t('trade.account.field.productCode') },
  { field: 'aliasName', title: t('trade.account.field.aliasName') },
  { field: 'active', title: t('trade.account.field.active') },
]))

const summaryColumns = computed(() => ([
  { field: 'accountNo', title: t('trade.label.accountNo') },
  { field: 'accountName', title: t('trade.account.field.accountName') },
  { field: 'totalAssetAmount', title: t('trade.label.totalAssetAmount') },
  { field: 'cashAmount', title: t('trade.label.cashAmount') },
]))

const positionColumns = computed(() => ([
  { field: 'itemCode', title: t('trade.label.itemCode') },
  { field: 'itemName', title: t('trade.label.itemName') },
  { field: 'quantity', title: t('trade.label.quantity') },
  { field: 'averagePrice', title: t('trade.label.averagePrice') },
  { field: 'currentPrice', title: t('trade.label.currentPrice') },
  { field: 'profitLossAmount', title: t('trade.label.profitLossAmount') },
  { field: 'profitLossRate', title: t('trade.label.profitLossRate') },
]))

const historyColumns = computed(() => ([
  { field: 'tradedAt', title: t('trade.label.tradedAt') },
  { field: 'side', title: t('trade.label.division') },
  { field: 'itemCode', title: t('trade.label.itemCode') },
  { field: 'itemName', title: t('trade.label.itemName') },
  { field: 'quantity', title: t('trade.label.quantity') },
  { field: 'price', title: t('trade.label.price') },
  { field: 'amount', title: t('trade.label.amount') },
]))

const homeStats = computed(() => {
  const totalAsset = accountSummaries.value.reduce((sum, row) => sum + Number(row.totalAssetAmount ?? 0), 0)
  const totalCash = accountSummaries.value.reduce((sum, row) => sum + Number(row.cashAmount ?? 0), 0)

  return [
    { label: t('trade.account.stat.registeredCount'), value: String(registeredAccounts.value.length) },
    { label: t('trade.account.stat.activeCount'), value: String(registeredAccounts.value.filter((row) => row.active).length) },
    { label: t('trade.account.stat.totalAsset'), value: totalAsset.toLocaleString() },
    { label: t('trade.account.stat.cash'), value: totalCash.toLocaleString() },
  ]
})

const resetForm = () => {
  accountForm.id = null
  accountForm.accountNo = ''
  accountForm.accountName = ''
  accountForm.productCode = '01'
  accountForm.aliasName = ''
  accountForm.memo = ''
  accountForm.active = true
}

const openCreateAccount = () => {
  dialogMode.value = 'create'
  resetForm()
  dialogVisible.value = true
}

const openEditAccount = (row: TradeRegisteredAccount) => {
  dialogMode.value = 'edit'
  accountForm.id = row.id
  accountForm.accountNo = row.accountNo
  accountForm.accountName = row.accountName
  accountForm.productCode = row.productCode
  accountForm.aliasName = row.aliasName ?? ''
  accountForm.memo = row.memo ?? ''
  accountForm.active = row.active
  dialogVisible.value = true
}

const refreshRegisteredAccounts = async () => {
  registeredAccounts.value = await fetchRegisteredAccounts()

  if (
    workspaceStore.selectedAccountNo
    && !registeredAccounts.value.some((row) => row.accountNo === workspaceStore.selectedAccountNo && row.active)
  ) {
    workspaceStore.setSelectedAccountNo('')
  }

  if (!workspaceStore.selectedAccountNo) {
    workspaceStore.setSelectedAccountNo(registeredAccounts.value.find((row) => row.active)?.accountNo ?? '')
  }
}

const refreshAccountSummaries = async () => {
  if (!selectedLookupAccountNo.value) {
    accountSummaries.value = []
    return
  }

  accountSummaries.value = await fetchAccounts(workspaceStore.tradeMode, selectedLookupAccountNo.value)
}

const refreshSelectedAccount = async () => {
  if (!selectedLookupAccountNo.value) {
    balance.value = null
    positions.value = []
    dailyBalances.value = []
    histories.value = []
    performanceRows.value = []
    return
  }

  const [nextBalance, nextPositions, nextDailyBalances, nextHistories, nextPerformance] = await Promise.allSettled([
    fetchAccountBalances(selectedLookupAccountNo.value, workspaceStore.tradeMode),
    fetchAccountPositions(selectedLookupAccountNo.value, workspaceStore.tradeMode),
    fetchDailyBalances(selectedLookupAccountNo.value, balanceBaseDate.value, workspaceStore.tradeMode),
    fetchTradeHistories(selectedLookupAccountNo.value, workspaceStore.tradeMode),
    fetchPerformanceHistories(selectedLookupAccountNo.value, performanceFrom.value, performanceTo.value),
  ])

  balance.value = nextBalance.status === 'fulfilled' ? nextBalance.value : null
  positions.value = nextPositions.status === 'fulfilled' ? nextPositions.value : []
  dailyBalances.value = nextDailyBalances.status === 'fulfilled' ? nextDailyBalances.value : []
  histories.value = nextHistories.status === 'fulfilled' ? nextHistories.value : []
  performanceRows.value = nextPerformance.status === 'fulfilled' ? nextPerformance.value : []
}

const refreshAll = async () => {
  loading.value = true

  try {
    await refreshRegisteredAccounts()
    await refreshAccountSummaries()
    await refreshSelectedAccount()
  } finally {
    loading.value = false
  }
}

const handleSaveAccount = async () => {
  if (!accountForm.accountNo.trim() || !accountForm.accountName.trim() || !accountForm.productCode.trim()) {
    toast.add({
      severity: 'warn',
      summary: t('trade.account.requiredTitle'),
      detail: t('trade.account.requiredDetail'),
      life: 2200,
    })
    return
  }

  dialogLoading.value = true

  try {
    await saveRegisteredAccount({
      id: accountForm.id,
      accountNo: accountForm.accountNo.trim(),
      accountName: accountForm.accountName.trim(),
      productCode: accountForm.productCode.trim(),
      aliasName: accountForm.aliasName.trim() || null,
      memo: accountForm.memo.trim() || null,
      active: accountForm.active,
    })
    dialogVisible.value = false
    await refreshAll()
    workspaceStore.setSelectedAccountNo(accountForm.accountNo.trim())
  } finally {
    dialogLoading.value = false
  }
}

const handleDeleteAccount = (row: TradeRegisteredAccount) => {
  confirm.require({
    message: t('trade.account.deleteConfirmMessage', undefined, {
      accountNo: row.accountNo,
      accountName: row.accountName,
    }),
    header: t('trade.account.deleteConfirmTitle'),
    icon: 'pi pi-exclamation-triangle',
    acceptLabel: t('common.delete'),
    rejectLabel: t('common.close'),
    accept: async () => {
      await removeRegisteredAccount(row.id)
      await refreshAll()
    },
  })
}

watch(
  () => workspaceStore.selectedAccountNo,
  async () => {
    await refreshAccountSummaries()
    await refreshSelectedAccount()
  },
)

watch(
  () => workspaceStore.tradeMode,
  async () => {
    await refreshAll()
  },
)

onMounted(async () => {
  await refreshAll()
})
</script>

<template>
  <div class="page-stack">
    <BasePageHeader title="menu.accounts" :description="t('trade.account.pageDescription')" />

    <TradePanel :title="t('trade.account.registeredTitle')" :description="t('trade.account.registeredDescription')">
      <template #actions>
        <Dropdown
          :model-value="workspaceStore.tradeMode"
          :options="tradeModeOptions"
          option-label="label"
          option-value="value"
          class="trade-panel__dropdown"
          @update:model-value="workspaceStore.setTradeMode($event as TradeMode)"
        />
        <Dropdown
          :model-value="workspaceStore.selectedAccountNo"
          :options="accountOptions"
          option-label="label"
          option-value="value"
          :placeholder="t('trade.account.selectPlaceholder')"
          class="trade-panel__dropdown"
          @update:model-value="workspaceStore.setSelectedAccountNo(String($event ?? ''))"
        />
        <Button icon="pi pi-plus" :label="t('trade.account.addAccount')" size="small" @click="openCreateAccount" />
        <Button icon="pi pi-refresh" :label="t('common.refresh')" size="small" severity="secondary" :loading="loading" @click="refreshAll" />
      </template>

      <div class="trade-summary-strip">
        <div v-for="stat in homeStats" :key="stat.label" class="trade-summary-strip__item">
          <span>{{ stat.label }}</span>
          <strong>{{ stat.value }}</strong>
        </div>
      </div>

      <TradeRecordTable
        :columns="registeredAccountColumns"
        :rows="registeredAccounts.map((row) => ({ ...row, active: row.active ? t('common.yes') : t('common.no') })) as unknown as Array<Record<string, unknown>>"
        row-key="id"
      >
        <template #actions="{ row }">
          <Button text size="small" :label="t('common.select')" @click="workspaceStore.setSelectedAccountNo((row as unknown as TradeRegisteredAccount).accountNo)" />
          <Button text size="small" :label="t('common.edit')" @click="openEditAccount(row as unknown as TradeRegisteredAccount)" />
          <Button text size="small" severity="danger" :label="t('common.delete')" @click="handleDeleteAccount(row as unknown as TradeRegisteredAccount)" />
        </template>
      </TradeRecordTable>
    </TradePanel>

    <BaseEmptyState
      v-if="!workspaceStore.selectedAccountNo"
      :title="t('trade.account.emptyTitle')"
      :description="t('trade.account.emptyDescription')"
    />

    <template v-else>
      <TradePanel :title="t('trade.account.selectedSummaryTitle')" :description="t('trade.account.selectedSummaryDescription')">
        <template #actions>
          <span class="trade-chip-static">
            {{ selectedRegisteredAccount?.accountNo ?? workspaceStore.selectedAccountNo }} / {{ selectedRegisteredAccount?.accountName ?? '-' }}
          </span>
        </template>

        <TradeRecordTable :columns="summaryColumns" :rows="accountSummaries as unknown as Array<Record<string, unknown>>" row-key="accountNo" />
      </TradePanel>

      <TradePanel :title="t('trade.account.detailTitle')" :description="t('trade.account.detailDescription')">
        <template #actions>
          <InputText :model-value="balanceBaseDate" type="date" @update:model-value="balanceBaseDate = String($event ?? today)" />
          <Button icon="pi pi-search" :label="t('trade.account.inquiryAction')" size="small" @click="refreshSelectedAccount" />
        </template>

        <div class="trade-summary-strip">
          <div class="trade-summary-strip__item">
            <span>{{ t('trade.account.balance.totalAsset') }}</span>
            <strong>{{ Number(balance?.totalAssetAmount ?? 0).toLocaleString() }}</strong>
          </div>
          <div class="trade-summary-strip__item">
            <span>{{ t('trade.account.balance.cash') }}</span>
            <strong>{{ Number(balance?.cashAmount ?? 0).toLocaleString() }}</strong>
          </div>
          <div class="trade-summary-strip__item">
            <span>{{ t('trade.account.balance.orderableCash') }}</span>
            <strong>{{ Number(balance?.orderableCashAmount ?? 0).toLocaleString() }}</strong>
          </div>
          <div class="trade-summary-strip__item">
            <span>{{ t('trade.account.balance.totalProfitLoss') }}</span>
            <strong>{{ Number(balance?.totalProfitLossAmount ?? 0).toLocaleString() }}</strong>
          </div>
        </div>

        <TradeRecordTable :columns="positionColumns" :rows="positions as unknown as Array<Record<string, unknown>>" row-key="itemCode" />
      </TradePanel>

      <div class="trade-two-column">
        <TradePanel :title="t('trade.account.performanceTitle')" :description="t('trade.account.performanceDescription')">
          <template #actions>
            <InputText :model-value="performanceFrom" type="date" @update:model-value="performanceFrom = String($event ?? monthAgo)" />
            <InputText :model-value="performanceTo" type="date" @update:model-value="performanceTo = String($event ?? today)" />
            <Button icon="pi pi-chart-line" :label="t('trade.account.performanceAction')" size="small" @click="refreshSelectedAccount" />
          </template>

          <BaseLineChart
            :labels="performanceRows.map((row) => row.baseDate)"
            :series-name="t('trade.account.performanceSeries')"
            :points="performanceRows.map((row) => row.totalAssetAmount)"
          />
        </TradePanel>

        <TradePanel :title="t('trade.account.historyTitle')" :description="t('trade.account.historyDescription')">
          <TradeRecordTable :columns="historyColumns" :rows="histories as unknown as Array<Record<string, unknown>>" row-key="id" />
        </TradePanel>
      </div>
    </template>

    <BaseDialog
      :visible="dialogVisible"
      :title="dialogMode === 'create' ? t('trade.account.dialog.createTitle') : t('trade.account.dialog.editTitle')"
      @update:visible="dialogVisible = $event"
    >
      <div class="form-popup-stack">
        <label class="inline-input">
          <span>{{ t('trade.label.accountNo') }}</span>
          <InputText v-model="accountForm.accountNo" />
        </label>
        <label class="inline-input">
          <span>{{ t('trade.account.field.accountName') }}</span>
          <InputText v-model="accountForm.accountName" />
        </label>
        <label class="inline-input">
          <span>{{ t('trade.account.field.productCode') }}</span>
          <InputText v-model="accountForm.productCode" />
        </label>
        <label class="inline-input">
          <span>{{ t('trade.account.field.aliasName') }}</span>
          <InputText v-model="accountForm.aliasName" />
        </label>
        <label class="inline-input">
          <span>{{ t('trade.account.field.memo') }}</span>
          <InputText v-model="accountForm.memo" />
        </label>
        <label class="checkbox-line">
          <Checkbox v-model="accountForm.active" binary />
          <span>{{ t('trade.account.field.active') }}</span>
        </label>
        <div class="dialog-actions">
          <Button :label="t('common.close')" severity="secondary" @click="dialogVisible = false" />
          <Button :label="t('common.save')" :loading="dialogLoading" @click="handleSaveAccount" />
        </div>
      </div>
    </BaseDialog>
  </div>
</template>
