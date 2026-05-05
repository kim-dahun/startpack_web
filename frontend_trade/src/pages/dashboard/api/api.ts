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
    status: { cachedEventCount?: number } | null
    connectionState: string
  },
) => [
  {
    label: '세션 사용자',
    value: sessionStore.persisted.user?.userName ?? '-',
    hint: `${sessionStore.persisted.user?.comCd ?? '-'} / ${sessionStore.persisted.user?.userId ?? '-'}`,
  },
  {
    label: '렌더 메뉴',
    value: String(sessionStore.persisted.menus.length),
    hint: '3depth 메뉴 구조',
  },
  {
    label: '실시간 이벤트',
    value: String(realtimeStore.status?.cachedEventCount ?? 0),
    hint: realtimeStore.connectionState,
  },
  {
    label: '서비스 접근',
    value: sessionStore.persisted.serviceAccesses.join(', ') || '-',
    hint: `현재 서비스 ${sessionStore.persisted.serviceId ?? '-'}`,
  },
  {
    label: '토큰 전달',
    value: sessionStore.persisted.token?.tokenDeliveryMethod ?? 'BEARER',
    hint: sessionStore.usesHttpOnlyCookie ? 'cookie session mode' : 'authorization header mode',
  },
]

export const selectTopQuotes = (quotes: RealtimeQuote[]) => quotes.slice(0, 3)
