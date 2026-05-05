<script setup lang="ts">
import { computed } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { CandlestickChart, LineChart, BarChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent, DataZoomComponent } from 'echarts/components'

import type { TradeChartDrawing, TradeChartPoint, TradeItemIndicators } from '@/types/trade'

use([CanvasRenderer, CandlestickChart, LineChart, BarChart, GridComponent, TooltipComponent, LegendComponent, DataZoomComponent])

const props = defineProps<{
  rows: TradeChartPoint[]
  indicators: TradeItemIndicators | null
  drawings: TradeChartDrawing[]
}>()

const option = computed(() => {
  const labels = props.rows.map((row) => row.baseDate)
  const candles = props.rows.map((row) => [row.openPrice, row.closePrice, row.lowPrice, row.highPrice])
  const maValue = (key: keyof NonNullable<TradeItemIndicators['movingAverages']>) =>
    props.rows.map(() => Number(props.indicators?.movingAverages?.[key] ?? 0))
  const drawingSeries = props.drawings.map((drawing) => ({
    name: `${drawing.drawingType}-${drawing.id}`,
    type: 'line',
    symbol: 'circle',
    lineStyle: {
      type: 'dashed',
      width: 2,
      color: drawing.drawingType === 'UPPER_LINE' ? '#ef4444' : '#2563eb',
    },
    data: labels.map((label) => {
      if (label === drawing.startDate) {
        return drawing.startPrice
      }
      if (label === drawing.endDate) {
        return drawing.endPrice
      }
      return null
    }),
    connectNulls: true,
  }))

  return {
    animation: false,
    tooltip: { trigger: 'axis' },
    legend: {
      top: 0,
      data: ['Candle', 'MA5', 'MA20', 'MA60', 'MA120', ...props.drawings.map((drawing) => drawing.memo || drawing.drawingType)],
    },
    dataZoom: [
      { type: 'inside', xAxisIndex: [0, 1] },
      { type: 'slider', xAxisIndex: [0, 1], bottom: 0 },
    ],
    grid: [
      { left: 12, right: 12, top: 34, height: '58%' },
      { left: 12, right: 12, top: '74%', height: '16%' },
    ],
    xAxis: [
      { type: 'category', data: labels, scale: true, boundaryGap: false },
      { type: 'category', gridIndex: 1, data: labels, scale: true, boundaryGap: false },
    ],
    yAxis: [
      { scale: true },
      { gridIndex: 1, scale: true },
    ],
    series: [
      { name: 'Candle', type: 'candlestick', data: candles },
      { name: 'MA5', type: 'line', data: maValue('ma5'), smooth: true, showSymbol: false },
      { name: 'MA20', type: 'line', data: maValue('ma20'), smooth: true, showSymbol: false },
      { name: 'MA60', type: 'line', data: maValue('ma60'), smooth: true, showSymbol: false },
      { name: 'MA120', type: 'line', data: maValue('ma120'), smooth: true, showSymbol: false },
      ...drawingSeries,
      {
        name: 'Volume',
        type: 'bar',
        xAxisIndex: 1,
        yAxisIndex: 1,
        data: props.rows.map((row) => row.volume),
      },
    ],
  }
})
</script>

<template>
  <VChart class="base-line-chart" :option="option" autoresize />
</template>
