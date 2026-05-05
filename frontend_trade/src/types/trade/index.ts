export type TradeMode = 'PAPER' | 'LIVE'
export type TradeSide = 'BUY' | 'SELL'
export type TradeRealtimeEventType =
  | 'QUOTE_TICK'
  | 'TRADE_TICK'
  | 'ORDERBOOK_SNAPSHOT'
  | 'ORDERBOOK_DELTA'
  | 'ACCOUNT_BALANCE_CHANGED'
  | 'POSITION_CHANGED'
  | 'ORDER_ACCEPTED'
  | 'ORDER_REJECTED'
  | 'ORDER_PARTIALLY_FILLED'
  | 'ORDER_FILLED'
  | 'WATCHLIST_CHANGED'
  | 'REALTIME_CONNECTION_STATUS'
export type TradeChartPeriodType = '1m' | '5m' | '15m' | '30m' | '60m' | 'DAY' | 'WEEK' | 'MONTH' | 'YEAR'
export type TradeDrawingType = 'UPPER_LINE' | 'LOWER_LINE'
export type TradeEventType = 'IPO_SUBSCRIPTION' | 'PAR_VALUE_CHANGE' | 'CORPORATE_ACTION'
export type TradeMasterType = 'KOSPI' | 'KOSDAQ' | 'KONEX' | 'ELW' | 'ETF_ETN' | 'OVERSEAS_STOCK'
export type TradeRankingType = 'volume' | 'turnover' | 'gainers' | 'losers' | 'market-cap' | 'high52' | 'low52' | 'volatility'

export interface TradeMasterTypeOption {
  masterType: string
  displayName: string
  description?: string | null
  sourceType?: string | null
  sourceUrl?: string | null
  marketCategory?: string | null
  parserImplementedYn?: boolean | null
  defaultImportTargetYn?: boolean | null
  enabled?: boolean | null
}

export interface TradeAccountSummary {
  accountNo: string
  accountName: string
  totalAssetAmount: number
  cashAmount: number
}

export interface TradeRegisteredAccount {
  id: number
  accountNo: string
  accountName: string
  productCode: string
  aliasName?: string | null
  memo?: string | null
  active: boolean
  createdAt?: string
  updatedAt?: string
}

export interface TradeAccountPosition {
  accountNo: string
  itemCode: string
  itemName: string
  quantity: number
  orderableQuantity?: number | null
  averagePrice: number
  currentPrice: number
  evaluationAmount: number
  profitLossAmount: number
  profitLossRate: number
}

export interface TradeAccountBalance {
  accountNo: string
  totalAssetAmount: number
  cashAmount: number
  orderableCashAmount: number
  totalEvaluationAmount: number
  totalProfitLossAmount: number
  totalProfitLossRate: number
  positions: TradeAccountPosition[]
}

export interface TradeDailyBalance {
  accountNo: string
  baseDate: string
  totalAssetAmount: number
  profitLossAmount: number
}

export interface TradeItemSummary {
  itemCode: string
  itemName: string
  marketCode: string
}

export interface TradeSearchCandidate extends TradeItemSummary {
  masterType?: TradeMasterType | null
  currentPrice?: number | null
  changeAmount?: number | null
  changeRate?: number | null
  accumulatedVolume?: number | null
}

export interface TradeItemDetail extends TradeItemSummary {
  currentPrice?: number | null
  changeRate?: number | null
  raw?: Record<string, unknown>
}

export interface TradeItemPrice {
  itemCode: string
  itemName: string
  currentPrice: number
  changeAmount: number
  changeRate: number
  openPrice: number
  highPrice: number
  lowPrice: number
  accumulatedVolume: number
  raw?: Record<string, unknown>
}

