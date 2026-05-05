<script setup lang="ts">
import { useAppI18n } from '@/composables/useAppI18n'

defineProps<{
  title?: string
  description?: string
}>()

const { t } = useAppI18n()
</script>

<template>
  <section class="base-state-card base-state-card--warn">
    <i class="pi pi-lock" />
    <h2>{{ title ?? t('state.forbidden.title') }}</h2>
    <p>
      {{ description ?? t('state.forbidden.detail') }}
    </p>
  </section>
</template>
