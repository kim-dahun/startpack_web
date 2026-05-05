import type { LoginRequest } from '@/types/app'

export const submitLogin = (
  sessionStore: { login: (payload: LoginRequest) => Promise<unknown> },
  payload: LoginRequest,
) => sessionStore.login(payload)

export const submitLogout = (
  sessionStore: { logout: () => Promise<void> },
) => sessionStore.logout()