export interface TradeItemMetrics {
  itemCode: string
  itemName: string
  marketCode: string
  sectorName?: string | null
  per?: number | null
  pbr?: number | null
  eps?: number | null
  bps?: number | null
  salesAmount?: number | null
  operatingProfit?: number | null
  marketCap?: number | null
  high52WeekPrice?: number | null
  low52WeekPrice?: number | null
}


export interface TradeItemIndicators {
  itemCode: string
  periodType: TradeChartPeriodType
  movingAverages: {
    ma5?: number | null
    ma20?: number | null
    ma60?: number | null
    ma120?: number | null
  }
  rsi?: number | null
  macd?: number | null
  macdSignal?: number | null
  macdHistogram?: number | null
  bollingerUpper?: number | null
  bollingerMiddle?: number | null
  bollingerLower?: number | null
  atr?: number | null
  stochasticK?: number | null
  stochasticD?: number | null
  obv?: number | null
  mfi?: number | null
}

export interface TradeOrderbookLevel {
  level: number
  askPrice: number
  askQuantity: number
  bidPrice: number
  bidQuantity: number
}

export interface TradeOrderbook {
  itemCode: string
  receivedAt: string
  levels: TradeOrderbookLevel[]
  raw?: Record<string, unknown>
}

export interface TradeChartPoint {
  itemCode: string
  periodType: TradeChartPeriodType
  baseDate: string
  openPrice: number
  highPrice: number
  lowPrice: number
  closePrice: number
  volume: number
}

export interface TradeChartDrawing {
  id: number
  userId: string
  itemCode: string
  drawingType: TradeDrawingType
  startDate: string
  startPrice: number
  endDate: string
  endPrice: number
  memo?: string | null
}

export interface TradeWorkspaceSnapshot {
  itemCode: string
  userId?: string | null
  quote: TradeItemPrice | null
  orderbook: TradeOrderbook | null
  metrics: TradeItemMetrics | null
  watchlistItems: TradeWatchlistItem[]
  frequentSearches: TradeFrequentSearchItem[]
}

export interface TradeWorkspaceChartSnapshot {
  itemCode: string
  interval: TradeChartPeriodType
  candles: TradeChartPoint[]
  indicators: TradeItemIndicators | null
  drawings: TradeChartDrawing[]
}

export interface TradeWorkspaceTradingSnapshot {
  itemCode: string
  accountNo: string
  balance: TradeAccountBalance | null
  positions: TradeAccountPosition[]
  currentPosition: TradeAccountPosition | null
  orderableAmount: TradeOrderableAmountResult | null
}

export interface TradeWatchlistGroup {
  id: number
  userId: string
  groupName: string
  createdAt?: string
}

export interface TradeWatchlistItem {
  id: number
  userId: string
  itemCode: string
  itemName: string
  groupId?: number | null
  memo?: string | null
  tags?: string[]
  createdAt?: string
}

export interface TradeFrequentSearchItem {
  id: number
  userId: string
  itemCode: string
  itemName: string
  marketCode: string
  searchCount: number
  lastSearchedAt: string
}

export interface TradeHistoryRecord {
  id: number
  accountNo: string
  itemCode: string
  itemName: string
  side: TradeSide
  quantity: number
  price: number
  amount: number
  idempotencyKey?: string | null
  tradedAt: string
}

export interface TradePerformanceRecord {
  accountNo: string
  baseDate: string
  totalAssetAmount: number
  profitLossAmount: number
}

export interface TradeOrderValidationRequest {
  accountNo: string
  itemCode: string
  side: TradeSide
  quantity: number
  price: number
  availableCashAmount?: number | null
  availableQuantity?: number | null
}

export interface TradeOrderValidationResult {
  accountNo: string
  itemCode: string
  side: TradeSide
  requestedQuantity: number
  price: number
  requiredAmount: number
  availableCashAmount?: number | null
  availableQuantity?: number | null
  allowed: boolean
  failureReason?: string | null
}

export interface TradeOrderableAmountRequest {
  accountNo: string
  itemCode: string
  price: number
  tradeMode?: TradeMode
}

