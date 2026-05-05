import http from '@/api/client/http'
import type {
  TradeAccountBalance,
  TradeAccountPosition,
  TradeAccountSummary,
  TradeCashOrderRequest,
  TradeCashOrderResult,
  TradeChartDrawing,
  TradeChartPeriodType,
  TradeChartPoint,
  TradeDailyBalance,
  TradeDryRunHistoryRequest,
  TradeEventItem,
  TradeFrequentSearchItem,
  TradeHistoryRecord,
  TradeItemDetail,
  TradeItemIndicators,
  TradeItemMetrics,
  TradeItemPrice,
  TradeItemSummary,
  TradeKisCallLog,
  TradeMasterImportHistory,
  TradeMasterImportRequestRow,
  TradeMasterStatus,
  TradeMasterTypeOption,
  TradeMode,
  TradeOrderValidationRequest,
  TradeOrderValidationResult,
  TradeOrderableAmountRequest,
  TradeOrderableAmountResult,
  TradeOrderbook,
  TradePerformanceRecord,
  TradePublishRealtimeEventRequest,
  TradeRankingRow,
  TradeRankingType,
  TradeRealtimeStatus,
  TradeRegisteredAccount,
  TradeReconnectHistory,
  TradeReconnectHistoryFilter,
  TradeSearchCandidate,
  TradeWatchlistCreateRequest,
  TradeWatchlistGroup,
  TradeWatchlistGroupCreateRequest,
  TradeWatchlistItem,
  TradeWatchlistMetadataUpdateRequest,
  TradeWorkspaceChartSnapshot,
  TradeWorkspaceSnapshot,
  TradeWorkspaceTradingSnapshot,
} from '@/types/trade'

const TRADE_BASE_PATH = '/api/trade'
const DEFAULT_TRADE_MODE: TradeMode = 'LIVE'

const unwrapTradePayload = <T>(payload: unknown): T => {
  if (
    payload
    && typeof payload === 'object'
    && 'success' in payload
    && 'data' in payload
  ) {
    return (payload as { data: T }).data
  }

  return payload as T
}
const asArray = <T>(value: T[] | null | undefined) => (Array.isArray(value) ? value : [])
const asRecord = <T extends Record<string, unknown>>(value: T | null | undefined) =>
  (value && typeof value === 'object' && !Array.isArray(value) ? value : {} as T)

export const listAccounts = async (params?: { tradeMode?: TradeMode; accountNo?: string; accountNumbers?: string[] }) => {
  const response = await http.get<TradeAccountSummary[]>(`${TRADE_BASE_PATH}/accounts`, {
    params: {
      tradeMode: params?.tradeMode ?? DEFAULT_TRADE_MODE,
      ...(params?.accountNo ? { accountNo: params.accountNo } : {}),
      ...(params?.accountNumbers?.length ? { accountNumbers: params.accountNumbers.join(',') } : {}),
    },
  })
  return asArray(unwrapTradePayload<TradeAccountSummary[]>(response.data))
}

export const listRegisteredAccounts = async () => {
  const response = await http.get<TradeRegisteredAccount[]>(`${TRADE_BASE_PATH}/accounts/registered`)
  return asArray(unwrapTradePayload<TradeRegisteredAccount[]>(response.data))
}

export const createRegisteredAccount = async (payload: {
  accountNo: string
  accountName: string
  productCode: string
  aliasName?: string | null
  memo?: string | null
  active: boolean
}) => {
  const response = await http.post<TradeRegisteredAccount>(`${TRADE_BASE_PATH}/accounts/registered`, payload)
  return unwrapTradePayload<TradeRegisteredAccount>(response.data)
}

export const updateRegisteredAccount = async (
  id: number,
  payload: {
    accountNo: string
    accountName: string
    productCode: string
    aliasName?: string | null
    memo?: string | null
    active: boolean
  },
) => {
  const response = await http.patch<TradeRegisteredAccount>(`${TRADE_BASE_PATH}/accounts/registered/${id}`, payload)
  return unwrapTradePayload<TradeRegisteredAccount>(response.data)
}

export const deleteRegisteredAccount = async (id: number) => {
  await http.delete(`${TRADE_BASE_PATH}/accounts/registered/${id}`)
}

