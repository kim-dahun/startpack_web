import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useToast } from 'primevue/usetoast'

import { getDropdownOptions, getManagementRows } from '@/api/modules/user'
import { useSessionStore } from '@/stores/session'
import type {
  CrudPayload,
  DataGridColumn,
  DataGridOption,
  ManagementQueryParams,
  ResourceDefinition,
} from '@/types/app'

const adminScopedResourceKeys = new Set([
  'groups',
  'groupMembers',
  'menus',
  'menuPermissions',
  'codeGroups',
  'codes',
])
const manageableServiceIds = ['TRADE', 'ERP', 'GROUPWARE'] as const
const groupScopedResourceKeys = new Set(['groupMembers', 'menuPermissions'])
const codeGroupScopedResourceKeys = new Set(['codes'])

const blockedPermissions = {
  permitRead: false,
  permitWrite: false,
  permitDelete: false,
  permitExcel: false,
}

interface UseManagementScreenOptions {
  resourceKey: string
  definition: ResourceDefinition
  listRows: (params: ManagementQueryParams) => Promise<Array<Record<string, unknown>>>
  saveRows: (
    payload: CrudPayload<Record<string, unknown>>,
    params: ManagementQueryParams,
  ) => Promise<Array<Record<string, unknown>>>
}

const buildOptions = (
  rows: Array<Record<string, unknown>>,
  valueField: string,
  labelField?: string,
  includeEmpty = false,
): DataGridOption[] => {
  const options = rows
    .map((row) => {
      const value = row[valueField]

      if (value === undefined || value === null || value === '') {
        return null
      }

      const label = labelField && row[labelField]
        ? `${String(value)} - ${String(row[labelField])}`
        : String(value)

      return {
        label,
        value: value as string | number | boolean,
      } satisfies DataGridOption
    })
    .filter((option): option is NonNullable<typeof option> => option !== null)

  return includeEmpty
    ? [{ label: 'Not Selected', value: '' }, ...options]
    : options
}

