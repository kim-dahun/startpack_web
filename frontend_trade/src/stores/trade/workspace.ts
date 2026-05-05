import { defineStore } from 'pinia'

import type { TradeChartPeriodType, TradeMode, TradeSide } from '@/types/trade'

const today = new Date().toISOString().slice(0, 10)

export const useTradeWorkspaceStore = defineStore('trade-workspace', {
  state: () => ({
    tradeMode: 'LIVE' as TradeMode,
    selectedAccountNo: '' as string,
    selectedBaseDate: today,
    selectedItemCode: '' as string,
    selectedItemName: '' as string,
    selectedWatchlistGroupId: null as number | null,
    chartInterval: 'DAY' as TradeChartPeriodType,
    selectedOrderPrice: 0,
    selectedOrderSide: 'BUY' as TradeSide,
  }),
  actions: {
    setTradeMode(mode: TradeMode) {
      this.tradeMode = mode
    },
    setSelectedAccountNo(accountNo: string) {
      this.selectedAccountNo = accountNo
    },
    setSelectedBaseDate(baseDate: string) {
      this.selectedBaseDate = baseDate
    },
    setSelectedItemCode(itemCode: string) {
      this.selectedItemCode = itemCode
    },
    setSelectedItem(itemCode: string, itemName = '') {
      this.selectedItemCode = itemCode
      this.selectedItemName = itemName
    },
    setSelectedWatchlistGroupId(groupId: number | null) {
      this.selectedWatchlistGroupId = groupId
    },
    setChartInterval(interval: TradeChartPeriodType) {
      this.chartInterval = interval
    },
    setSelectedOrderPrice(price: number) {
      this.selectedOrderPrice = price
    },
    setSelectedOrderSide(side: TradeSide) {
      this.selectedOrderSide = side
    },
  },
})
