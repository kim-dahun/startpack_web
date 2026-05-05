import {
  listCorporateActions,
  listIpoSubscriptions,
  listParValueChanges,
  listRankingRows,
  listSectorRows,
  listThemeRows,
  listTradeEvents,
} from '@/services/trade/api'
import type { TradeRankingType } from '@/types/trade'

export const fetchTradeEvents = async (params: { eventType?: string; from?: string; to?: string }) =>
  listTradeEvents(params)

export const fetchIpoSubscriptions = async () => listIpoSubscriptions()

export const fetchParValueChanges = async () => listParValueChanges()

export const fetchCorporateActions = async () => listCorporateActions()

export const fetchRankingRows = async (rankingType: TradeRankingType, masterType?: string) =>
  listRankingRows(rankingType, masterType)

export const fetchSectorRows = async () => listSectorRows()

export const fetchThemeRows = async () => listThemeRows()
