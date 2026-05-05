import { defineStore } from 'pinia'

import type {
  TradeAccountBalance,
  TradeAccountPosition,
  TradeItemMetrics,
  TradeItemPrice,
  TradeOrderableAmountResult,
  TradeOrderbook,
  TradeRealtimeEventType,
  TradeWebSocketEvent,
  TradeWorkspaceChartSnapshot,
  TradeWorkspaceSnapshot,
  TradeWorkspaceTradingSnapshot,
} from '@/types/trade'

type TradeWorkspaceDeltaTarget = {
  quote: TradeItemPrice | null
  orderbook: TradeOrderbook | null
  metrics: TradeItemMetrics | null
  balance: TradeAccountBalance | null
  positions: TradeAccountPosition[]
  currentPosition: TradeAccountPosition | null
  orderableAmount: TradeOrderableAmountResult | null
}

const toNumber = (value: unknown, fallback = 0) => {
  const next = Number(value)
  return Number.isFinite(next) ? next : fallback
}

const mergeQuote = (current: TradeItemPrice | null, payload: Record<string, unknown>, itemCode: string) => ({
  itemCode,
  itemName: String(payload.itemName ?? current?.itemName ?? ''),
  currentPrice: toNumber(payload.currentPrice ?? current?.currentPrice),
  changeAmount: toNumber(payload.changeAmount ?? current?.changeAmount),
  changeRate: toNumber(payload.changeRate ?? current?.changeRate),
  openPrice: toNumber(payload.openPrice ?? current?.openPrice),
  highPrice: toNumber(payload.highPrice ?? current?.highPrice),
  lowPrice: toNumber(payload.lowPrice ?? current?.lowPrice),
  accumulatedVolume: toNumber(payload.accumulatedVolume ?? payload.volume ?? current?.accumulatedVolume),
  raw: {
    ...(current?.raw ?? {}),
    ...payload,
  },
})

const mergeOrderbook = (current: TradeOrderbook | null, payload: Record<string, unknown>, itemCode: string): TradeOrderbook => {
  if (Array.isArray(payload.levels)) {
    return {
      itemCode,
      receivedAt: String(payload.receivedAt ?? payload.occurredAt ?? current?.receivedAt ?? new Date().toISOString()),
      levels: payload.levels as TradeOrderbook['levels'],
      raw: {
        ...(current?.raw ?? {}),
        ...payload,
      },
    }
  }

  if (!current) {
    return {
      itemCode,
      receivedAt: String(payload.receivedAt ?? payload.occurredAt ?? new Date().toISOString()),
      levels: [],
      raw: { ...payload },
    }
  }

  return {
    ...current,
    receivedAt: String(payload.receivedAt ?? payload.occurredAt ?? current.receivedAt),
    raw: {
      ...(current.raw ?? {}),
      ...payload,
    },
  }
}

const normalizePositions = (value: unknown) => (Array.isArray(value) ? value as TradeAccountPosition[] : [])

const sameItem = (row: TradeAccountPosition, itemCode: string) => row.itemCode === itemCode

