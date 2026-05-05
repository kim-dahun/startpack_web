import axios from 'axios'

import type { ApiEnvelope } from '@/types/app'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? ''

const http = axios.create({
  baseURL: API_BASE_URL,
  timeout: 60000,
  withCredentials: true,
})

http.interceptors.request.use((config) => {
  const token = window.localStorage.getItem('frontend_groupware.accessToken')

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

http.interceptors.response.use((response) => {
  const payload = response.data as Partial<ApiEnvelope<unknown>> | unknown

  if (
    payload
    && typeof payload === 'object'
    && 'success' in payload
    && payload.success === false
  ) {
    const message = 'responseMessage' in payload ? String(payload.responseMessage ?? '요청이 실패했습니다.') : '요청이 실패했습니다.'
    return Promise.reject(new Error(message))
  }

  return response
})

export default http
