import http from '@/api/client/http'
import type { ApiEnvelope, CrudPayload, DataGridOption, ManagementQueryParams } from '@/types/app'

const endpoints = {
  users: {
    list: '/api/users',
    bulk: '/api/users/bulk',
    requiredParams: ['comCd'],
    requestFields: ['comCd', 'userId', 'userName', 'password', 'email', 'phone', 'address', 'jobGradeId', 'status'],
  },
  departments: {
    list: '/api/users/departments',
    bulk: '/api/users/departments/bulk',
    requiredParams: ['comCd'],
    requestFields: ['comCd', 'departmentId', 'departmentName', 'parentDepartmentId', 'departmentHeadUserId', 'departmentHeadPositionId', 'sortSeq', 'enabled'],
  },
  jobGrades: {
    list: '/api/users/job-grades',
    bulk: '/api/users/job-grades/bulk',
    requiredParams: ['comCd'],
    requestFields: ['comCd', 'jobGradeId', 'jobGradeName', 'jobGradeType', 'sortSeq', 'enabled'],
  },
  positions: {
    list: '/api/users/positions',
    bulk: '/api/users/positions/bulk',
    requiredParams: ['comCd'],
    requestFields: ['comCd', 'positionId', 'positionName', 'positionType', 'sortSeq', 'enabled'],
  },
  userPositions: {
    list: '/api/users/user-positions',
    bulk: '/api/users/user-positions/bulk',
    requiredParams: ['comCd', 'userId'],
    requestFields: ['comCd', 'userPositionId', 'userId', 'departmentId', 'positionId', 'primaryYn', 'enabled'],
  },
  serviceAccesses: {
    list: '/api/users/service-accesses',
    bulk: '/api/users/service-accesses/bulk',
    requiredParams: ['comCd', 'userId'],
    requestFields: ['comCd', 'userId', 'serviceId', 'accessible'],
  },
  groups: {
    list: '/api/users/groups',
    bulk: '/api/users/groups/bulk',
    requiredParams: ['comCd', 'serviceId'],
    requestFields: ['comCd', 'serviceId', 'groupId', 'groupName', 'description', 'enabled'],
  },
  groupMembers: {
    list: '/api/users/groups/group-members',
    bulk: '/api/users/groups/group-members/bulk',
    requiredParams: ['comCd', 'serviceId', 'groupId'],
    requestFields: ['comCd', 'serviceId', 'groupId', 'userId'],
  },
  menus: {
    list: '/api/users/menus',
    bulk: '/api/users/menus/bulk',
    requiredParams: ['comCd', 'serviceId'],
    requestFields: ['comCd', 'serviceId', 'menuId', 'menuParentId', 'menuName', 'menuUrl', 'i18nCode', 'icon', 'menuLevel', 'sortSeq', 'enabled'],
  },
  menuPermissions: {
    list: '/api/users/menus/menu-permissions',
    bulk: '/api/users/menus/menu-permissions/bulk',
    requiredParams: ['comCd', 'serviceId', 'groupId'],
    requestFields: ['comCd', 'serviceId', 'groupId', 'menuId', 'permitRead', 'permitWrite', 'permitDelete', 'permitExcel'],
  },
  codeGroups: {
    list: '/api/users/code-groups',
    bulk: '/api/users/code-groups/bulk',
    requiredParams: ['comCd', 'serviceId'],
    requestFields: ['comCd', 'serviceId', 'codeGroupId', 'codeGroupName', 'description', 'enabled'],
  },
  codes: {
    list: '/api/users/codes',
    bulk: '/api/users/codes/bulk',
    requiredParams: ['comCd', 'serviceId', 'codeGroupId'],
    requestFields: ['comCd', 'serviceId', 'codeGroupId', 'codeId', 'codeName', 'parentCodeGroupId', 'parentCodeId', 'subInfo1', 'subInfo2', 'subInfo3', 'sortSeq', 'enabled'],
  },
} as const

const dropdownEndpoints = {
  departments: '/api/users/dropdown/departments',
  jobGrades: '/api/users/dropdown/job-grades',
  positions: '/api/users/dropdown/positions',
  users: '/api/users/dropdown/users',
  codes: '/api/users/dropdown/codes',
} as const

type ResourceKey = keyof typeof endpoints
type DropdownKey = keyof typeof dropdownEndpoints

type MenuTreeResponse = {
  comCd: string
  serviceId: string
  menuId: string
  menuParentId: string
  menuName: string
  menuUrl: string
  i18nCode: string
  icon: string
  menuLevel: number
  sortSeq: number
  enabled: boolean
  children?: MenuTreeResponse[]
}

type MenuPermissionTreeResponse = {
  comCd: string
  serviceId: string
  groupId: string
  menuId: string
  menuParentId: string
  menuName: string
  permitRead: boolean
  permitWrite: boolean
  permitDelete: boolean
  permitExcel: boolean
  children?: MenuPermissionTreeResponse[]
}

export interface OrganizationUserResponse {
  comCd: string
  userId: string
  userName: string
  jobGradeId: string
  jobGradeName: string
  affiliations: Array<{
    comCd: string
    userPositionId: string
    departmentId: string
    departmentName: string
    positionId: string
    positionName: string
    primaryYn: boolean
  }>
}