export const useTradeRealtimeStore = defineStore('trade-realtime', {
  state: () => ({
    workspaceSnapshot: null as TradeWorkspaceSnapshot | null,
    chartSnapshot: null as TradeWorkspaceChartSnapshot | null,
    tradingSnapshot: null as TradeWorkspaceTradingSnapshot | null,
    deltaEvents: [] as TradeWebSocketEvent[],
    orderEvents: [] as TradeWebSocketEvent[],
    lastQuoteEventAt: '' as string,
    lastOrderEventAt: '' as string,
  }),
  getters: {
    quote: (state) => state.workspaceSnapshot?.quote ?? null,
    orderbook: (state) => state.workspaceSnapshot?.orderbook ?? null,
    metrics: (state) => state.workspaceSnapshot?.metrics ?? null,
    watchlistItems: (state) => state.workspaceSnapshot?.watchlistItems ?? [],
    frequentSearches: (state) => state.workspaceSnapshot?.frequentSearches ?? [],
    chartRows: (state) => state.chartSnapshot?.candles ?? [],
    indicators: (state) => state.chartSnapshot?.indicators ?? null,
    drawings: (state) => state.chartSnapshot?.drawings ?? [],
    balance: (state) => state.tradingSnapshot?.balance ?? null,
    positions: (state) => state.tradingSnapshot?.positions ?? [],
    currentPosition: (state) => state.tradingSnapshot?.currentPosition ?? null,
    orderableAmount: (state) => state.tradingSnapshot?.orderableAmount ?? null,
  },
  actions: {
    setWorkspaceSnapshot(snapshot: TradeWorkspaceSnapshot | null) {
      this.workspaceSnapshot = snapshot
    },
    setChartSnapshot(snapshot: TradeWorkspaceChartSnapshot | null) {
      this.chartSnapshot = snapshot
    },
    setTradingSnapshot(snapshot: TradeWorkspaceTradingSnapshot | null) {
      this.tradingSnapshot = snapshot
    },
    resetWorkspace() {
      this.workspaceSnapshot = null
      this.chartSnapshot = null
      this.tradingSnapshot = null
      this.deltaEvents = []
      this.orderEvents = []
      this.lastQuoteEventAt = ''
      this.lastOrderEventAt = ''
    },
    updateDrawingRows(rows: TradeWorkspaceChartSnapshot['drawings']) {
      if (!this.chartSnapshot) {
        return
      }

      this.chartSnapshot = {
        ...this.chartSnapshot,
        drawings: rows,
      }
    },
    patchTradingPrice(price: number) {
      if (!this.tradingSnapshot?.orderableAmount) {
        return
      }

      this.tradingSnapshot = {
        ...this.tradingSnapshot,
        orderableAmount: {
          ...this.tradingSnapshot.orderableAmount,
          price,
        },
      }
    },
    applyDelta(event: TradeWebSocketEvent) {
      this.deltaEvents = [event, ...this.deltaEvents].slice(0, 40)

      const itemCode = event.itemCode ?? this.workspaceSnapshot?.itemCode ?? this.tradingSnapshot?.itemCode ?? ''
      const payload = event.payload ?? {}
      const tradingTarget: TradeWorkspaceDeltaTarget = {
        quote: this.workspaceSnapshot?.quote ?? null,
        orderbook: this.workspaceSnapshot?.orderbook ?? null,
        metrics: this.workspaceSnapshot?.metrics ?? null,
        balance: this.tradingSnapshot?.balance ?? null,
        positions: this.tradingSnapshot?.positions ?? [],
        currentPosition: this.tradingSnapshot?.currentPosition ?? null,
        orderableAmount: this.tradingSnapshot?.orderableAmount ?? null,
      }

      switch (event.eventType as TradeRealtimeEventType) {
        case 'QUOTE_TICK':
        case 'TRADE_TICK': {
          if (!this.workspaceSnapshot || !itemCode) {
            return
          }

          this.workspaceSnapshot = {
            ...this.workspaceSnapshot,
            quote: mergeQuote(tradingTarget.quote, payload, itemCode),
          }
          this.lastQuoteEventAt = event.occurredAt
          return
        }
        case 'ORDERBOOK_SNAPSHOT':
        case 'ORDERBOOK_DELTA': {
          if (!this.workspaceSnapshot || !itemCode) {
            return
          }

          this.workspaceSnapshot = {
            ...this.workspaceSnapshot,
            orderbook: mergeOrderbook(tradingTarget.orderbook, payload, itemCode),
          }
          return
        }
        case 'ACCOUNT_BALANCE_CHANGED': {
          if (!this.tradingSnapshot?.accountNo) {
            return
          }

          this.tradingSnapshot = {
            ...this.tradingSnapshot,
            balance: {
              accountNo: String(payload.accountNo ?? this.tradingSnapshot.accountNo),
              totalAssetAmount: toNumber(payload.totalAssetAmount ?? tradingTarget.balance?.totalAssetAmount),
              cashAmount: toNumber(payload.cashAmount ?? tradingTarget.balance?.cashAmount),
              orderableCashAmount: toNumber(payload.orderableCashAmount ?? tradingTarget.balance?.orderableCashAmount),
              totalEvaluationAmount: toNumber(payload.totalEvaluationAmount ?? tradingTarget.balance?.totalEvaluationAmount),
              totalProfitLossAmount: toNumber(payload.totalProfitLossAmount ?? tradingTarget.balance?.totalProfitLossAmount),
              totalProfitLossRate: toNumber(payload.totalProfitLossRate ?? tradingTarget.balance?.totalProfitLossRate),
              positions: normalizePositions(payload.positions ?? tradingTarget.balance?.positions),
            },
          }
          return
        }
        case 'POSITION_CHANGED': {
          if (!this.tradingSnapshot) {
            return
          }

          const nextPositions = normalizePositions(payload.positions)

          if (nextPositions.length) {
            this.tradingSnapshot = {
              ...this.tradingSnapshot,
              positions: nextPositions,
              currentPosition: nextPositions.find((row) => sameItem(row, this.tradingSnapshot?.itemCode ?? '')) ?? null,
            }
            return
          }

          const singlePosition = payload as unknown as TradeAccountPosition
          if (singlePosition?.itemCode) {
            const merged = [
              ...this.tradingSnapshot.positions.filter((row) => !sameItem(row, singlePosition.itemCode)),
              singlePosition,
            ]
            this.tradingSnapshot = {
              ...this.tradingSnapshot,
              positions: merged,
              currentPosition: merged.find((row) => sameItem(row, this.tradingSnapshot?.itemCode ?? '')) ?? null,
            }
          }
          return
        }
        case 'ORDER_ACCEPTED':
        case 'ORDER_REJECTED':
        case 'ORDER_PARTIALLY_FILLED':
        case 'ORDER_FILLED': {
          this.orderEvents = [event, ...this.orderEvents].slice(0, 20)
          this.lastOrderEventAt = event.occurredAt
          return
        }
        default:
          return
      }
    },
  },
})
