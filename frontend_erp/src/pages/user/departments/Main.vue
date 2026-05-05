<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import Button from 'primevue/button'
import Checkbox from 'primevue/checkbox'
import InputNumber from 'primevue/inputnumber'
import InputText from 'primevue/inputtext'
import type { TreeExpandedKeys } from 'primevue/tree'
import { useConfirm } from 'primevue/useconfirm'
import { useToast } from 'primevue/usetoast'

import BaseDialog from '@/components/common/BaseDialog.vue'
import BaseEmptyState from '@/components/common/BaseEmptyState.vue'
import BaseForbiddenState from '@/components/common/BaseForbiddenState.vue'
import BasePageHeader from '@/components/common/BasePageHeader.vue'
import BaseSearchForm from '@/components/form/BaseSearchForm.vue'
import { useAppI18n } from '@/composables/useAppI18n'
import { useSessionStore } from '@/stores/session'
import type { DataGridOption } from '@/types/app'
import type { UserPositionRow } from '@/api/modules/user'
import { listDepartmentMembers, listDepartmentOptions, listDepartments, listPositionOptions, listUserOptions, saveDepartments, saveMembers, type DepartmentRow } from './api/api'
import DepartmentOrgChartPopupView from './popup-view/DepartmentOrgChartPopupView.vue'
import DepartmentTreeView from './sub-view/DepartmentTreeView.vue'

type DialogMode = 'create' | 'edit'
type MemberDialogMode = 'create' | 'edit'
type DepartmentTreeNode = {
  key: string
  label: string
  data: DepartmentRow
  children: DepartmentTreeNode[]
}

const sessionStore = useSessionStore()
const route = useRoute()
const toast = useToast()
const confirm = useConfirm()
const { t } = useAppI18n()

const permissions = computed(() => sessionStore.getPermissions(route.meta.menuId as string))
const comCd = computed(() => sessionStore.persisted.user?.comCd ?? 'COM001')

const keyword = ref('')
const departments = ref<DepartmentRow[]>([])
const selectedDepartmentId = ref('')
const departmentOptions = ref<DataGridOption[]>([])
const userOptions = ref<DataGridOption[]>([])
const positionOptions = ref<DataGridOption[]>([])
const members = ref<UserPositionRow[]>([])
const treeExpandedKeys = ref<TreeExpandedKeys>({})
const orgChartVisible = ref(false)

const dialogVisible = ref(false)
const dialogMode = ref<DialogMode>('create')
const dialogLoading = ref(false)
const memberDialogVisible = ref(false)
const memberDialogMode = ref<MemberDialogMode>('create')
const memberDialogLoading = ref(false)

const departmentForm = reactive({
  departmentId: '',
  departmentName: '',
  parentDepartmentId: '',
  departmentHeadUserId: '',
  departmentHeadPositionId: '',
  sortSeq: 1,
  enabled: true,
})

const memberForm = reactive({
  userId: '',
  positionId: '',
  enabled: true,
})

const filteredDepartments = computed(() => {
  const normalized = keyword.value.trim().toLowerCase()
  if (!normalized) {
    return departments.value
  }
  return departments.value.filter((department) =>
    [department.departmentId, department.departmentName]
      .some((value) => String(value ?? '').toLowerCase().includes(normalized)),
  )
})

const buildDepartmentTree = (rows: DepartmentRow[]) => {
  const lookup = new Map<string, DepartmentTreeNode>()
  const roots: DepartmentTreeNode[] = []

  rows.forEach((department) => {
    lookup.set(department.departmentId, {
      key: department.departmentId,
      label: department.departmentName,
      data: department,
      children: [],
    })
  })

  rows.forEach((department) => {
    const node = lookup.get(department.departmentId)
    if (!node) {
      return
    }

    const parentId = department.parentDepartmentId?.trim()
    if (parentId && lookup.has(parentId)) {
      lookup.get(parentId)?.children.push(node)
      return
    }

    roots.push(node)
  })

  const sortNodes = (nodes: DepartmentTreeNode[]) => {
    nodes.sort((left, right) => {
      const seqGap = Number(left.data.sortSeq ?? 0) - Number(right.data.sortSeq ?? 0)
      if (seqGap !== 0) {
        return seqGap
      }
      return left.label.localeCompare(right.label)
    })
    nodes.forEach((node) => sortNodes(node.children))
  }

  sortNodes(roots)
  return roots
}

