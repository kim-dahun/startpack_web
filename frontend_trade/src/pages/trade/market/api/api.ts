import { useSessionStore } from '@/stores/session'
import {
  createItemDrawing,
  createWatchlistItem,
  deleteItemDrawing,
  getOrderableAmount,
  getRealtimeStatus,
  listCorporateActions,
  listRankingRows,
  getWorkspaceChartSnapshot,
  getWorkspaceSnapshot,
  getWorkspaceTradingSnapshot,
  listIpoSubscriptions,
  listParValueChanges,
  listAccounts,
  listItemChart,
  listItemDrawings,
  submitCashOrder,
  listTradeEvents,
  updateItemDrawing,
  validateOrder,
} from '@/services/trade/api'
import type {
  TradeCashOrderRequest,
  TradeChartDrawing,
  TradeChartPeriodType,
  TradeChartPoint,
  TradeMode,
  TradeRankingRow,
  TradeRankingType,
  TradeEventItem,
  TradeWorkspaceChartSnapshot,
  TradeWorkspaceSnapshot,
  TradeWorkspaceTradingSnapshot,
  TradeOrderValidationRequest,
  TradeOrderableAmountRequest,
  TradeWatchlistCreateRequest,
} from '@/types/trade'

const getCurrentUserId = () => {
  const sessionStore = useSessionStore()
  const userId = sessionStore.persisted.user?.userId
  if (!userId) {
    throw new Error('로그인 사용자 정보가 없다.')
  }
  return userId
}

export const fetchAccounts = async (tradeMode: TradeMode) => listAccounts({ tradeMode })

export const fetchRealtimeStatus = async () => getRealtimeStatus()

export const fetchWorkspaceSnapshot = async (itemCode: string, tradeMode: TradeMode): Promise<TradeWorkspaceSnapshot> =>
  getWorkspaceSnapshot({
    itemCode,
    userId: getCurrentUserId(),
    tradeMode,
  })

export const fetchWorkspaceChartSnapshot = async (
  itemCode: string,
  interval: TradeChartPeriodType,
  from: string,
  to: string,
  tradeMode: TradeMode,
): Promise<TradeWorkspaceChartSnapshot> =>
  getWorkspaceChartSnapshot({
    itemCode,
    interval,
    from,
    to,
    userId: getCurrentUserId(),
    tradeMode,
  })

export const fetchWorkspaceTradingSnapshot = async (
  itemCode: string,
  accountNo: string,
  price: number | null | undefined,
  tradeMode: TradeMode,
): Promise<TradeWorkspaceTradingSnapshot> =>
  getWorkspaceTradingSnapshot({
    itemCode,
    accountNo,
    price: price ?? undefined,
    tradeMode,
  })

export const fetchItemChart = async (
  itemCode: string,
  periodType: TradeChartPeriodType,
  from: string,
  to: string,
  tradeMode: TradeMode,
) => listItemChart({ itemCode, periodType, from, to, tradeMode }) as Promise<TradeChartPoint[]>

export const fetchItemIndicators = async (
  itemCode: string,
  periodType: TradeChartPeriodType,
  from: string,
  to: string,
  tradeMode: TradeMode,
) => getWorkspaceChartSnapshot({
  itemCode,
  interval: periodType,
  from,
  to,
  userId: getCurrentUserId(),
  tradeMode,
}).then((snapshot) => snapshot.indicators)

export const fetchItemDrawings = async (itemCode: string) =>
  listItemDrawings(itemCode, getCurrentUserId())

export const createCurrentUserItemDrawing = async (
  itemCode: string,
  payload: Omit<TradeChartDrawing, 'id' | 'userId' | 'itemCode'>,
) => createItemDrawing(itemCode, {
  userId: getCurrentUserId(),
  ...payload,
})

export const updateCurrentUserItemDrawing = async (
  itemCode: string,
  drawingId: number,
  payload: Omit<TradeChartDrawing, 'id' | 'userId' | 'itemCode'>,
) => updateItemDrawing(itemCode, drawingId, {
  userId: getCurrentUserId(),
  ...payload,
})

export const deleteCurrentUserItemDrawing = async (itemCode: string, drawingId: number) =>
  deleteItemDrawing(itemCode, drawingId, getCurrentUserId())

export const addWatchlistForCurrentUser = async (payload: TradeWatchlistCreateRequest) =>
  createWatchlistItem({
    userId: getCurrentUserId(),
    ...payload,
  })

export const fetchOrderableAmount = async (payload: TradeOrderableAmountRequest) => getOrderableAmount(payload)

export const runValidateOrder = async (payload: TradeOrderValidationRequest) => validateOrder(payload)

export const executeCashOrder = async (payload: TradeCashOrderRequest) => submitCashOrder(payload)

export const fetchTradeEvents = async (): Promise<TradeEventItem[]> => listTradeEvents({})
export const fetchIpoSubscriptions = async (): Promise<TradeEventItem[]> => listIpoSubscriptions()
export const fetchParValueChanges = async (): Promise<TradeEventItem[]> => listParValueChanges()
export const fetchCorporateActions = async (): Promise<TradeEventItem[]> => listCorporateActions()
export const fetchRankingRows = async (rankingType: TradeRankingType, masterType?: string): Promise<TradeRankingRow[]> =>
  listRankingRows(rankingType, masterType)