export const getAccountBalances = async (params: { accountNo: string; tradeMode?: TradeMode }) => {
  const response = await http.get<TradeAccountBalance>(`${TRADE_BASE_PATH}/accounts/${params.accountNo}/balances`, {
    params: { tradeMode: params.tradeMode ?? DEFAULT_TRADE_MODE },
  })
  return unwrapTradePayload<TradeAccountBalance>(response.data)
}

export const listAccountPositions = async (params: { accountNo: string; tradeMode?: TradeMode }) => {
  const response = await http.get<TradeAccountPosition[]>(`${TRADE_BASE_PATH}/accounts/${params.accountNo}/positions`, {
    params: { tradeMode: params.tradeMode ?? DEFAULT_TRADE_MODE },
  })
  return asArray(unwrapTradePayload<TradeAccountPosition[]>(response.data))
}

export const listDailyBalances = async (params: { accountNo: string; baseDate: string; tradeMode?: TradeMode }) => {
  const response = await http.get<TradeDailyBalance[]>(`${TRADE_BASE_PATH}/accounts/daily-balances`, {
    params: {
      accountNo: params.accountNo,
      baseDate: params.baseDate,
      tradeMode: params.tradeMode ?? DEFAULT_TRADE_MODE,
    },
  })
  return asArray(unwrapTradePayload<TradeDailyBalance[]>(response.data))
}

export const listItems = async (params: { keyword?: string; tradeMode?: TradeMode }) => {
  const response = await http.get<TradeItemSummary[]>(`${TRADE_BASE_PATH}/items`, {
    params: { keyword: params.keyword ?? '', tradeMode: params.tradeMode ?? DEFAULT_TRADE_MODE },
  })
  return asArray(unwrapTradePayload<TradeItemSummary[]>(response.data))
}

export const searchItems = async (params: { keyword: string; tradeMode?: TradeMode }) => {
  const response = await http.get<Record<string, TradeSearchCandidate>>(`${TRADE_BASE_PATH}/items/search`, {
    params: { keyword: params.keyword, tradeMode: params.tradeMode ?? DEFAULT_TRADE_MODE },
  })
  return asRecord<Record<string, TradeSearchCandidate>>(unwrapTradePayload<Record<string, TradeSearchCandidate>>(response.data))
}

export const getItemDetail = async (itemCode: string, tradeMode: TradeMode = DEFAULT_TRADE_MODE) => {
  const response = await http.get<TradeItemDetail>(`${TRADE_BASE_PATH}/items/${itemCode}`, {
    params: { tradeMode },
  })
  return unwrapTradePayload<TradeItemDetail>(response.data)
}

export const getItemQuote = async (itemCode: string, tradeMode: TradeMode = DEFAULT_TRADE_MODE) => {
  const response = await http.get<TradeItemPrice>(`${TRADE_BASE_PATH}/items/${itemCode}/quote`, {
    params: { tradeMode },
  })
  return unwrapTradePayload<TradeItemPrice>(response.data)
}

export const getItemPrice = async (itemCode: string, tradeMode: TradeMode = DEFAULT_TRADE_MODE) => {
  const response = await http.get<TradeItemPrice>(`${TRADE_BASE_PATH}/items/${itemCode}/price`, {
    params: { tradeMode },
  })
  return unwrapTradePayload<TradeItemPrice>(response.data)
}

export const listItemQuotes = async (params: { itemCodes: string[]; tradeMode?: TradeMode }) => {
  const response = await http.post<Record<string, TradeItemPrice>>(`${TRADE_BASE_PATH}/items/quotes`, {
    itemCodes: params.itemCodes,
    tradeMode: params.tradeMode ?? DEFAULT_TRADE_MODE,
  })
  return asRecord<Record<string, TradeItemPrice>>(unwrapTradePayload<Record<string, TradeItemPrice>>(response.data))
}

