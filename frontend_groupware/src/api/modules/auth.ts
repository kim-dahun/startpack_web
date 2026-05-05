import http from '@/api/client/http'
import { GROUPWARE_SERVICE_ID } from '@/services/groupware/menuSeed'
import type {
  ApiEnvelope,
  LoginRequest,
  LoginResponse,
  LogoutRequest,
  LogoutResponse,
} from '@/types/app'

export const login = async (payload: LoginRequest) => {
  const requestPayload: LoginRequest = {
    ...payload,
    serviceId: GROUPWARE_SERVICE_ID,
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
