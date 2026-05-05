import type { RealtimeQuote } from '@/types/app'

export const buildDashboardStats = (
  sessionStore: {
    persisted: {
      user: { userName: string; comCd: string; userId: string } | null
      menus: unknown[]
      serviceAccesses: string[]
      serviceId: string | null
      token: { tokenDeliveryMethod?: string } | null
    }
    usesHttpOnlyCookie: boolean
  },
  realtimeStore: {
    status: { cachedEventCount?: number; unreadCount?: number; roomCount?: number } | null
    connectionState: string
  },
  t: (key: string, fallback?: string, params?: Record<string, string | number>) => string,
) => [
  {
    label: t('dashboard.sessionUser'),
    value: sessionStore.persisted.user?.userName ?? '-',
    hint: `${sessionStore.persisted.user?.comCd ?? '-'} / ${sessionStore.persisted.user?.userId ?? '-'}`,
  },
  {
    label: t('dashboard.loadedMenus'),
    value: String(sessionStore.persisted.menus.length),
    hint: t('dashboard.loadedMenusHint'),
  },
  {
    label: t('dashboard.realtimeStatus'),
    value: String(realtimeStore.status?.unreadCount ?? realtimeStore.status?.cachedEventCount ?? 0),
    hint: realtimeStore.connectionState,
  },
  {
    label: t('dashboard.serviceAccess'),
    value: sessionStore.persisted.serviceAccesses.join(', ') || '-',
    hint: t('dashboard.currentService', undefined, { serviceId: sessionStore.persisted.serviceId ?? '-' }),
  },
  {
    label: t('dashboard.tokenDelivery'),
    value: sessionStore.persisted.token?.tokenDeliveryMethod ?? 'BEARER',
    hint: sessionStore.usesHttpOnlyCookie ? t('dashboard.cookieSessionMode') : t('dashboard.authorizationHeaderMode'),
  },
]

export const selectTopQuotes = (quotes: RealtimeQuote[]) => quotes.slice(0, 3)
