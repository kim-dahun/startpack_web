import {
  createRegisteredAccount,
  deleteRegisteredAccount,
  getAccountBalances,
  listAccountPositions,
  listAccounts,
  listDailyBalances,
  listPerformanceHistories,
  listRegisteredAccounts,
  listTradeHistories,
  updateRegisteredAccount,
} from '@/services/trade/api'
import type { TradeMode } from '@/types/trade'

export const fetchAccounts = async (tradeMode: TradeMode, accountNo?: string) =>
  listAccounts({ tradeMode, accountNo })

export const fetchRegisteredAccounts = async () => listRegisteredAccounts()

export const saveRegisteredAccount = async (
  payload: {
    id?: number | null
    accountNo: string
    accountName: string
    productCode: string
    aliasName?: string | null
    memo?: string | null
    active: boolean
  },
) => {
  if (payload.id) {
    return updateRegisteredAccount(payload.id, payload)
  }
  return createRegisteredAccount(payload)
}

export const removeRegisteredAccount = async (id: number) => deleteRegisteredAccount(id)

export const fetchAccountBalances = async (accountNo: string, tradeMode: TradeMode) =>
  getAccountBalances({ accountNo, tradeMode })

export const fetchAccountPositions = async (accountNo: string, tradeMode: TradeMode) =>
  listAccountPositions({ accountNo, tradeMode })

export const fetchDailyBalances = async (accountNo: string, baseDate: string, tradeMode: TradeMode) =>
  listDailyBalances({ accountNo, baseDate, tradeMode })

export const fetchTradeHistories = async (accountNo: string, tradeMode: TradeMode) =>
  listTradeHistories({ accountNo, tradeMode })

export const fetchPerformanceHistories = async (accountNo: string, from: string, to: string) =>
  listPerformanceHistories({ accountNo, from, to })
