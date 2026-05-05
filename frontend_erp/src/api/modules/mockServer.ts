import {
  initialManagementData,
  mockMenus,
  mockPermissions,
  mockUser,
} from '@/data/mockData'
import { createRowId } from '@/utils/gridUtils'
import type { CrudPayload, LoginRequest, LoginResponse, ManagementQueryParams, UserGroup } from '@/types/app'

const clone = <T>(value: T): T => JSON.parse(JSON.stringify(value)) as T

const delay = async () => new Promise((resolve) => window.setTimeout(resolve, 140))

const managementState = clone(initialManagementData)
const serviceScopedResources = new Set(['groups', 'groupMembers', 'menus', 'menuPermissions', 'codeGroups', 'codes'])
const userScopedResources = new Set(['userPositions', 'serviceAccesses'])
const mockLoginProfiles: Record<string, { serviceAccesses: string[]; groups: UserGroup[]; roles: string[] }> = {
  admin: {
    serviceAccesses: ['TRADE', 'ERP', 'GROUPWARE'],
    groups: [
      {
        comCd: 'COM001',
        serviceId: 'TRADE',
        groupId: 'ADMIN',
        groupName: 'Trade Admin',
        description: 'TRADE administration access',
        enabled: true,
      },
      {
        comCd: 'COM001',
        serviceId: 'ERP',
        groupId: 'ADMIN',
        groupName: 'ERP Admin',
        description: 'ERP administration access',
        enabled: true,
      },
      {
        comCd: 'COM001',
        serviceId: 'GROUPWARE',
        groupId: 'ADMIN',
        groupName: 'Groupware Admin',
        description: 'GROUPWARE administration access',
        enabled: true,
      },
    ],
    roles: ['ADMIN'],
  },
  trader01: {
    serviceAccesses: ['TRADE', 'ERP'],
    groups: [
      {
        comCd: 'COM001',
        serviceId: 'TRADE',
        groupId: 'TRADE_USER',
        groupName: 'Trade User',
        description: 'TRADE service scoped access',
        enabled: true,
      },
    ],
    roles: ['USER'],
  },
  risk02: {
    serviceAccesses: ['ERP'],
    groups: [
      {
        comCd: 'COM001',
        serviceId: 'ERP',
        groupId: 'USER',
        groupName: 'ERP User',
        description: 'ERP service scoped access',
        enabled: true,
      },
    ],
    roles: ['USER'],
  },
}
const resourceKeyFields: Record<string, string[]> = {
  users: ['userId'],
  departments: ['departmentId'],
  jobGrades: ['jobGradeId'],
  positions: ['positionId'],
  userPositions: ['userPositionId'],
  serviceAccesses: ['userId', 'serviceId'],
  groups: ['serviceId', 'groupId'],
  groupMembers: ['serviceId', 'groupId', 'userId'],
  menus: ['serviceId', 'menuId'],
  menuPermissions: ['serviceId', 'groupId', 'menuId'],
  codeGroups: ['serviceId', 'codeGroupId'],
  codes: ['serviceId', 'codeGroupId', 'codeId'],
}

export const mockLogin = async (payload: LoginRequest): Promise<LoginResponse> => {
  await delay()

  if (!payload.comCd || !payload.userId || !payload.password || !payload.serviceId) {
    throw new Error('comCd, userId, password, serviceId는 필수입니다.')
  }

  const profile = mockLoginProfiles[payload.userId] ?? {
    serviceAccesses: ['ERP'],
    groups: [
      {
        comCd: payload.comCd,
        serviceId: 'ERP',
        groupId: 'USER',
        groupName: 'ERP User',
        description: 'ERP service scoped access',
        enabled: true,
      },
    ],
    roles: ['USER'],
  }
  const serviceAccesses = profile.serviceAccesses
  const groups = profile.groups.map((group) => ({
    ...group,
    comCd: payload.comCd,
  }))

  if (!serviceAccesses.includes(payload.serviceId)) {
    throw new Error(`${payload.serviceId} 서비스 접근 권한이 없어 로그인할 수 없습니다.`)
  }

  return {
    user: {
      ...mockUser,
      comCd: payload.comCd,
      userId: payload.userId,
      serviceAccesses,
    },
    serviceId: payload.serviceId,
    serviceAccesses,
    groups,
    token: {
      userId: payload.userId,
      loginId: payload.userId,
      accessToken: `mock-access-token-${payload.userId}`,
      accessTokenExpiresAt: new Date(Date.now() + 60 * 60 * 1000).toISOString(),
      refreshToken: `mock-refresh-token-${payload.userId}`,
      refreshTokenExpiresAt: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString(),
      roles: profile.roles,
    },
    menus: clone(mockMenus),
    menuPermissions: clone(mockPermissions),
  }
}

export const listManagementResource = async (resourceKey: string, params: ManagementQueryParams) => {
  await delay()
  let rows = clone(managementState[resourceKey] ?? [])

  if (serviceScopedResources.has(resourceKey) && params.serviceId) {
    rows = rows.filter((row: Record<string, unknown>) => row.serviceId === params.serviceId)
  }

  if (userScopedResources.has(resourceKey) && params.userId) {
    rows = rows.filter((row: Record<string, unknown>) => row.userId === params.userId)
  }

  return rows
}

export const saveManagementResource = async (
  resourceKey: string,
  payload: CrudPayload<Record<string, unknown>>,
  params: ManagementQueryParams,
) => {
  await delay()

  const current = managementState[resourceKey] ?? []
  const keys = resourceKeyFields[resourceKey] ?? []
  const matches = (left: Record<string, unknown>, right: Record<string, unknown>) =>
    keys.every((key) => left[key] === right[key])

  const kept = current.filter((row) => !payload.deleted.some((candidate) => matches(row, candidate)))
  const updated = kept.map((row) => {
    const matched = payload.updated.find((candidate) => matches(row, candidate))
    return matched ? { ...row, ...matched } : row
  })

  const withContext = (item: Record<string, unknown>) => ({
    ...item,
    ...(serviceScopedResources.has(resourceKey) && params.serviceId ? { serviceId: item.serviceId ?? params.serviceId } : {}),
    ...(userScopedResources.has(resourceKey) && params.userId ? { userId: item.userId ?? params.userId } : {}),
  })

  managementState[resourceKey] = [
    ...updated,
    ...payload.added.map((item) => ({ rowId: createRowId(), ...withContext(item) })),
  ]

  return listManagementResource(resourceKey, params)
}