export const getWorkspaceSnapshot = async (params: {
  itemCode: string
  userId?: string | null
  tradeMode?: TradeMode
}) => {
  const response = await http.get<TradeWorkspaceSnapshot>(`${TRADE_BASE_PATH}/workspace/${params.itemCode}/snapshot`, {
    params: {
      ...(params.userId ? { userId: params.userId } : {}),
      tradeMode: params.tradeMode ?? DEFAULT_TRADE_MODE,
    },
  })
  const payload = unwrapTradePayload<TradeWorkspaceSnapshot | null>(response.data)
  return payload ?? {
    itemCode: params.itemCode,
    userId: params.userId ?? null,
    quote: null,
    orderbook: null,
    metrics: null,
    watchlistItems: [],
    frequentSearches: [],
  }
}

export const getWorkspaceChartSnapshot = async (params: {
  itemCode: string
  from: string
  to: string
  interval?: TradeChartPeriodType
  userId?: string | null
  tradeMode?: TradeMode
}) => {
  const response = await http.get<TradeWorkspaceChartSnapshot>(`${TRADE_BASE_PATH}/workspace/${params.itemCode}/chart-snapshot`, {
    params: {
      from: params.from,
      to: params.to,
      interval: params.interval ?? 'DAY',
      ...(params.userId ? { userId: params.userId } : {}),
      tradeMode: params.tradeMode ?? DEFAULT_TRADE_MODE,
    },
  })
  const payload = unwrapTradePayload<TradeWorkspaceChartSnapshot | null>(response.data)
  return payload ?? {
    itemCode: params.itemCode,
    interval: params.interval ?? 'DAY',
    candles: [],
    indicators: null,
    drawings: [],
  }
}

export const getWorkspaceTradingSnapshot = async (params: {
  itemCode: string
  accountNo: string
  price?: number | null
  tradeMode?: TradeMode
}) => {
  const response = await http.get<TradeWorkspaceTradingSnapshot>(`${TRADE_BASE_PATH}/workspace/${params.itemCode}/trading-snapshot`, {
    params: {
      accountNo: params.accountNo,
      ...(params.price ? { price: params.price } : {}),
      tradeMode: params.tradeMode ?? DEFAULT_TRADE_MODE,
    },
  })
  const payload = unwrapTradePayload<TradeWorkspaceTradingSnapshot | null>(response.data)
  return payload ?? {
    itemCode: params.itemCode,
    accountNo: params.accountNo,
    balance: null,
    positions: [],
    currentPosition: null,
    orderableAmount: null,
  }
}

export const getItemOrderbook = async (itemCode: string, tradeMode: TradeMode = DEFAULT_TRADE_MODE) => {
  const response = await http.get<TradeOrderbook>(`${TRADE_BASE_PATH}/items/${itemCode}/orderbook`, {
    params: { tradeMode },
  })
  return unwrapTradePayload<TradeOrderbook>(response.data)
}

export const listItemOrderbooks = async (params: { itemCodes: string[]; tradeMode?: TradeMode }) => {
  const response = await http.post<TradeOrderbook[]>(`${TRADE_BASE_PATH}/items/orderbooks`, {
    itemCodes: params.itemCodes,
    tradeMode: params.tradeMode ?? DEFAULT_TRADE_MODE,
  })
  return asArray(unwrapTradePayload<TradeOrderbook[]>(response.data))
}

export const listItemChart = async (params: {
  itemCode: string
  periodType: TradeChartPeriodType
  from: string
  to: string
  tradeMode?: TradeMode
}) => {
  const response = await http.get<TradeChartPoint[]>(`${TRADE_BASE_PATH}/items/${params.itemCode}/chart`, {
    params: {
      periodType: params.periodType,
      from: params.from,
      to: params.to,
      tradeMode: params.tradeMode ?? DEFAULT_TRADE_MODE,
    },
  })
  return asArray(unwrapTradePayload<TradeChartPoint[]>(response.data))
}

export const getItemIndicators = async (params: {
  itemCode: string
  periodType: TradeChartPeriodType
  from: string
  to: string
  tradeMode?: TradeMode
}) => {
  const response = await http.get<TradeItemIndicators>(`${TRADE_BASE_PATH}/items/${params.itemCode}/indicators`, {
    params: {
      periodType: params.periodType,
      from: params.from,
      to: params.to,
      tradeMode: params.tradeMode ?? DEFAULT_TRADE_MODE,
    },
  })
  return unwrapTradePayload<TradeItemIndicators>(response.data)
}

