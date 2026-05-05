import { useSessionStore } from '@/stores/session'
import {
  createWatchlistGroup,
  createWatchlistItem,
  deleteWatchlistGroup,
  deleteWatchlistItem,
  listFrequentSearches,
  listWatchlist,
  listWatchlistGroups,
  updateWatchlistItemMetadata,
} from '@/services/trade/api'

const getCurrentUserId = () => {
  const sessionStore = useSessionStore()
  const userId = sessionStore.persisted.user?.userId
  if (!userId) {
    throw new Error('로그인 사용자 정보가 없습니다.')
  }
  return userId
}

export const getCurrentUserWatchlistGroups = async () => listWatchlistGroups(getCurrentUserId())

export const getCurrentUserWatchlist = async (groupId?: number | null) =>
  listWatchlist({
    userId: getCurrentUserId(),
    groupId,
  })

export const getCurrentUserFrequentSearches = async () => listFrequentSearches(getCurrentUserId())

export const createCurrentUserWatchlistGroup = async (groupName: string) =>
  createWatchlistGroup({
    userId: getCurrentUserId(),
    groupName,
  })

export const removeWatchlistGroup = async (groupId: number) => deleteWatchlistGroup(groupId)

export const createCurrentUserWatchlistItem = async (payload: {
  itemCode: string
  itemName: string
  groupId?: number | null
  memo?: string | null
  tags?: string[]
}) =>
  createWatchlistItem({
    userId: getCurrentUserId(),
    ...payload,
  })

export const updateWatchlistMetadata = async (
  id: number,
  payload: {
    groupId?: number | null
    memo?: string | null
    tags?: string[]
  },
) => updateWatchlistItemMetadata(id, payload)

export const removeWatchlistItem = async (id: number) => deleteWatchlistItem(id)
