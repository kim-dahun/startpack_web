<script setup lang="ts">
import { useAppI18n } from '@/composables/useAppI18n'

defineProps<{
  title: string
  description: string
}>()

const { t } = useAppI18n()
</script>

<template>
  <section class="base-state-card">
    <i class="pi pi-inbox" />
    <h2>{{ t(title, title) }}</h2>
    <p>{{ t(description, description) }}</p>
  </section>
</template>
