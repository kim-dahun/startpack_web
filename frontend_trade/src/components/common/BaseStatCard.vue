<script setup lang="ts">
defineProps<{
  label: string
  value: string
  hint?: string
}>()
</script>

<template>
  <article class="base-stat-card">
    <span>{{ label }}</span>
    <strong>{{ value }}</strong>
  </article>
</template>
