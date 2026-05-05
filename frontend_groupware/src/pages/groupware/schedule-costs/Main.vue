<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import Button from 'primevue/button'
import InputNumber from 'primevue/inputnumber'
import InputText from 'primevue/inputtext'
import { useToast } from 'primevue/usetoast'

import BaseDialog from '@/components/common/BaseDialog.vue'
import BasePageHeader from '@/components/common/BasePageHeader.vue'
import BaseStatCard from '@/components/common/BaseStatCard.vue'
import { useAppI18n } from '@/composables/useAppI18n'
import GroupwarePanel from '@/components/groupware/GroupwarePanel.vue'
import GroupwareRecordTable from '@/components/groupware/GroupwareRecordTable.vue'
import { useSessionStore } from '@/stores/session'
import type {
  GroupwareCostAccount,
  GroupwareCostItem,
  GroupwareProjectItem,
  GroupwareScheduleCost,
  GroupwareScheduleItem,
} from '@/types/groupware'
import {
  createCostAccount,
  createCostItem,
  createScheduleCost,
  deleteCostAccount,
  deleteCostItem,
  deleteScheduleCost,
  getScheduleCostSummaryByProjectCode,
  listCostAccounts,
  listCostItems,
  listProjects,
  listSchedulesByProjectCode,
  searchScheduleCosts,
  updateCostAccount,
  updateCostItem,
  updateScheduleCost,
} from './api/api'

type CostDialogMode = 'cost' | 'item' | 'account'
type DialogIntent = 'create' | 'edit'

const sessionStore = useSessionStore()
const toast = useToast()
const { t } = useAppI18n()

const permissions = computed(() => sessionStore.getPermissions(sessionStore.persisted.currentMenuId))

const projects = ref<GroupwareProjectItem[]>([])
const selectedProjectCode = ref('')
const costs = ref<GroupwareScheduleCost[]>([])
const schedules = ref<GroupwareScheduleItem[]>([])
const costItems = ref<GroupwareCostItem[]>([])
const costAccounts = ref<GroupwareCostAccount[]>([])
const dialogMode = ref<CostDialogMode>('cost')
const dialogIntent = ref<DialogIntent>('create')
const dialogVisible = ref(false)
const selectedCostId = ref('')
const selectedItemId = ref('')
const selectedAccountId = ref('')
const searchCostItemId = ref('')
const searchAccountId = ref('')
const searchFrom = ref('')
const searchTo = ref('')
const totalAmount = ref(0)

const projectOptions = computed(() =>
  projects.value.map((project) => ({
    label: project.projectCode || project.name,
    value: project.projectCode || '',
    projectId: project.projectId,
  })).filter((project) => Boolean(project.value)),
)
const costRows = computed(() => costs.value.map((item) => ({
  scheduleCostId: item.scheduleCostId,
  projectCode: item.projectCode || '-',
  costDate: item.costDate,
  amount: item.amount,
  description: item.description || '-',
})))
const itemRows = computed(() => costItems.value.map((item) => ({
  costItemId: item.costItemId,
  costItemName: item.costItemName,
  enabled: item.enabled ? 'Y' : 'N',
})))
const accountRows = computed(() => costAccounts.value.map((item) => ({
  accountId: item.accountId,
  accountName: item.accountName,
  enabled: item.enabled ? 'Y' : 'N',
})))

const columns = [
  { field: 'projectCode', title: 'common.project' },
  { field: 'costDate', title: 'common.date' },
  { field: 'amount', title: 'groupware.totalAmount' },
  { field: 'description', title: 'common.description' },
]

const itemColumns = [
  { field: 'costItemName', title: 'groupware.costItem' },
  { field: 'enabled', title: 'common.enabled' },
]

const accountColumns = [
  { field: 'accountName', title: 'groupware.costAccount' },
  { field: 'enabled', title: 'common.enabled' },
]

const costForm = reactive({
  scheduleId: '',
  costDate: '',
  costItemId: '',
  accountId: '',
  amount: 0,
  description: '',
})

const itemForm = reactive({
  costItemName: '',
  enabled: true,
})

const accountForm = reactive({
  accountName: '',
  enabled: true,
})

