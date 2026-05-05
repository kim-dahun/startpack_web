import { defineStore } from 'pinia'

import { fetchRealtimeStatus } from '@/api/modules/trade'
import type { RealtimeConnectionState, RealtimeQuote, RealtimeStatus } from '@/types/app'

export const useRealtimeStore = defineStore('realtime', {
  state: () => ({
    connectionState: 'CONNECTING' as RealtimeConnectionState,
    status: null as RealtimeStatus | null,
    quotes: [] as RealtimeQuote[],
    timerId: null as number | null,
  }),
  actions: {
    async refresh() {
      this.connectionState = this.quotes.length ? 'RECONNECTING' : 'CONNECTING'

      try {
        // const status = await fetchRealtimeStatus()

        // this.status = status
        // this.quotes = []
        // this.connectionState = status.kisConnected ? 'CONNECTED' : 'DISCONNECTED'
      } catch (error) {
        console.error(error)
        this.connectionState = 'DISCONNECTED'
      }
    },
    start() {
      if (this.timerId) {
        return
      }

      // void this.refresh()
      // this.timerId = window.setInterval(() => {
      //   void this.refresh()
      // }, 4000)
    },
    stop() {
      if (!this.timerId) {
        return
      }

      // window.clearInterval(this.timerId)
      this.timerId = null
    },
  },
})
