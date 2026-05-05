import { useSessionStore } from '@/stores/session'
import {
  createWatchlistItem,
  importDefaultMasters,
  importMastersByDownload,
  listMasterImportHistories,
  listMasterStatuses,
  listMasterTypes,
  searchItems,
} from '@/services/trade/api'
import type { TradeMasterTypeOption, TradeMode, TradeWatchlistCreateRequest } from '@/types/trade'

const getCurrentUserId = () => {
  const sessionStore = useSessionStore()
  const userId = sessionStore.persisted.user?.userId
  if (!userId) {
    throw new Error('로그인 사용자 정보가 없다.')
  }
  return userId
}

export const fetchMasterStatuses = async () => listMasterStatuses()

export const fetchMasterTypes = async (): Promise<TradeMasterTypeOption[]> => listMasterTypes()

export const fetchMasterImportHistories = async (masterType?: string) =>
  listMasterImportHistories(masterType)

export const runMasterDownloadImport = async (payload: {
  masterType: string
  sourceUrl?: string | null
  sourceVersion: string
}) => importMastersByDownload(payload)

export const runDefaultMasterImport = async () => importDefaultMasters()

export const fetchSearchCandidates = async (keyword: string, tradeMode: TradeMode) => {
  const result = await searchItems({ keyword, tradeMode })
  return Object.values(result)
}

export const addWatchlistForCurrentUser = async (payload: TradeWatchlistCreateRequest) =>
  createWatchlistItem({
    userId: getCurrentUserId(),
    ...payload,
  })
