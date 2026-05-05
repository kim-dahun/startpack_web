import { defineStore } from 'pinia'

import {
  buildTradeSubscriptionKey,
  createTradeRealtimeSocket,
  normalizeTradeRealtimeEvent,
  serializeTradeRealtimeCommand,
} from '@/services/trade/websocket'
import { useTradeRealtimeStore } from '@/stores/trade/realtime'
import type {
  TradeRealtimeEventType,
  TradeWebSocketCommand,
  TradeWebSocketEvent,
} from '@/types/trade'

type SocketConnectionState = 'DISCONNECTED' | 'CONNECTING' | 'CONNECTED' | 'ERROR'
const ITEM_EVENT_TYPES: TradeRealtimeEventType[] = ['QUOTE_TICK', 'TRADE_TICK', 'ORDERBOOK_SNAPSHOT', 'ORDERBOOK_DELTA']
const ACCOUNT_EVENT_TYPES: TradeRealtimeEventType[] = ['ACCOUNT_BALANCE_CHANGED', 'POSITION_CHANGED', 'ORDER_ACCEPTED', 'ORDER_REJECTED', 'ORDER_PARTIALLY_FILLED', 'ORDER_FILLED']
const STATUS_EVENT_TYPES: TradeRealtimeEventType[] = ['REALTIME_CONNECTION_STATUS']

export const useTradeSocketStore = defineStore('trade-socket', {
  state: () => ({
    socket: null as WebSocket | null,
    connectionState: 'DISCONNECTED' as SocketConnectionState,
    events: [] as TradeWebSocketEvent[],
    subscriptions: [] as string[],
    lastError: '' as string,
    activeItemCode: '' as string,
    activeAccountNo: '' as string,
  }),
  actions: {
    connect() {
      if (this.socket && (this.socket.readyState === WebSocket.CONNECTING || this.socket.readyState === WebSocket.OPEN)) {
        return
      }

      this.connectionState = 'CONNECTING'
      this.lastError = ''

      const socket = createTradeRealtimeSocket()
      this.socket = socket

      socket.onopen = () => {
        this.connectionState = 'CONNECTED'
        this.replaySubscriptions()
      }

      socket.onmessage = (event) => {
        try {
          const payload = normalizeTradeRealtimeEvent(JSON.parse(String(event.data)))
          this.events = [payload, ...this.events].slice(0, 30)
          useTradeRealtimeStore().applyDelta(payload)
        } catch (error) {
          this.lastError = error instanceof Error ? error.message : 'websocket event parse failed'
        }
      }

      socket.onerror = () => {
        this.connectionState = 'ERROR'
        this.lastError = 'websocket connection error'
      }

      socket.onclose = () => {
        this.connectionState = 'DISCONNECTED'
        this.socket = null
      }
    },
    disconnect() {
      this.socket?.close()
      this.socket = null
      this.connectionState = 'DISCONNECTED'
    },
    sendCommand(command: TradeWebSocketCommand) {
      if (!this.socket || this.socket.readyState !== WebSocket.OPEN) {
        throw new Error('websocket is not connected')
      }

      this.socket.send(serializeTradeRealtimeCommand(command))
    },
    sendOrQueue(command: TradeWebSocketCommand) {
      if (!this.socket || this.socket.readyState !== WebSocket.OPEN) {
        return
      }

      this.sendCommand(command)
    },
    subscribe(type: TradeRealtimeEventType, itemCode?: string, accountNo?: string) {
      this.sendOrQueue({
        action: 'subscribe',
        type,
        itemCode,
        accountNo,
      })
      const key = buildTradeSubscriptionKey({ type, itemCode, accountNo })
      if (!this.subscriptions.includes(key)) {
        this.subscriptions = [key, ...this.subscriptions]
      }
    },
    unsubscribe(type: TradeRealtimeEventType, itemCode?: string, accountNo?: string) {
      this.sendOrQueue({
        action: 'unsubscribe',
        type,
        itemCode,
        accountNo,
      })
      const key = buildTradeSubscriptionKey({ type, itemCode, accountNo })
      this.subscriptions = this.subscriptions.filter((value) => value !== key)
    },
    replaySubscriptions() {
      this.subscriptions.forEach((key) => {
        const [type, itemCode, accountNo] = key.split(':')
        this.sendOrQueue({
          action: 'subscribe',
          type: type as TradeRealtimeEventType,
          itemCode: itemCode || undefined,
          accountNo: accountNo || undefined,
        })
      })
    },
    syncWorkspaceSubscriptions(params: { itemCode?: string | null; accountNo?: string | null }) {
      const nextItemCode = params.itemCode?.trim() ?? ''
      const nextAccountNo = params.accountNo?.trim() ?? ''

      if (this.activeItemCode && this.activeItemCode !== nextItemCode) {
        ITEM_EVENT_TYPES.forEach((type) => this.unsubscribe(type, this.activeItemCode))
      }

      if (this.activeAccountNo && this.activeAccountNo !== nextAccountNo) {
        ACCOUNT_EVENT_TYPES.forEach((type) => this.unsubscribe(type, undefined, this.activeAccountNo))
      }

      if (nextItemCode && this.activeItemCode !== nextItemCode) {
        ITEM_EVENT_TYPES.forEach((type) => this.subscribe(type, nextItemCode))
      }

      if (nextAccountNo && this.activeAccountNo !== nextAccountNo) {
        ACCOUNT_EVENT_TYPES.forEach((type) => this.subscribe(type, undefined, nextAccountNo))
      }

      STATUS_EVENT_TYPES.forEach((type) => {
        const key = buildTradeSubscriptionKey({ type })
        if (!this.subscriptions.includes(key)) {
          this.subscribe(type)
        }
      })

      this.activeItemCode = nextItemCode
      this.activeAccountNo = nextAccountNo
    },
    clearWorkspaceSubscriptions() {
      if (this.activeItemCode) {
        ITEM_EVENT_TYPES.forEach((type) => this.unsubscribe(type, this.activeItemCode))
      }

      if (this.activeAccountNo) {
        ACCOUNT_EVENT_TYPES.forEach((type) => this.unsubscribe(type, undefined, this.activeAccountNo))
      }

      this.activeItemCode = ''
      this.activeAccountNo = ''
    },
    ping() {
      this.sendCommand({ action: 'ping' })
    },
    clearEvents() {
      this.events = []
    },
  },
})