export const getItemSummary = async (itemCode: string, tradeMode: TradeMode = DEFAULT_TRADE_MODE) => {
  const response = await http.get<TradeItemMetrics>(`${TRADE_BASE_PATH}/items/${itemCode}/summary`, {
    params: { tradeMode },
  })
  return unwrapTradePayload<TradeItemMetrics>(response.data)
}

export const getItemMetrics = async (itemCode: string, tradeMode: TradeMode = DEFAULT_TRADE_MODE) => {
  const response = await http.get<TradeItemMetrics>(`${TRADE_BASE_PATH}/items/${itemCode}/metrics`, {
    params: { tradeMode },
  })
  return unwrapTradePayload<TradeItemMetrics>(response.data)
}

export const listItemDrawings = async (itemCode: string, userId: string) => {
  const response = await http.get<TradeChartDrawing[]>(`${TRADE_BASE_PATH}/items/${itemCode}/drawings`, {
    params: { userId },
  })
  return asArray(unwrapTradePayload<TradeChartDrawing[]>(response.data))
}

export const createItemDrawing = async (itemCode: string, payload: Omit<TradeChartDrawing, 'id' | 'userId' | 'itemCode'> & { userId: string }) => {
  const response = await http.post<TradeChartDrawing>(`${TRADE_BASE_PATH}/items/${itemCode}/drawings`, payload)
  return unwrapTradePayload<TradeChartDrawing>(response.data)
}

export const updateItemDrawing = async (
  itemCode: string,
  drawingId: number,
  payload: Omit<TradeChartDrawing, 'id' | 'userId' | 'itemCode'> & { userId: string },
) => {
  const response = await http.patch<TradeChartDrawing>(`${TRADE_BASE_PATH}/items/${itemCode}/drawings/${drawingId}`, payload)
  return unwrapTradePayload<TradeChartDrawing>(response.data)
}

export const deleteItemDrawing = async (itemCode: string, drawingId: number, userId: string) => {
  await http.delete(`${TRADE_BASE_PATH}/items/${itemCode}/drawings/${drawingId}`, {
    params: { userId },
  })
}

export const listWatchlist = async (params: { userId: string; groupId?: number | null }) => {
  const response = await http.get<TradeWatchlistItem[]>(`${TRADE_BASE_PATH}/watchlist`, {
    params: {
      userId: params.userId,
      ...(params.groupId ? { groupId: params.groupId } : {}),
    },
  })
  return asArray(unwrapTradePayload<TradeWatchlistItem[]>(response.data))
}

export const createWatchlistItem = async (payload: { userId: string } & TradeWatchlistCreateRequest) => {
  const response = await http.post<TradeWatchlistItem>(`${TRADE_BASE_PATH}/watchlist`, payload)
  return unwrapTradePayload<TradeWatchlistItem>(response.data)
}

export const updateWatchlistItemMetadata = async (id: number, payload: TradeWatchlistMetadataUpdateRequest) => {
  const response = await http.patch<TradeWatchlistItem>(`${TRADE_BASE_PATH}/watchlist/${id}/metadata`, payload)
  return unwrapTradePayload<TradeWatchlistItem>(response.data)
}

export const deleteWatchlistItem = async (id: number) => {
  await http.delete(`${TRADE_BASE_PATH}/watchlist/${id}`)
}

export const listWatchlistGroups = async (userId: string) => {
  const response = await http.get<TradeWatchlistGroup[]>(`${TRADE_BASE_PATH}/watchlist/groups`, {
    params: { userId },
  })
  return asArray(unwrapTradePayload<TradeWatchlistGroup[]>(response.data))
}

export const createWatchlistGroup = async (payload: { userId: string } & TradeWatchlistGroupCreateRequest) => {
  const response = await http.post<TradeWatchlistGroup>(`${TRADE_BASE_PATH}/watchlist/groups`, payload)
  return unwrapTradePayload<TradeWatchlistGroup>(response.data)
}

export const deleteWatchlistGroup = async (id: number) => {
  await http.delete(`${TRADE_BASE_PATH}/watchlist/groups/${id}`)
}

