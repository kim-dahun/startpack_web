<script setup lang="ts">
import { computed } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'

use([CanvasRenderer, LineChart, GridComponent, TooltipComponent, LegendComponent])

const props = defineProps<{
  labels: string[]
  seriesName: string
  points: number[]
}>()

const option = computed(() => ({
  tooltip: {
    trigger: 'axis',
  },
  grid: {
    left: 18,
    right: 18,
    top: 18,
    bottom: 24,
    containLabel: true,
  },
  xAxis: {
    type: 'category',
    data: props.labels,
    boundaryGap: false,
  },
  yAxis: {
    type: 'value',
    scale: true,
  },
  series: [
    {
      name: props.seriesName,
      type: 'line',
      smooth: true,
      symbol: 'circle',
      areaStyle: {},
      data: props.points,
    },
  ],
}))
</script>

<template>
  <VChart class="base-line-chart" :option="option" autoresize />
</template>