function resetForms() {
  costForm.scheduleId = ''
  costForm.costDate = ''
  costForm.costItemId = ''
  costForm.accountId = ''
  costForm.amount = 0
  costForm.description = ''
  itemForm.costItemName = ''
  itemForm.enabled = true
  accountForm.accountName = ''
  accountForm.enabled = true
  selectedCostId.value = ''
  selectedItemId.value = ''
  selectedAccountId.value = ''
}

async function loadProjects() {
  projects.value = await listProjects()
  if (!projects.value.some((project) => project.projectCode === selectedProjectCode.value)) {
    selectedProjectCode.value = projects.value.find((project) => Boolean(project.projectCode))?.projectCode ?? ''
  }
}

async function loadCosts() {
  costs.value = await searchScheduleCosts({
    projectCode: selectedProjectCode.value || undefined,
    costItemId: searchCostItemId.value || undefined,
    accountId: searchAccountId.value || undefined,
    from: searchFrom.value || undefined,
    to: searchTo.value || undefined,
  })

  if (selectedProjectCode.value) {
    totalAmount.value = (await getScheduleCostSummaryByProjectCode(selectedProjectCode.value)).totalAmount ?? 0
  } else {
    totalAmount.value = costs.value.reduce((sum, item) => sum + Number(item.amount ?? 0), 0)
  }
}

async function loadSchedules() {
  schedules.value = selectedProjectCode.value ? await listSchedulesByProjectCode(selectedProjectCode.value) : []
}

async function loadMeta() {
  const [items, accounts] = await Promise.all([listCostItems(), listCostAccounts()])
  costItems.value = items
  costAccounts.value = accounts
}

function openDialog(mode: CostDialogMode, intent: DialogIntent, id = '') {
  dialogMode.value = mode
  dialogIntent.value = intent
  resetForms()

  if (mode === 'cost' && intent === 'edit') {
    const target = costs.value.find((item) => item.scheduleCostId === id)
    if (target) {
      selectedCostId.value = id
      costForm.scheduleId = target.scheduleId ?? ''
      costForm.costDate = target.costDate
      costForm.costItemId = target.costItemId ?? ''
      costForm.accountId = target.accountId ?? ''
      costForm.amount = Number(target.amount ?? 0)
      costForm.description = target.description ?? ''
    }
  }

  if (mode === 'item' && intent === 'edit') {
    const target = costItems.value.find((item) => item.costItemId === id)
    if (target) {
      selectedItemId.value = id
      itemForm.costItemName = target.costItemName
      itemForm.enabled = Boolean(target.enabled)
    }
  }

  if (mode === 'account' && intent === 'edit') {
    const target = costAccounts.value.find((item) => item.accountId === id)
    if (target) {
      selectedAccountId.value = id
      accountForm.accountName = target.accountName
      accountForm.enabled = Boolean(target.enabled)
    }
  }

  dialogVisible.value = true
}

async function handleSave() {
  if (!permissions.value.permitWrite) {
    return
  }

  if (dialogMode.value === 'cost') {
    const project = projectOptions.value.find((item) => item.value === selectedProjectCode.value)
    const payload = {
      scheduleId: costForm.scheduleId,
      projectId: project?.projectId ?? null,
      projectCode: selectedProjectCode.value || null,
      costDate: costForm.costDate,
      costItemId: costForm.costItemId,
      accountId: costForm.accountId,
      amount: Number(costForm.amount ?? 0),
      description: costForm.description.trim(),
    }

    if (dialogIntent.value === 'edit' && selectedCostId.value) {
      await updateScheduleCost(selectedCostId.value, payload)
    } else {
      await createScheduleCost(payload)
    }
    await loadCosts()
  } else if (dialogMode.value === 'item') {
    const payload = {
      costItemName: itemForm.costItemName.trim(),
      enabled: itemForm.enabled,
    }

    if (dialogIntent.value === 'edit' && selectedItemId.value) {
      await updateCostItem(selectedItemId.value, payload)
    } else {
      await createCostItem(payload)
    }
    await loadMeta()
  } else {
    const payload = {
      accountName: accountForm.accountName.trim(),
      enabled: accountForm.enabled,
    }

    if (dialogIntent.value === 'edit' && selectedAccountId.value) {
      await updateCostAccount(selectedAccountId.value, payload)
    } else {
      await createCostAccount(payload)
    }
    await loadMeta()
  }

  dialogVisible.value = false
  toast.add({ severity: 'success', summary: t('common.summary.saved'), detail: t('common.detail.saved'), life: 2200 })
}