export interface TradeOrderableAmountResult {
  accountNo: string
  itemCode: string
  price: number
  orderableCashAmount: number
  orderableQuantity: number
  tradeMode: TradeMode
  raw?: Record<string, unknown>
}

export interface TradeCashOrderRequest {
  accountNo: string
  itemCode: string
  side: TradeSide
  quantity: number
  price: number
  tradeMode?: TradeMode
  confirmLiveOrder?: boolean
}

export interface TradeCashOrderResult {
  accountNo: string
  itemCode: string
  side: TradeSide
  quantity: number
  price: number
  tradeMode: TradeMode
  orderNo: string
  branchNo: string
  responseCode: string
  message: string
  raw?: Record<string, unknown>
}

export interface TradeDryRunHistoryRequest {
  accountNo: string
  itemCode: string
  itemName: string
  side: TradeSide
  quantity: number
  price: number
  idempotencyKey: string
}

export interface TradeKisCallLog {
  id: number
  endpoint: string
  method: string
  statusCode: number
  elapsedMillis: number
  errorCode?: string | null
  errorMessage?: string | null
  calledAt: string
}

export interface TradeRealtimeStatus {
  kisConnected: boolean
  sessionCount: number
  subscriptionCount: number
  cachedEventCount: number
}

export interface TradeReconnectHistory {
  id: number
  attemptedAt: string
  success: boolean
  subscriptionCount: number
  failureReason?: string | null
}

export interface TradePublishRealtimeEventRequest {
  type: TradeRealtimeEventType
  itemCode: string
  payload: Record<string, unknown>
}

export interface TradeWebSocketCommand {
  action: 'subscribe' | 'unsubscribe' | 'ping'
  type?: TradeRealtimeEventType
  itemCode?: string
  accountNo?: string
}

export interface TradeWebSocketEvent {
  eventType: TradeRealtimeEventType
  topic?: string | null
  itemCode?: string | null
  accountNo?: string | null
  occurredAt: string
  sequenceNo?: number | null
  payload: Record<string, unknown>
}

export interface TradeReconnectHistoryFilter {
  success?: boolean | null
  from?: string
  to?: string
  limit?: number
}

export interface TradeWatchlistCreateRequest {
  itemCode: string
  itemName: string
  groupId?: number | null
  memo?: string | null
  tags?: string[]
}

export interface TradeWatchlistMetadataUpdateRequest {
  groupId?: number | null
  memo?: string | null
  tags?: string[]
}

export interface TradeWatchlistGroupCreateRequest {
  groupName: string
}

export interface TradeEventItem {
  id: number
  eventType: TradeEventType
  itemCode: string
  title: string
  eventDate: string
  description?: string | null
}

export interface TradeRankingRow {
  rank: number
  itemCode: string
  itemName: string
  marketCode: string
  countryCode?: string | null
  sectorName?: string | null
  metricValue: number
}

export interface TradeMasterImportHistory {
  id: number
  historyId?: number
  masterType: TradeMasterType
  sourceFileName: string
  sourceVersion: string
  importedCount: number
  startedAt: string
  finishedAt: string
  success: boolean
  failureReason?: string | null
}

export interface TradeMasterStatus {
  masterType: TradeMasterType
  itemCount: number
  lastImportedAt?: string | null
  lastSourceFileName?: string | null
  lastSourceVersion?: string | null
  lastImportSuccess?: boolean | null
}

export interface TradeMasterImportRequestRow {
  itemCode: string
  itemName: string
  marketCode: string
  countryCode: string
  sectorName?: string | null
  per?: number | null
  pbr?: number | null
  eps?: number | null
  bps?: number | null
  salesAmount?: number | null
  operatingProfit?: number | null
  marketCap?: number | null
  high52WeekPrice?: number | null
  low52WeekPrice?: number | null
  raw?: Record<string, unknown>
}
