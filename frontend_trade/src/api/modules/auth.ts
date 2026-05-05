import http from '@/api/client/http'
import type {
  ApiEnvelope,
  LoginRequest,
  LoginResponse,
  LogoutRequest,
  LogoutResponse,
} from '@/types/app'

const TRADE_SERVICE_ID = 'TRADE'

export const login = async (payload: LoginRequest) => {
  const requestPayload: LoginRequest = {
    ...payload,
    serviceId: TRADE_SERVICE_ID,
  }

  const response = await http.post<ApiEnvelope<LoginResponse>>('/api/users/login', requestPayload)
  return response.data.data
}

export const logout = async (payload?: LogoutRequest) => {
  const requestPayload = payload?.refreshToken
    ? { refreshToken: payload.refreshToken }
    : {}

  const response = await http.post<ApiEnvelope<LogoutResponse>>('/api/auth/tokens/logout', requestPayload)
  return response.data.data
}