export const useManagementScreen = ({
  resourceKey,
  definition,
  listRows,
  saveRows,
}: UseManagementScreenOptions) => {
  const toast = useToast()
  const route = useRoute()
  const sessionStore = useSessionStore()
  const selectedServiceId = ref(sessionStore.persisted.serviceId ?? 'TRADE')
  const keyword = ref('')
  const rows = ref<Array<Record<string, unknown>>>([])
  const loading = ref(false)
  const selectedUserId = ref(sessionStore.persisted.user?.userId ?? '')
  const selectedGroupId = ref('')
  const selectedCodeGroupId = ref('')
  const groupOptions = ref<string[]>([])
  const codeGroupOptions = ref<string[]>([])
  const fieldOptions = ref<Record<string, DataGridOption[]>>({})

  const permissions = computed(() => {
    const basePermissions = sessionStore.getPermissions(route.meta.menuId as string)
    const needsServiceAdmin = adminScopedResourceKeys.has(resourceKey)

    if (needsServiceAdmin && !sessionStore.hasServiceAdmin(selectedServiceId.value)) {
      return blockedPermissions
    }

    return basePermissions
  })

  const serviceOptions = computed(() => (
    adminScopedResourceKeys.has(resourceKey)
      ? manageableServiceIds.filter((serviceId) => sessionStore.hasServiceAdmin(serviceId))
      : [sessionStore.persisted.serviceId ?? 'TRADE']
  ))

  const columns = computed<DataGridColumn[]>(() => definition.columns.map((column) => {
    const optionList = fieldOptions.value[column.field]
    const isScopedField = (
      column.field === 'serviceId'
      || (groupScopedResourceKeys.has(resourceKey) && column.field === 'groupId')
      || (codeGroupScopedResourceKeys.has(resourceKey) && column.field === 'codeGroupId')
    )

    return {
      ...column,
      editorOptions: optionList ?? column.editorOptions,
      editable: isScopedField ? false : column.editable,
    }
  }))

  const queryParams = computed<ManagementQueryParams>(() => ({
    comCd: sessionStore.persisted.user?.comCd ?? 'COM001',
    serviceId: definition.serviceScoped ? selectedServiceId.value : undefined,
    userId: definition.requiresUserId ? selectedUserId.value : undefined,
    groupId: groupScopedResourceKeys.has(resourceKey) ? selectedGroupId.value : undefined,
    codeGroupId: codeGroupScopedResourceKeys.has(resourceKey) ? selectedCodeGroupId.value : undefined,
  }))

  const rowDefaults = computed<Record<string, unknown>>(() => ({
    comCd: sessionStore.persisted.user?.comCd ?? 'COM001',
    ...(definition.serviceScoped ? { serviceId: selectedServiceId.value } : {}),
    ...(definition.requiresUserId ? { userId: selectedUserId.value } : {}),
    ...(groupScopedResourceKeys.has(resourceKey) ? { groupId: selectedGroupId.value } : {}),
    ...(codeGroupScopedResourceKeys.has(resourceKey) ? { codeGroupId: selectedCodeGroupId.value } : {}),
  }))

  const filteredRows = computed(() => {
    const normalized = keyword.value.trim().toLowerCase()

    if (!normalized) {
      return rows.value
    }

    return rows.value.filter((row) =>
      Object.values(row).some((value) => String(value).toLowerCase().includes(normalized)),
    )
  })

  const hasRequiredQueryParams = computed(() => {
    if (definition.serviceScoped && !selectedServiceId.value) {
      return false
    }

    if (definition.requiresUserId && !selectedUserId.value) {
      return false
    }

    if (groupScopedResourceKeys.has(resourceKey) && !selectedGroupId.value) {
      return false
    }

    if (codeGroupScopedResourceKeys.has(resourceKey) && !selectedCodeGroupId.value) {
      return false
    }

    return true
  })

  const syncSelectableOptions = async () => {
    const comCd = sessionStore.persisted.user?.comCd ?? 'COM001'

    const [userOptions, departmentOptions, positionOptions, jobGradeOptions, groups, menus, codeGroups, codeOptions] = await Promise.all([
      getDropdownOptions('users', { comCd }),
      getDropdownOptions('departments', { comCd }),
      getDropdownOptions('positions', { comCd }),
      getDropdownOptions('jobGrades', { comCd }),
      getManagementRows('groups', { comCd, serviceId: selectedServiceId.value }),
      getManagementRows('menus', { comCd, serviceId: selectedServiceId.value }),
      getManagementRows('codeGroups', { comCd, serviceId: selectedServiceId.value }),
      getDropdownOptions('codes', {
        comCd,
        serviceId: selectedServiceId.value,
        codeGroupId: selectedCodeGroupId.value || 'TEMP',
      }).catch(() => []),
    ])

    groupOptions.value = groups
      .map((row) => String(row.groupId ?? ''))
      .filter(Boolean)
    codeGroupOptions.value = codeGroups
      .map((row) => String(row.codeGroupId ?? ''))
      .filter(Boolean)

    selectedGroupId.value = groupOptions.value.includes(selectedGroupId.value)
      ? selectedGroupId.value
      : (groupOptions.value[0] ?? '')
    selectedCodeGroupId.value = codeGroupOptions.value.includes(selectedCodeGroupId.value)
      ? selectedCodeGroupId.value
      : (codeGroupOptions.value[0] ?? '')

    fieldOptions.value = {
      jobGradeId: [{ label: 'Not Selected', value: '' }, ...jobGradeOptions],
      departmentId: [{ label: 'Not Selected', value: '' }, ...departmentOptions],
      parentDepartmentId: [{ label: 'Not Selected', value: '' }, ...departmentOptions],
      departmentHeadUserId: [{ label: 'Not Selected', value: '' }, ...userOptions],
      departmentHeadPositionId: [{ label: 'Not Selected', value: '' }, ...positionOptions],
      status: [
        { label: 'ACTIVE', value: 'ACTIVE' },
        { label: 'LOCKED', value: 'LOCKED' },
        { label: 'INACTIVE', value: 'INACTIVE' },
      ],
      jobGradeType: [
        { label: 'GENERAL', value: 'GENERAL' },
        { label: 'RESEARCH', value: 'RESEARCH' },
        { label: 'CUSTOM', value: 'CUSTOM' },
      ],
      positionType: [
        { label: 'DEFAULT', value: 'DEFAULT' },
        { label: 'CUSTOM', value: 'CUSTOM' },
      ],
      userId: [{ label: 'Not Selected', value: '' }, ...userOptions],
      serviceId: manageableServiceIds.map((serviceId) => ({ label: serviceId, value: serviceId })),
      groupId: buildOptions(groups, 'groupId', 'groupName', true),
      menuParentId: buildOptions(menus, 'menuId', 'menuName', true),
      menuId: buildOptions(menus, 'menuId', 'menuName', true),
      menuLevel: [
        { label: '1', value: 1 },
        { label: '2', value: 2 },
        { label: '3', value: 3 },
      ],
      codeGroupId: buildOptions(codeGroups, 'codeGroupId', 'codeGroupName', true),
      parentCodeGroupId: buildOptions(codeGroups, 'codeGroupId', 'codeGroupName', true),
      parentCodeId: [{ label: 'Not Selected', value: '' }, ...codeOptions],
    }
  }

  const loadRows = async () => {
    if (!hasRequiredQueryParams.value) {
      rows.value = []
      return
    }

    loading.value = true

    try {
      rows.value = await listRows(queryParams.value)
    } finally {
      loading.value = false
    }
  }

  const handleSave = async (payload: CrudPayload<Record<string, unknown>>) => {
    try {
      rows.value = await saveRows(payload, queryParams.value)
      toast.add({
        severity: 'success',
        summary: 'Saved',
        detail: `${definition.title} changes were saved.`,
        life: 2500,
      })
    } catch (error) {
      toast.add({
        severity: 'error',
        summary: 'Save Failed',
        detail: error instanceof Error ? error.message : 'Failed to save changes.',
        life: 3000,
      })
    }
  }

  onMounted(() => {
    void (async () => {
      await syncSelectableOptions()
      await loadRows()
    })()
  })

  watch(
    () => [sessionStore.persisted.serviceId, sessionStore.adminServiceIds.join(','), resourceKey],
    async () => {
      const fallbackServiceId = sessionStore.persisted.serviceId ?? 'TRADE'
      selectedServiceId.value = serviceOptions.value.includes(selectedServiceId.value)
        ? selectedServiceId.value
        : serviceOptions.value[0] ?? fallbackServiceId

      await syncSelectableOptions()
    },
    { immediate: true },
  )

  watch(
    () => [selectedServiceId.value, selectedCodeGroupId.value],
    async () => {
      await syncSelectableOptions()
    },
  )

  watch(
    () => [selectedServiceId.value, selectedUserId.value, selectedGroupId.value, selectedCodeGroupId.value, resourceKey],
    () => {
      void loadRows()
    },
  )

  return reactive({
    permissions,
    keyword,
    rows,
    filteredRows,
    loading,
    selectedServiceId,
    selectedUserId,
    selectedGroupId,
    selectedCodeGroupId,
    serviceOptions,
    groupOptions,
    codeGroupOptions,
    columns,
    rowDefaults,
    loadRows,
    handleSave,
  })
}