const filterDepartmentTree = (nodes: DepartmentTreeNode[], normalized: string): DepartmentTreeNode[] =>
  nodes.flatMap((node) => {
    const nextChildren = filterDepartmentTree(node.children, normalized)
    const matched = [node.key, node.label].some((value) => value.toLowerCase().includes(normalized))

    if (!matched && !nextChildren.length) {
      return []
    }

    return [{
      ...node,
      children: nextChildren,
    }]
  })

const collectExpandedKeys = (nodes: DepartmentTreeNode[]) =>
  nodes.reduce<TreeExpandedKeys>((accumulator, node) => {
    if (node.children.length) {
      accumulator[node.key] = true
      Object.assign(accumulator, collectExpandedKeys(node.children))
    }
    return accumulator
  }, {})

const departmentTreeNodes = computed(() => {
  const normalized = keyword.value.trim().toLowerCase()
  const tree = buildDepartmentTree(departments.value)
  return normalized ? filterDepartmentTree(tree, normalized) : tree
})

const toOrgChartNode = (node: DepartmentTreeNode): Record<string, unknown> => ({
  key: node.key,
  label: node.label,
  expanded: true,
  data: node.data,
  children: node.children.map((child) => toOrgChartNode(child)),
})

const orgChartNodes = computed(() =>
  buildDepartmentTree(departments.value).map((node) => toOrgChartNode(node)),
)

const selectedDepartment = computed(() => departments.value.find((department) => department.departmentId === selectedDepartmentId.value) ?? null)

const decoratedMembers = computed(() => members.value.map((member) => ({
  ...member,
  lockedHead: Boolean(
    selectedDepartment.value
    && member.userId === selectedDepartment.value.departmentHeadUserId
    && member.positionId === selectedDepartment.value.departmentHeadPositionId,
  ),
})))

const resetDepartmentForm = () => {
  departmentForm.departmentId = ''
  departmentForm.departmentName = ''
  departmentForm.parentDepartmentId = ''
  departmentForm.departmentHeadUserId = ''
  departmentForm.departmentHeadPositionId = ''
  departmentForm.sortSeq = 1
  departmentForm.enabled = true
}

const resetMemberForm = () => {
  memberForm.userId = ''
  memberForm.positionId = ''
  memberForm.enabled = true
}

const loadDropdowns = async () => {
  const [nextDepartments, nextUsers, nextPositions] = await Promise.all([
    listDepartmentOptions({ comCd: comCd.value }),
    listUserOptions({ comCd: comCd.value }),
    listPositionOptions({ comCd: comCd.value }),
  ])
  departmentOptions.value = nextDepartments
  userOptions.value = nextUsers
  positionOptions.value = nextPositions
}

const loadDepartments = async () => {
  departments.value = await listDepartments({ comCd: comCd.value })
  if (!departments.value.some((department) => department.departmentId === selectedDepartmentId.value)) {
    selectedDepartmentId.value = departments.value[0]?.departmentId ?? ''
  }
  treeExpandedKeys.value = collectExpandedKeys(buildDepartmentTree(departments.value))
}

const loadMembers = async () => {
  if (!selectedDepartmentId.value) {
    members.value = []
    return
  }
  members.value = await listDepartmentMembers({
    comCd: comCd.value,
    departmentId: selectedDepartmentId.value,
  })
}

const loadAll = async () => {
  await Promise.all([loadDropdowns(), loadDepartments()])
  await loadMembers()
}

const openCreateDepartment = () => {
  dialogMode.value = 'create'
  resetDepartmentForm()
  dialogVisible.value = true
}