export interface UserPositionRow {
  comCd: string
  userPositionId: string
  userId: string
  departmentId: string
  positionId: string
  primaryYn: boolean
  enabled: boolean
}

const pickFields = (row: Record<string, unknown>, fields: readonly string[]) => fields.reduce<Record<string, unknown>>((accumulator, field) => {
  if (field in row) {
    accumulator[field] = row[field]
  }

  return accumulator
}, {})

const validateParams = (resourceKey: ResourceKey, params: ManagementQueryParams) => {
  const config = endpoints[resourceKey]
  const missingKeys = config.requiredParams.filter((key) => {
    const value = params[key as keyof ManagementQueryParams]
    return value === undefined || value === null || value === ''
  })

  if (missingKeys.length) {
    throw new Error(`${resourceKey} query requires: ${missingKeys.join(', ')}`)
  }
}

const toRequestPayload = (resourceKey: ResourceKey, payload: CrudPayload<Record<string, unknown>>) => {
  const fields = endpoints[resourceKey].requestFields
  return {
    added: payload.added.map((row) => pickFields(row, fields)),
    updated: payload.updated.map((row) => pickFields(row, fields)),
    deleted: payload.deleted.map((row) => pickFields(row, fields)),
  }
}

export const getManagementRows = async (resourceKey: string, params: ManagementQueryParams) => {
  const typedKey = resourceKey as ResourceKey
  validateParams(typedKey, params)

  const response = await http.get<ApiEnvelope<Array<Record<string, unknown>>>>(endpoints[typedKey].list, {
    params,
  })

  return response.data.data
}

export const saveManagementRows = async (
  resourceKey: string,
  payload: CrudPayload<Record<string, unknown>>,
  params: ManagementQueryParams,
) => {
  const typedKey = resourceKey as ResourceKey
  validateParams(typedKey, params)

  await http.post<ApiEnvelope<unknown>>(endpoints[typedKey].bulk, toRequestPayload(typedKey, payload))

  return getManagementRows(typedKey, params)
}

export const getDropdownOptions = async (
  dropdownKey: DropdownKey,
  params: Pick<ManagementQueryParams, 'comCd' | 'serviceId' | 'codeGroupId'>,
) => {
  const response = await http.get<ApiEnvelope<DataGridOption[]>>(dropdownEndpoints[dropdownKey], {
    params,
  })

  return response.data.data ?? []
}

export const getMenuTreeRows = async (params: Pick<ManagementQueryParams, 'comCd' | 'serviceId'>) => {
  const response = await http.get<ApiEnvelope<MenuTreeResponse[]>>('/api/users/menus/tree', {
    params,
  })

  return response.data.data ?? []
}

export const getMenuPermissionTreeRows = async (
  params: Pick<ManagementQueryParams, 'comCd' | 'serviceId' | 'groupId'>,
) => {
  const response = await http.get<ApiEnvelope<MenuPermissionTreeResponse[]>>('/api/users/menus/menu-permissions/tree', {
    params,
  })

  return response.data.data ?? []
}

export const saveMenuTreeRows = async (
  payload: CrudPayload<Record<string, unknown>>,
  params: Pick<ManagementQueryParams, 'comCd' | 'serviceId'>,
) => {
  await http.post<ApiEnvelope<unknown>>('/api/users/menus/bulk', toRequestPayload('menus', payload))
  return getMenuTreeRows(params)
}

export const saveMenuPermissionTreeRows = async (
  payload: CrudPayload<Record<string, unknown>>,
  params: Pick<ManagementQueryParams, 'comCd' | 'serviceId' | 'groupId'>,
) => {
  await http.post<ApiEnvelope<unknown>>('/api/users/menus/menu-permissions/bulk', toRequestPayload('menuPermissions', payload))
  return getMenuPermissionTreeRows(params)
}

export const getOrganizationUsers = async (params: { comCd: string; departmentId?: string; keyword?: string }) => {
  const response = await http.get<ApiEnvelope<OrganizationUserResponse[]>>('/api/users/organization/users', {
    params,
  })

  return response.data.data ?? []
}

export const getDepartmentMembers = async (params: { comCd: string; departmentId: string }) => {
  const response = await http.get<ApiEnvelope<UserPositionRow[]>>('/api/users/user-positions/department-members', {
    params,
  })

  return response.data.data ?? []
}

export const saveDepartmentMembers = async (
  payload: CrudPayload<Record<string, unknown>>,
  params: { comCd: string; departmentId: string },
) => {
  await http.post<ApiEnvelope<unknown>>('/api/users/user-positions/department-members/bulk', {
    added: payload.added,
    updated: payload.updated,
    deleted: payload.deleted,
  })

  return getDepartmentMembers(params)
}

export const getUserAffiliations = async (params: { comCd: string; userId: string }) => {
  const response = await http.get<ApiEnvelope<UserPositionRow[]>>('/api/users/user-positions', {
    params,
  })

  return response.data.data ?? []
}

export const updateUserPrimaryYn = async (payload: {
  comCd: string
  userId: string
  departmentId: string
  positionId: string
  primaryYn: boolean
}) => {
  const response = await http.post<ApiEnvelope<UserPositionRow>>('/api/users/user-positions/primary-yn', payload)
  return response.data.data
}
