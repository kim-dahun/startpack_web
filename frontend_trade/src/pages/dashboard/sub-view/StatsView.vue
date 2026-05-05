<script setup lang="ts">
import BaseStatCard from '@/components/common/BaseStatCard.vue'

defineProps<{
  stats: Array<{ label: string; value: string; hint: string }>
}>()
</script>

<template>
  <section class="stats-grid">
    <BaseStatCard v-for="stat in stats" :key="stat.label" :label="stat.label" :value="stat.value" :hint="stat.hint" />
  </section>
</template>
