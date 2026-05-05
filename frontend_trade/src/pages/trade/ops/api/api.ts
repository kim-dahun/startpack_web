import {
  listKisCallLogs,
  listReconnectHistories,
  getRealtimeStatus,
} from '@/services/trade/api'
import type { TradeReconnectHistoryFilter } from '@/types/trade'

export const fetchRealtimeStatus = async () => getRealtimeStatus()

export const fetchReconnectHistories = async (filter: TradeReconnectHistoryFilter) =>
  listReconnectHistories(filter)

export const fetchKisCallLogs = async () => listKisCallLogs()
