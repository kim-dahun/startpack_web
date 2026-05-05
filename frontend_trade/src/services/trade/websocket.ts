import type { TradeRealtimeEventType, TradeWebSocketCommand, TradeWebSocketEvent } from '@/types/trade'

export const getTradeRealtimeWebSocketUrl = () => {
  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
  return `${protocol}//${window.location.host}/ws/trade/realtime`
}

export const createTradeRealtimeSocket = () => new WebSocket(getTradeRealtimeWebSocketUrl())

export const serializeTradeRealtimeCommand = (command: TradeWebSocketCommand) => JSON.stringify(command)

export const buildTradeSubscriptionKey = (params: {
  type: TradeRealtimeEventType
  itemCode?: string | null
  accountNo?: string | null
}) => `${params.type}:${params.itemCode ?? ''}:${params.accountNo ?? ''}`

export const normalizeTradeRealtimeEvent = (payload: unknown): TradeWebSocketEvent => {
  const source = (payload && typeof payload === 'object' ? payload : {}) as Record<string, unknown>
  const legacyType = source.type

  return {
    eventType: String(source.eventType ?? legacyType ?? 'QUOTE_TICK') as TradeRealtimeEventType,
    topic: source.topic ? String(source.topic) : null,
    itemCode: source.itemCode ? String(source.itemCode) : null,
    accountNo: source.accountNo ? String(source.accountNo) : null,
    occurredAt: String(source.occurredAt ?? new Date().toISOString()),
    sequenceNo: source.sequenceNo == null ? null : Number(source.sequenceNo),
    payload: (source.payload && typeof source.payload === 'object' ? source.payload : {}) as Record<string, unknown>,
  }
}
