import { useSessionStore } from '@/stores/session'
import {
  createWatchlistItem,
  createItemDrawing,
  getItemDetail,
  getItemIndicators,
  getItemMetrics,
  getItemQuote,
  getItemSummary,
  getItemPrice,
  getOrderableAmount,
  listAccounts,
  listItemChart,
  listItemQuotes,
  listItemDrawings,
  listItemOrderbooks,
  listItems,
  searchItems,
  recordFrequentSearch,
  submitCashOrder,
  updateItemDrawing,
  deleteItemDrawing,
  validateOrder,
} from '@/services/trade/api'
import type {
  TradeCashOrderRequest,
  TradeChartDrawing,
  TradeChartPeriodType,
  TradeMode,
  TradeOrderValidationRequest,
  TradeOrderableAmountRequest,
  TradeWatchlistCreateRequest,
} from '@/types/trade'

const getCurrentUserId = () => {
  const sessionStore = useSessionStore()
  const userId = sessionStore.persisted.user?.userId
  if (!userId) {
    throw new Error('로그인 사용자 정보가 없습니다.')
  }
  return userId
}

export const fetchAccounts = async (tradeMode: TradeMode) => listAccounts({ tradeMode })

export const fetchItems = async (keyword: string, tradeMode: TradeMode) =>
  listItems({ keyword, tradeMode })

export const fetchAutocompleteItems = async (keyword: string, tradeMode: TradeMode) =>
  searchItems({ keyword, tradeMode })

export const fetchItemDetail = async (itemCode: string, tradeMode: TradeMode) =>
  getItemDetail(itemCode, tradeMode)

export const fetchItemQuote = async (itemCode: string, tradeMode: TradeMode) =>
  getItemQuote(itemCode, tradeMode)

export const fetchItemPrice = async (itemCode: string, tradeMode: TradeMode) =>
  getItemPrice(itemCode, tradeMode)

export const fetchItemQuotes = async (itemCodes: string[], tradeMode: TradeMode) =>
  listItemQuotes({ itemCodes, tradeMode })

export const fetchOrderbooks = async (itemCode: string, tradeMode: TradeMode) =>
  listItemOrderbooks({ itemCodes: [itemCode], tradeMode })

export const fetchItemChart = async (
  itemCode: string,
  periodType: TradeChartPeriodType,
  from: string,
  to: string,
  tradeMode: TradeMode,
) => listItemChart({ itemCode, periodType, from, to, tradeMode })

export const fetchItemIndicators = async (
  itemCode: string,
  periodType: TradeChartPeriodType,
  from: string,
  to: string,
  tradeMode: TradeMode,
) => getItemIndicators({ itemCode, periodType, from, to, tradeMode })

export const fetchItemSummary = async (itemCode: string, tradeMode: TradeMode) =>
  getItemSummary(itemCode, tradeMode)

export const fetchItemMetrics = async (itemCode: string, tradeMode: TradeMode) =>
  getItemMetrics(itemCode, tradeMode)

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

export const removeCurrentUserItemDrawing = async (itemCode: string, drawingId: number) =>
  deleteItemDrawing(itemCode, drawingId, getCurrentUserId())

export const addWatchlistForCurrentUser = async (payload: TradeWatchlistCreateRequest) =>
  createWatchlistItem({
    userId: getCurrentUserId(),
    ...payload,
  })

export const createFrequentSearchForCurrentUser = async (payload: {
  itemCode: string
  itemName: string
  marketCode: string
}) => recordFrequentSearch({
  userId: getCurrentUserId(),
  ...payload,
})

export const runValidateOrder = async (payload: TradeOrderValidationRequest) => validateOrder(payload)

export const fetchOrderableAmount = async (payload: TradeOrderableAmountRequest) => getOrderableAmount(payload)

export const executeCashOrder = async (payload: TradeCashOrderRequest) => submitCashOrder(payload)