export const listFrequentSearches = async (userId: string) => {
  const response = await http.get<TradeFrequentSearchItem[]>(`${TRADE_BASE_PATH}/items/frequent-searches`, {
    params: { userId },
  })
  return asArray(unwrapTradePayload<TradeFrequentSearchItem[]>(response.data))
}

export const recordFrequentSearch = async (payload: {
  userId: string
  itemCode: string
  itemName: string
  marketCode: string
}) => {
  const response = await http.post<TradeFrequentSearchItem>(`${TRADE_BASE_PATH}/items/frequent-searches`, payload)
  return unwrapTradePayload<TradeFrequentSearchItem>(response.data)
}

export const listTradeHistories = async (params: { accountNo: string; tradeMode?: TradeMode }) => {
  const response = await http.get<TradeHistoryRecord[]>(`${TRADE_BASE_PATH}/histories`, {
    params: { accountNo: params.accountNo, tradeMode: params.tradeMode ?? DEFAULT_TRADE_MODE },
  })
  return asArray(unwrapTradePayload<TradeHistoryRecord[]>(response.data))
}

export const createDryRunTradeHistory = async (payload: TradeDryRunHistoryRequest) => {
  const response = await http.post<TradeHistoryRecord>(`${TRADE_BASE_PATH}/histories/dry-run`, payload)
  return unwrapTradePayload<TradeHistoryRecord>(response.data)
}

export const listPerformanceHistories = async (params: { accountNo: string; from: string; to: string }) => {
  const response = await http.get<TradePerformanceRecord[]>(`${TRADE_BASE_PATH}/performance/histories`, {
    params,
  })
  return asArray(unwrapTradePayload<TradePerformanceRecord[]>(response.data))
}

export const validateOrder = async (payload: TradeOrderValidationRequest) => {
  const response = await http.post<TradeOrderValidationResult>(`${TRADE_BASE_PATH}/orders/validate`, payload)
  return unwrapTradePayload<TradeOrderValidationResult>(response.data)
}

export const getOrderableAmount = async (payload: TradeOrderableAmountRequest) => {
  const response = await http.post<TradeOrderableAmountResult>(`${TRADE_BASE_PATH}/kis/orders/orderable-amount`, {
    ...payload,
    tradeMode: payload.tradeMode ?? DEFAULT_TRADE_MODE,
  })
  return unwrapTradePayload<TradeOrderableAmountResult>(response.data)
}

export const submitCashOrder = async (payload: TradeCashOrderRequest) => {
  const path = payload.side === 'BUY' ? 'buy' : 'sell'
  const response = await http.post<TradeCashOrderResult>(`${TRADE_BASE_PATH}/orders/${path}`, {
    accountNo: payload.accountNo,
    itemCode: payload.itemCode,
    quantity: payload.quantity,
    price: payload.price,
    confirmLiveOrder: payload.confirmLiveOrder ?? false,
  })
  return unwrapTradePayload<TradeCashOrderResult>(response.data)
}

export const listKisCallLogs = async () => {
  const response = await http.get<TradeKisCallLog[]>(`${TRADE_BASE_PATH}/kis/call-logs`)
  return asArray(unwrapTradePayload<TradeKisCallLog[]>(response.data))
}

export const getRealtimeStatus = async () => {
  const response = await http.get<TradeRealtimeStatus>(`${TRADE_BASE_PATH}/realtime/status`)
  return unwrapTradePayload<TradeRealtimeStatus | null>(response.data) ?? {
    kisConnected: false,
    sessionCount: 0,
    subscriptionCount: 0,
    cachedEventCount: 0,
  }
}

export const listReconnectHistories = async (filter: TradeReconnectHistoryFilter) => {
  const response = await http.get<TradeReconnectHistory[]>(`${TRADE_BASE_PATH}/realtime/reconnect-histories`, {
    params: {
      ...(filter.success === null || filter.success === undefined ? {} : { success: filter.success }),
      ...(filter.from ? { from: filter.from } : {}),
      ...(filter.to ? { to: filter.to } : {}),
      ...(filter.limit ? { limit: filter.limit } : {}),
    },
  })
  return asArray(unwrapTradePayload<TradeReconnectHistory[]>(response.data))
}

