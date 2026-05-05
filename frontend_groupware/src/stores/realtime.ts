import { defineStore } from 'pinia'

import { fetchRealtimeStatus } from '@/api/modules/groupware'
import {
  createConnectFrame,
  createDisconnectFrame,
  createGroupwareRealtimeSocket,
  createSubscribeFrame,
  parseRealtimeEvent,
  parseRealtimeFrames,
} from '@/services/groupware/websocket'
import { useSessionStore } from '@/stores/session'
import type { RealtimeConnectionState } from '@/types/app'
import type { GroupwareRealtimeEvent, GroupwareRealtimeStatus } from '@/types/groupware'

type GroupwareSocketState = RealtimeConnectionState | 'ERROR'

export const useRealtimeStore = defineStore('realtime', {
  state: () => ({
    connectionState: 'DISCONNECTED' as GroupwareSocketState,
    status: null as GroupwareRealtimeStatus | null,
    quotes: [] as never[],
    socket: null as WebSocket | null,
    events: [] as GroupwareRealtimeEvent[],
    reconnectTimerId: null as number | null,
    shouldReconnect: false,
  }),
  getters: {
    latestEvent: (state) => state.events[0] ?? null,
  },
  actions: {
    async refreshStatus() {
      try {
        this.status = await fetchRealtimeStatus()
      } catch {
        this.status = this.status ?? { unreadCount: 0, roomCount: 0 }
      }
    },
    clearReconnectTimer() {
      if (this.reconnectTimerId) {
        window.clearTimeout(this.reconnectTimerId)
        this.reconnectTimerId = null
      }
    },
    scheduleReconnect() {
      if (!this.shouldReconnect || this.reconnectTimerId) {
        return
      }

      this.reconnectTimerId = window.setTimeout(() => {
        this.reconnectTimerId = null
        void this.connect()
      }, 3000)
    },
    async connect() {
      const sessionStore = useSessionStore()
      const user = sessionStore.persisted.user

      if (!sessionStore.isAuthenticated || !user) {
        this.connectionState = 'DISCONNECTED'
        return
      }

      if (this.socket && (this.socket.readyState === WebSocket.CONNECTING || this.socket.readyState === WebSocket.OPEN)) {
        return
      }

      this.shouldReconnect = true
      this.connectionState = this.status ? 'RECONNECTING' : 'CONNECTING'
      this.clearReconnectTimer()

      const socket = createGroupwareRealtimeSocket()
      this.socket = socket

      socket.onopen = () => {
        const token = window.localStorage.getItem('frontend_groupware.accessToken')
        socket.send(createConnectFrame(token, user.userId, user.comCd))
      }

      socket.onmessage = (event) => {
        const frames = parseRealtimeFrames(String(event.data))

        frames.forEach((frame) => {
          if (frame.command === 'CONNECTED') {
            this.connectionState = 'CONNECTED'
            socket.send(createSubscribeFrame('groupware-notifications', `/topic/groupware/${user.comCd}/users/${user.userId}/notifications`))
            socket.send(createSubscribeFrame('groupware-messages', `/topic/groupware/${user.comCd}/users/${user.userId}/messages`))
            void this.refreshStatus()
            return
          }

          if (frame.command === 'MESSAGE') {
            const payload = parseRealtimeEvent(frame.body)
            if (payload) {
              this.events = [payload, ...this.events].slice(0, 30)
              void this.refreshStatus()
            }
            return
          }

          if (frame.command === 'ERROR') {
            this.connectionState = 'ERROR'
          }
        })
      }

      socket.onerror = () => {
        this.connectionState = 'ERROR'
      }

      socket.onclose = () => {
        this.socket = null
        this.connectionState = 'DISCONNECTED'
        this.scheduleReconnect()
      }
    },
    disconnect() {
      this.shouldReconnect = false
      this.clearReconnectTimer()

      if (this.socket && this.socket.readyState === WebSocket.OPEN) {
        this.socket.send(createDisconnectFrame())
      }

      this.socket?.close()
      this.socket = null
      this.connectionState = 'DISCONNECTED'
    },
    start() {
      void this.refreshStatus()
      void this.connect()
    },
    stop() {
      this.disconnect()
    },
    clearEvents() {
      this.events = []
    },
  },
})