async function handleDelete(mode: CostDialogMode, id: string) {
  if (!permissions.value.permitDelete) {
    return
  }

  if (mode === 'cost') {
    await deleteScheduleCost(id)
    await loadCosts()
    return
  }

  if (mode === 'item') {
    await deleteCostItem(id)
    await loadMeta()
    return
  }

  await deleteCostAccount(id)
  await loadMeta()
}

onMounted(async () => {
  await Promise.all([loadProjects(), loadMeta()])
  await Promise.all([loadCosts(), loadSchedules()])
})

watch(selectedProjectCode, () => {
  void Promise.all([loadCosts(), loadSchedules()])
})
</script>

<template>
  <div class="page-stack">
    <BasePageHeader title="groupware.scheduleCosts" description="Project-linked cost CRUD wired to backend_groupware cost APIs." />

    <section class="stats-grid">
      <BaseStatCard :label="t('groupware.status.projectCode')" :value="selectedProjectCode || t('common.notSelected')" />
      <BaseStatCard :label="t('groupware.status.costRows')" :value="String(costs.length)" />
      <BaseStatCard :label="t('groupware.totalAmount')" :value="String(totalAmount)" />
    </section>

    <GroupwarePanel title="groupware.costLedger" description="Create, search, edit, and delete cost rows plus cost items and accounts.">
      <template #actions>
        <div class="trade-inline-actions">
          <Button v-if="permissions.permitWrite" size="small" icon="pi pi-plus" :label="t('common.add')" @click="openDialog('cost', 'create')" />
          <Button v-if="permissions.permitWrite" size="small" severity="secondary" :label="t('groupware.costItem')" @click="openDialog('item', 'create')" />
          <Button v-if="permissions.permitWrite" size="small" severity="secondary" :label="t('groupware.costAccount')" @click="openDialog('account', 'create')" />
        </div>
      </template>

      <div class="base-search-form__body">
        <div class="base-search-form__fields">
          <label class="inline-input">
            <span>{{ t('common.project') }}</span>
            <select
              :value="selectedProjectCode"
              class="native-select"
              @change="selectedProjectCode = String(($event.target as HTMLSelectElement).value)"
            >
              <option value="">{{ t('common.all') }}</option>
              <option v-for="project in projectOptions" :key="project.value" :value="project.value">
                {{ project.label }}
              </option>
            </select>
          </label>
          <label class="inline-input">
            <span>{{ t('groupware.costItem') }}</span>
            <select v-model="searchCostItemId" class="native-select">
              <option value="">{{ t('common.all') }}</option>
              <option v-for="item in costItems" :key="item.costItemId" :value="item.costItemId">{{ item.costItemName }}</option>
            </select>
          </label>
          <label class="inline-input">
            <span>{{ t('groupware.costAccount') }}</span>
            <select v-model="searchAccountId" class="native-select">
              <option value="">{{ t('common.all') }}</option>
              <option v-for="account in costAccounts" :key="account.accountId" :value="account.accountId">{{ account.accountName }}</option>
            </select>
          </label>
          <label class="inline-input">
            <span>{{ t('common.from') }}</span>
            <input v-model="searchFrom" class="native-select" type="date">
          </label>
          <label class="inline-input">
            <span>{{ t('common.to') }}</span>
            <input v-model="searchTo" class="native-select" type="date">
          </label>
        </div>
        <div class="base-search-form__actions">
          <Button :label="t('common.search')" severity="secondary" @click="loadCosts" />
          <Button :label="t('common.refresh')" @click="loadCosts" />
        </div>
      </div>

      <GroupwareRecordTable :columns="columns" :rows="costRows" row-key="scheduleCostId">
        <template #actions="{ row }">
          <div class="trade-inline-actions">
            <Button size="small" severity="secondary" :label="t('common.edit')" :disabled="!permissions.permitWrite" @click="openDialog('cost', 'edit', String(row.scheduleCostId))" />
            <Button size="small" severity="danger" :label="t('common.delete')" :disabled="!permissions.permitDelete" @click="handleDelete('cost', String(row.scheduleCostId))" />
          </div>
        </template>
      </GroupwareRecordTable>
    </GroupwarePanel>

    <div class="admin-split-layout">
      <GroupwarePanel title="groupware.costItem" description="Enabled cost items.">
        <GroupwareRecordTable :columns="itemColumns" :rows="itemRows" row-key="costItemId">
          <template #actions="{ row }">
            <div class="trade-inline-actions">
              <Button size="small" severity="secondary" :label="t('common.edit')" :disabled="!permissions.permitWrite" @click="openDialog('item', 'edit', String(row.costItemId))" />
              <Button size="small" severity="danger" :label="t('common.delete')" :disabled="!permissions.permitDelete" @click="handleDelete('item', String(row.costItemId))" />
            </div>
          </template>
        </GroupwareRecordTable>
      </GroupwarePanel>

      <GroupwarePanel title="groupware.costAccount" description="Enabled settlement accounts.">
        <GroupwareRecordTable :columns="accountColumns" :rows="accountRows" row-key="accountId">
          <template #actions="{ row }">
            <div class="trade-inline-actions">
              <Button size="small" severity="secondary" :label="t('common.edit')" :disabled="!permissions.permitWrite" @click="openDialog('account', 'edit', String(row.accountId))" />
              <Button size="small" severity="danger" :label="t('common.delete')" :disabled="!permissions.permitDelete" @click="handleDelete('account', String(row.accountId))" />
            </div>
          </template>
        </GroupwareRecordTable>
      </GroupwarePanel>
    </div>

    <BaseDialog
      :visible="dialogVisible"
      :title="dialogIntent === 'create' ? t('common.add') : t('common.edit')"
      @update:visible="dialogVisible = $event"
    >
      <div class="form-popup-stack">
        <template v-if="dialogMode === 'cost'">
          <label class="inline-input">
            <span>Schedule</span>
            <select v-model="costForm.scheduleId" class="native-select">
              <option value="">{{ t('common.notSelected') }}</option>
              <option v-for="schedule in schedules" :key="schedule.scheduleId" :value="schedule.scheduleId">
                {{ schedule.title }} ({{ schedule.startAt.slice(0, 10) }})
              </option>
            </select>
          </label>
          <label class="inline-input">
            <span>{{ t('common.date') }}</span>
            <input v-model="costForm.costDate" class="native-select" type="date">
          </label>
          <label class="inline-input">
            <span>{{ t('groupware.costItem') }}</span>
            <select v-model="costForm.costItemId" class="native-select">
              <option value="">{{ t('common.notSelected') }}</option>
              <option v-for="item in costItems" :key="item.costItemId" :value="item.costItemId">{{ item.costItemName }}</option>
            </select>
          </label>
          <label class="inline-input">
            <span>{{ t('groupware.costAccount') }}</span>
            <select v-model="costForm.accountId" class="native-select">
              <option value="">{{ t('common.notSelected') }}</option>
              <option v-for="account in costAccounts" :key="account.accountId" :value="account.accountId">{{ account.accountName }}</option>
            </select>
          </label>
          <label class="inline-input">
            <span>{{ t('groupware.totalAmount') }}</span>
            <InputNumber v-model="costForm.amount" :min="0" fluid />
          </label>
          <label class="inline-input">
            <span>{{ t('common.description') }}</span>
            <textarea v-model="costForm.description" class="trade-textarea"></textarea>
          </label>
        </template>

        <template v-else-if="dialogMode === 'item'">
          <label class="inline-input">
            <span>{{ t('groupware.costItem') }}</span>
            <InputText v-model="itemForm.costItemName" />
          </label>
        </template>

        <template v-else>
          <label class="inline-input">
            <span>{{ t('groupware.costAccount') }}</span>
            <InputText v-model="accountForm.accountName" />
          </label>
        </template>

        <div class="dialog-actions">
          <Button :label="t('common.close')" severity="secondary" @click="dialogVisible = false" />
          <Button :label="dialogIntent === 'create' ? t('common.create') : t('common.save')" :disabled="!permissions.permitWrite" @click="handleSave" />
        </div>
      </div>
    </BaseDialog>
  </div>
</template>