const openEditDepartment = () => {
  if (!selectedDepartment.value) {
    return
  }
  dialogMode.value = 'edit'
  departmentForm.departmentId = selectedDepartment.value.departmentId
  departmentForm.departmentName = selectedDepartment.value.departmentName
  departmentForm.parentDepartmentId = selectedDepartment.value.parentDepartmentId ?? ''
  departmentForm.departmentHeadUserId = selectedDepartment.value.departmentHeadUserId ?? ''
  departmentForm.departmentHeadPositionId = selectedDepartment.value.departmentHeadPositionId ?? ''
  departmentForm.sortSeq = Number(selectedDepartment.value.sortSeq ?? 1)
  departmentForm.enabled = Boolean(selectedDepartment.value.enabled)
  dialogVisible.value = true
}

const handleSaveDepartment = async () => {
  if (!departmentForm.departmentId.trim() || !departmentForm.departmentName.trim()) {
    toast.add({
      severity: 'warn',
      summary: t('common.requiredTitle'),
      detail: t('departments.requiredDepartment'),
      life: 2200,
    })
    return
  }

  dialogLoading.value = true
  try {
    departments.value = await saveDepartments(dialogMode.value === 'edit'
      ? { added: [], updated: [{ ...departmentForm, comCd: comCd.value, departmentId: departmentForm.departmentId.trim(), departmentName: departmentForm.departmentName.trim() }], deleted: [] }
      : { added: [{ ...departmentForm, comCd: comCd.value, departmentId: departmentForm.departmentId.trim(), departmentName: departmentForm.departmentName.trim() }], updated: [], deleted: [] }, { comCd: comCd.value })
    selectedDepartmentId.value = departmentForm.departmentId.trim()
    dialogVisible.value = false
    await loadMembers()
  } finally {
    dialogLoading.value = false
  }
}

const handleDeleteDepartment = () => {
  if (!selectedDepartment.value) {
    return
  }
  confirm.require({
    message: t('departments.deleteDepartment', undefined, { name: selectedDepartment.value.departmentName }),
    header: t('common.delete'),
    icon: 'pi pi-exclamation-triangle',
    acceptLabel: t('common.delete'),
    rejectLabel: t('common.close'),
    accept: async () => {
      departments.value = await saveDepartments({ added: [], updated: [], deleted: [selectedDepartment.value as unknown as Record<string, unknown>] }, { comCd: comCd.value })
      selectedDepartmentId.value = departments.value[0]?.departmentId ?? ''
      await loadMembers()
    },
  })
}

const openCreateMember = () => {
  if (!selectedDepartmentId.value) {
    return
  }
  memberDialogMode.value = 'create'
  resetMemberForm()
  memberDialogVisible.value = true
}

const openEditMember = (row: UserPositionRow & { lockedHead?: boolean }) => {
  if (row.lockedHead) {
    return
  }
  memberDialogMode.value = 'edit'
  memberForm.userId = row.userId
  memberForm.positionId = row.positionId
  memberForm.enabled = row.enabled
  memberDialogVisible.value = true
}

const handleSaveMember = async () => {
  if (!selectedDepartmentId.value || !memberForm.userId || !memberForm.positionId) {
    toast.add({
      severity: 'warn',
      summary: t('common.requiredTitle'),
      detail: t('departments.requiredMember'),
      life: 2200,
    })
    return
  }

  const row = {
    comCd: comCd.value,
    departmentId: selectedDepartmentId.value,
    userId: memberForm.userId,
    positionId: memberForm.positionId,
    enabled: Boolean(memberForm.enabled),
  }

  memberDialogLoading.value = true
  try {
    members.value = await saveMembers(memberDialogMode.value === 'edit'
      ? { added: [], updated: [row], deleted: [] }
      : { added: [row], updated: [], deleted: [] }, {
      comCd: comCd.value,
      departmentId: selectedDepartmentId.value,
    })
    memberDialogVisible.value = false
  } finally {
    memberDialogLoading.value = false
  }
}