export const publishRealtimeEvent = async (payload: TradePublishRealtimeEventRequest) => {
  const response = await http.post<Record<string, unknown>>(`${TRADE_BASE_PATH}/realtime/events`, payload)
  return unwrapTradePayload<Record<string, unknown>>(response.data)
}

export const listTradeEvents = async (params: {
  eventType?: string
  from?: string
  to?: string
}) => {
  const response = await http.get<TradeEventItem[]>(`${TRADE_BASE_PATH}/events`, {
    params,
  })
  return asArray(unwrapTradePayload<TradeEventItem[]>(response.data))
}

export const listIpoSubscriptions = async () => {
  const response = await http.get<TradeEventItem[]>(`${TRADE_BASE_PATH}/events/ipo-subscriptions`)
  return asArray(unwrapTradePayload<TradeEventItem[]>(response.data))
}

export const listParValueChanges = async () => {
  const response = await http.get<TradeEventItem[]>(`${TRADE_BASE_PATH}/events/par-value-changes`)
  return asArray(unwrapTradePayload<TradeEventItem[]>(response.data))
}

export const listCorporateActions = async () => {
  const response = await http.get<TradeEventItem[]>(`${TRADE_BASE_PATH}/events/corporate-actions`)
  return asArray(unwrapTradePayload<TradeEventItem[]>(response.data))
}

export const listRankingRows = async (rankingType: TradeRankingType, masterType?: string) => {
  const response = await http.get<TradeRankingRow[]>(`${TRADE_BASE_PATH}/analysis/rankings/${rankingType}`, {
    params: masterType ? { masterType } : {},
  })
  return asArray(unwrapTradePayload<TradeRankingRow[]>(response.data))
}

export const listSectorRows = async () => {
  const response = await http.get<Record<string, unknown>[]>(`${TRADE_BASE_PATH}/analysis/sectors`)
  return asArray(unwrapTradePayload<Record<string, unknown>[]>(response.data))
}

export const listThemeRows = async () => {
  const response = await http.get<Record<string, unknown>[]>(`${TRADE_BASE_PATH}/analysis/themes`)
  return asArray(unwrapTradePayload<Record<string, unknown>[]>(response.data))
}

export const listMasterImportHistories = async (masterType?: string) => {
  const response = await http.get<TradeMasterImportHistory[]>(`${TRADE_BASE_PATH}/masters/import-histories`, {
    params: masterType ? { masterType } : {},
  })
  return asArray(unwrapTradePayload<TradeMasterImportHistory[]>(response.data))
}

export const listMasterStatuses = async () => {
  const response = await http.get<TradeMasterStatus[]>(`${TRADE_BASE_PATH}/masters/status`)
  return asArray(unwrapTradePayload<TradeMasterStatus[]>(response.data))
}

export const listMasterTypes = async () => {
  const response = await http.get<TradeMasterTypeOption[]>(`${TRADE_BASE_PATH}/masters/types`)
  return asArray(unwrapTradePayload<TradeMasterTypeOption[]>(response.data))
}

export const importMastersByDownload = async (payload: {
  masterType: string
  sourceUrl?: string | null
  sourceVersion: string
}) => {
  const response = await http.post<TradeMasterImportHistory>(`${TRADE_BASE_PATH}/masters/download-import/async`, payload)
  return unwrapTradePayload<TradeMasterImportHistory>(response.data)
}

export const importMasterRows = async (payload: {
  masterType: string
  sourceFileName: string
  sourceVersion: string
  rows: TradeMasterImportRequestRow[]
}) => {
  const response = await http.post<TradeMasterImportHistory>(`${TRADE_BASE_PATH}/masters/import`, payload)
  return unwrapTradePayload<TradeMasterImportHistory>(response.data)
}

export const importDefaultMasters = async () => {
  const response = await http.post<TradeMasterImportHistory[] | Record<string, unknown>[]>(`${TRADE_BASE_PATH}/masters/download-import/defaults/async`)
  return asArray(unwrapTradePayload<Record<string, unknown>[]>(response.data))
}
