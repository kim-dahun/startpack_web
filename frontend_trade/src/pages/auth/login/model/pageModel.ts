import type { LoginRequest } from '@/types/app'

const LOGIN_DRAFT_STORAGE_KEY = 'frontend_trade.loginDraft'

export const createLoginForm = (): LoginRequest => ({
  comCd: 'COM001',
  userId: 'admin',
  password: 'admin',
  serviceId: 'TRADE',
})

export const loadLoginDraft = () => {
  try {
    const rawValue = window.localStorage.getItem(LOGIN_DRAFT_STORAGE_KEY)

    if (!rawValue) {
      return null
    }

    const parsed = JSON.parse(rawValue) as Partial<Pick<LoginRequest, 'comCd' | 'userId'>>

    return {
      comCd: parsed.comCd ?? '',
      userId: parsed.userId ?? '',
    }
  } catch {
    return null
  }
}

export const saveLoginDraft = (payload: Pick<LoginRequest, 'comCd' | 'userId'>) => {
  window.localStorage.setItem(LOGIN_DRAFT_STORAGE_KEY, JSON.stringify(payload))
}
