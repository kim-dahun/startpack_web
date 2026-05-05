import { computed, ref, watch } from 'vue'
import { defineStore } from 'pinia'
import { useStorage } from '@vueuse/core'

import { login as loginApi, logout as logoutApi } from '@/api/modules/auth'
import {
  GROUPWARE_SERVICE_ID,
  hydrateGroupwareSession,
} from '@/services/groupware/menuSeed'
import type { LoginRequest, MenuPermission, PermissionFlags, SessionState } from '@/types/app'

const MANAGEABLE_SERVICE_IDS = ['TRADE', 'ERP', 'GROUPWARE'] as const
const getFavoritesStorageKey = (userId: string) => `frontend_groupware.favorites.${userId}`

const defaultPermissions: PermissionFlags = {
  permitRead: false,
  permitWrite: false,
  permitDelete: false,
  permitExcel: false,
}

export const useSessionStore = defineStore('session', () => {
  const persisted = useStorage<SessionState>('frontend_groupware.session', {
    user: null,
    token: null,
    menus: [],
    menuPermissions: [],
    serviceId: null,
    serviceAccesses: [],
    groups: [],
    currentMenuId: null,
    currentMenuUrl: null,
  })
  const favoriteMenuIds = ref<string[]>([])

  const isAuthenticated = computed(() => {
    return Boolean(
      persisted.value.user
      && persisted.value.token
      && persisted.value.serviceId,
    )
  })
  const hasGroupwareAccess = computed(() =>
    (persisted.value.serviceAccesses ?? []).includes(GROUPWARE_SERVICE_ID),
  )
  const adminServiceIds = computed(() =>
    [...new Set(
      (persisted.value.groups ?? [])
        .filter((group) => group.groupId === 'ADMIN' && MANAGEABLE_SERVICE_IDS.includes(group.serviceId as (typeof MANAGEABLE_SERVICE_IDS)[number]))
        .map((group) => group.serviceId),
    )],
  )
  const hasServiceAdmin = (serviceId: string | null | undefined) =>
    Boolean(serviceId && adminServiceIds.value.includes(serviceId))
  const usesHttpOnlyCookie = computed(() => persisted.value.token?.tokenDeliveryMethod === 'HTTP_ONLY_COOKIE')

  const permissionsMap = computed(() => {
    return persisted.value.menuPermissions.reduce<Record<string, MenuPermission>>((accumulator, permission) => {
      accumulator[permission.menuId] = permission
      return accumulator
    }, {})
  })

  const getPermissions = (menuId: string | undefined | null): PermissionFlags => {
    if (!menuId) {
      return defaultPermissions
    }

    return permissionsMap.value[menuId] ?? defaultPermissions
  }

  const canAccess = (menuId: string | undefined | null) => getPermissions(menuId).permitRead

  const loadFavoriteMenuIds = (userId: string | null | undefined) => {
    if (!userId) {
      favoriteMenuIds.value = []
      return
    }

    try {
      const rawValue = window.localStorage.getItem(getFavoritesStorageKey(userId))
      favoriteMenuIds.value = rawValue ? JSON.parse(rawValue) as string[] : []
    } catch {
      favoriteMenuIds.value = []
    }
  }

  const persistFavoriteMenuIds = () => {
    const userId = persisted.value.user?.userId

    if (!userId) {
      return
    }

    window.localStorage.setItem(
      getFavoritesStorageKey(userId),
      JSON.stringify([...new Set(favoriteMenuIds.value)]),
    )
  }

  const addFavoriteMenu = (menuId: string) => {
    if (favoriteMenuIds.value.includes(menuId)) {
      return false
    }

    favoriteMenuIds.value = [...favoriteMenuIds.value, menuId]
    persistFavoriteMenuIds()
    return true
  }

  const removeFavoriteMenu = (menuId: string) => {
    favoriteMenuIds.value = favoriteMenuIds.value.filter((value) => value !== menuId)
    persistFavoriteMenuIds()
  }

  const isFavoriteMenu = (menuId: string) => favoriteMenuIds.value.includes(menuId)

  const syncFavoriteMenuIds = (availableLeafMenuIds: string[]) => {
    const nextIds = favoriteMenuIds.value.filter((menuId) => availableLeafMenuIds.includes(menuId))

    if (nextIds.length === favoriteMenuIds.value.length) {
      return
    }

    favoriteMenuIds.value = nextIds
    persistFavoriteMenuIds()
  }

  const login = async (payload: LoginRequest) => {
    const rawSession = await loginApi({
      ...payload,
      serviceId: GROUPWARE_SERVICE_ID,
    })
    const session = hydrateGroupwareSession(rawSession)
    const serviceAccesses = session.user.serviceAccesses ?? session.serviceAccesses ?? []
    const groups = session.groups ?? []

    if (!serviceAccesses.includes(GROUPWARE_SERVICE_ID)) {
      throw new Error('GROUPWARE service access is required for login.')
    }

    persisted.value = {
      ...persisted.value,
      user: {
        ...session.user,
        serviceAccesses,
      },
      token: session.token,
      menus: session.menus,
      menuPermissions: session.menuPermissions,
      serviceId: session.serviceId ?? GROUPWARE_SERVICE_ID,
      serviceAccesses,
      groups,
    }

    if (session.token.accessToken) {
      window.localStorage.setItem('frontend_groupware.accessToken', session.token.accessToken)
    } else {
      window.localStorage.removeItem('frontend_groupware.accessToken')
    }
    loadFavoriteMenuIds(session.user.userId)
    return session
  }

  const clearSession = () => {
    persisted.value = {
      user: null,
      token: null,
      menus: [],
      menuPermissions: [],
      serviceId: null,
      serviceAccesses: [],
      groups: [],
      currentMenuId: null,
      currentMenuUrl: null,
    }

    window.localStorage.removeItem('frontend_groupware.accessToken')
    favoriteMenuIds.value = []
  }

  const logout = async () => {
    let logoutError: unknown = null

    try {
      if (persisted.value.user) {
        await logoutApi({
          refreshToken: persisted.value.token?.refreshToken,
        })
      }
    } catch (error) {
      logoutError = error
    } finally {
      clearSession()
    }

    if (logoutError) {
      throw logoutError
    }
  }

  const setCurrentMenu = (menuId: string | null, menuUrl: string | null) => {
    persisted.value.currentMenuId = menuId
    persisted.value.currentMenuUrl = menuUrl
  }

  watch(
    () => persisted.value.user?.userId ?? null,
    (userId) => {
      loadFavoriteMenuIds(userId)
    },
    { immediate: true },
  )

  return {
    persisted,
    favoriteMenuIds,
    isAuthenticated,
    permissionsMap,
    getPermissions,
    canAccess,
    hasGroupwareAccess,
    adminServiceIds,
    hasServiceAdmin,
    usesHttpOnlyCookie,
    addFavoriteMenu,
    removeFavoriteMenu,
    isFavoriteMenu,
    syncFavoriteMenuIds,
    login,
    logout,
    setCurrentMenu,
  }
})