const handleDeleteMember = (row: UserPositionRow & { lockedHead?: boolean }) => {
  if (row.lockedHead) {
    toast.add({
      severity: 'warn',
      summary: t('common.delete'),
      detail: t('departments.lockedMemberDelete'),
      life: 2200,
    })
    return
  }
  confirm.require({
    message: t('departments.deleteMember', undefined, { userId: row.userId, positionId: row.positionId }),
    header: t('common.delete'),
    icon: 'pi pi-exclamation-triangle',
    acceptLabel: t('common.delete'),
    rejectLabel: t('common.close'),
    accept: async () => {
      members.value = await saveMembers({
        added: [],
        updated: [],
        deleted: [{ comCd: row.comCd, departmentId: row.departmentId, userId: row.userId, positionId: row.positionId, enabled: row.enabled }],
      }, {
        comCd: comCd.value,
        departmentId: selectedDepartmentId.value,
      })
    },
  })
}

watch(selectedDepartmentId, async () => {
  await loadMembers()
})

watch(() => comCd.value, async () => {
  await loadAll()
}, { immediate: true })
</script>

<template>
  <div class="page-stack">
    <BasePageHeader title="menu.departments" />
    <BaseForbiddenState v-if="!permissions.permitRead" />
    <template v-else>
      <BaseSearchForm
        :model-value="keyword"
        placeholder="departmentId, departmentName"
        title="search.title"
        @update:model-value="keyword = String($event ?? '')"
        @search="loadDepartments"
      >
        <label class="inline-input">
          <span>{{ t('common.keyword') }}</span>
          <InputText :model-value="keyword" placeholder="departmentId, departmentName" @update:model-value="keyword = String($event ?? '')" />
        </label>
      </BaseSearchForm>

      <div class="admin-split-layout">
        <DepartmentTreeView
          :nodes="departmentTreeNodes as unknown as Array<Record<string, unknown>>"
          :selected-key="selectedDepartmentId"
          :expanded-keys="treeExpandedKeys"
          :can-write="permissions.permitWrite"
          :can-delete="permissions.permitDelete"
          @select="selectedDepartmentId = $event"
          @update:expanded-keys="treeExpandedKeys = $event"
          @create="openCreateDepartment"
          @edit="openEditDepartment"
          @remove="handleDeleteDepartment"
          @refresh="loadAll"
          @open-org-chart="orgChartVisible = true"
        />

        <BaseEmptyState
          v-if="!selectedDepartmentId"
          :title="t('menu.departments')"
          description="departments.noSelectionDescription"
        />

        <section v-else class="split-panel">
          <header class="split-panel__header">
            <strong>{{ t('menu.userPositions') }}</strong>
            <Button icon="pi pi-plus" :label="t('common.add')" size="small" :disabled="!permissions.permitWrite" @click="openCreateMember" />
          </header>
          <div class="simple-admin-table-wrap">
            <table class="simple-admin-table">
              <thead>
                <tr>
                  <th>{{ t('common.user') }}</th>
                  <th>{{ t('common.position') }}</th>
                  <th>{{ t('common.primary') }}</th>
                  <th>{{ t('common.enabled') }}</th>
                  <th>{{ t('common.actions') }}</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="row in decoratedMembers" :key="row.userPositionId">
                  <td>{{ row.userId }}</td>
                  <td>{{ row.positionId }}</td>
                  <td>{{ row.primaryYn ? 'Y' : 'N' }}</td>
                  <td>{{ row.enabled ? 'Y' : 'N' }}</td>
                  <td>
                    <div class="split-panel__actions">
                      <Button :label="t('common.edit')" size="small" severity="secondary" :disabled="!permissions.permitWrite || row.lockedHead" @click="openEditMember(row)" />
                      <Button :label="t('common.delete')" size="small" severity="danger" :disabled="!permissions.permitDelete || row.lockedHead" @click="handleDeleteMember(row)" />
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>
      </div>
    </template>

    <BaseDialog :visible="dialogVisible" :title="dialogMode === 'create' ? `${t('common.add')} ${t('menu.departments')}` : `${t('common.edit')} ${t('menu.departments')}`" @update:visible="dialogVisible = $event">
      <div class="form-popup-stack">
        <label class="inline-input"><span>{{ t('departments.field.departmentId') }}</span><InputText v-model="departmentForm.departmentId" /></label>
        <label class="inline-input"><span>{{ t('departments.field.departmentName') }}</span><InputText v-model="departmentForm.departmentName" /></label>
        <label class="inline-input">
          <span>{{ t('departments.field.parentDepartment') }}</span>
          <select v-model="departmentForm.parentDepartmentId" class="native-select">
            <option value="">{{ t('common.notSelected') }}</option>
            <option v-for="option in departmentOptions" :key="String(option.value)" :value="String(option.value ?? '')">{{ option.label }}</option>
          </select>
        </label>
        <label class="inline-input">
          <span>{{ t('departments.field.headUser') }}</span>
          <select v-model="departmentForm.departmentHeadUserId" class="native-select">
            <option value="">{{ t('common.notSelected') }}</option>
            <option v-for="option in userOptions" :key="String(option.value)" :value="String(option.value ?? '')">{{ option.label }}</option>
          </select>
        </label>
        <label class="inline-input">
          <span>{{ t('departments.field.headPosition') }}</span>
          <select v-model="departmentForm.departmentHeadPositionId" class="native-select">
            <option value="">{{ t('common.notSelected') }}</option>
            <option v-for="option in positionOptions" :key="String(option.value)" :value="String(option.value ?? '')">{{ option.label }}</option>
          </select>
        </label>
        <label class="inline-input"><span>{{ t('departments.field.sortSeq') }}</span><InputNumber v-model="departmentForm.sortSeq" :min="1" fluid /></label>
        <label class="checkbox-line"><Checkbox v-model="departmentForm.enabled" binary /><span>{{ t('common.enabled') }}</span></label>
        <div class="dialog-actions">
          <Button :label="t('common.close')" severity="secondary" @click="dialogVisible = false" />
          <Button :label="t('common.save')" :loading="dialogLoading" @click="handleSaveDepartment" />
        </div>
      </div>
    </BaseDialog>

    <BaseDialog :visible="memberDialogVisible" :title="memberDialogMode === 'create' ? `${t('common.add')} ${t('menu.userPositions')}` : `${t('common.edit')} ${t('menu.userPositions')}`" @update:visible="memberDialogVisible = $event">
      <div class="form-popup-stack">
        <label class="inline-input">
          <span>{{ t('common.user') }}</span>
          <select v-model="memberForm.userId" class="native-select">
            <option value="">{{ t('common.notSelected') }}</option>
            <option v-for="option in userOptions" :key="String(option.value)" :value="String(option.value ?? '')">{{ option.label }}</option>
          </select>
        </label>
        <label class="inline-input">
          <span>{{ t('common.position') }}</span>
          <select v-model="memberForm.positionId" class="native-select">
            <option value="">{{ t('common.notSelected') }}</option>
            <option v-for="option in positionOptions" :key="String(option.value)" :value="String(option.value ?? '')">{{ option.label }}</option>
          </select>
        </label>
        <label class="checkbox-line"><Checkbox v-model="memberForm.enabled" binary /><span>{{ t('common.enabled') }}</span></label>
        <div class="dialog-actions">
          <Button :label="t('common.close')" severity="secondary" @click="memberDialogVisible = false" />
          <Button :label="t('common.save')" :loading="memberDialogLoading" @click="handleSaveMember" />
        </div>
      </div>
    </BaseDialog>

    <DepartmentOrgChartPopupView
      :visible="orgChartVisible"
      :nodes="orgChartNodes as unknown as Array<Record<string, unknown>>"
      @update:visible="orgChartVisible = $event"
    />
  </div>
</template>
