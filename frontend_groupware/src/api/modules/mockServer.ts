import {
  accountSummaries,
  dailyBalances,
  initialManagementData,
  mockMenus,
  mockPermissions,
  mockUser,
  realtimeQuotes,
  realtimeStatus,
  tradeHistories,
  tradeItems,
  watchlistItems,
} from '@/data/mockData'
import { createRowId } from '@/utils/gridUtils'
import { formatDateTime } from '@/utils/formatUtils'
import type { CrudPayload, LoginRequest, LoginResponse, ManagementQueryParams, UserGroup, WatchlistItem } from '@/types/app'

const clone = <T>(value: T): T => JSON.parse(JSON.stringify(value)) as T

const delay = async () => new Promise((resolve) => window.setTimeout(resolve, 140))

const managementState = clone(initialManagementData)
const watchlistState = clone(watchlistItems)
const realtimeState = clone(realtimeQuotes)
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
        groupId: 'ERP_USER',
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
    serviceAccesses: ['TRADE'],
    groups: [
      {
        comCd: payload.comCd,
        serviceId: 'TRADE',
        groupId: 'TRADE_USER',
        groupName: 'Trade User',
        description: 'TRADE service scoped access',
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

export const getAccounts = async () => {
  await delay()
  return clone(accountSummaries)
}

export const getDailyBalances = async () => {
  await delay()
  return clone(dailyBalances)
}

export const getTradeItems = async (keyword = '') => {
  await delay()
  const normalized = keyword.toLowerCase()
  return clone(
    tradeItems.filter((item) => {
      if (!normalized) {
        return true
      }

      return (
        item.itemCode.toLowerCase().includes(normalized)
        || item.itemName.toLowerCase().includes(normalized)
        || item.marketCode.toLowerCase().includes(normalized)
      )
    }),
  )
}

export const getTradeItem = async (itemCode: string) => {
  await delay()
  return clone(tradeItems.find((item) => item.itemCode === itemCode) ?? tradeItems[0])
}

export const getWatchlist = async (userId: string) => {
  await delay()
  return clone(watchlistState.filter((item) => item.userId === userId))
}

export const addWatchlist = async (payload: Omit<WatchlistItem, 'id'>) => {
  await delay()

  const exists = watchlistState.some(
    (item) => item.userId === payload.userId && item.itemCode === payload.itemCode,
  )

  if (exists) {
    throw new Error('이미 등록된 관심종목입니다.')
  }

  const nextItem: WatchlistItem = { id: createRowId(), ...payload }
  watchlistState.push(nextItem)
  return clone(nextItem)
}

export const removeWatchlist = async (watchlistId: string) => {
  await delay()
  const index = watchlistState.findIndex((item) => item.id === watchlistId)

  if (index >= 0) {
    watchlistState.splice(index, 1)
  }
}

export const getTradeHistories = async () => {
  await delay()
  return clone(tradeHistories)
}

export const getRealtimeStatus = async () => {
  await delay()
  return clone(realtimeStatus)
}

export const getRealtimeQuotes = async () => {
  await delay()

  realtimeState.forEach((quote) => {
    const deltaFactor = quote.symbol === 'paper-account' ? 0.0012 : 0.009
    const swing = quote.price * (Math.random() * deltaFactor - deltaFactor / 2)
    quote.price = Number((quote.price + swing).toFixed(quote.symbol === 'AAPL' ? 2 : 0))
    quote.changeRate = Number((((quote.price - quote.basePrice) / quote.basePrice) * 100).toFixed(2))
    quote.occurredAt = new Date().toISOString()
  })

  return clone(realtimeState)
}

export const getChartSeries = async (itemCode: string) => {
  await delay()

  const seed = tradeItems.find((item) => item.itemCode === itemCode) ?? tradeItems[0]
  const points = Array.from({ length: 12 }, (_, index) => {
    const baseline = seed.price
    const noise = Math.sin(index / 2) * baseline * 0.01 + (Math.random() - 0.5) * baseline * 0.008
    return Number((baseline + noise).toFixed(seed.itemCode === 'AAPL' ? 2 : 0))
  })

  return {
    itemCode: seed.itemCode,
    itemName: seed.itemName,
    points,
    labels: points.map((_, index) => `${9 + index}:00`),
    lastUpdated: formatDateTime(new Date()),
  }
}
