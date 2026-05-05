<script setup lang="ts">
defineProps<{
  title: string
  description: string
}>()
</script>

<template>
  <section class="base-state-card">
    <i class="pi pi-inbox" />
    <h2>{{ title }}</h2>
    <p>{{ description }}</p>
  </section>
</template>
