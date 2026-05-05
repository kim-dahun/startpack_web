<script setup lang="ts">
import { useAppI18n } from '@/composables/useAppI18n'

defineProps<{
  title: string
  description?: string
}>()

const { t } = useAppI18n()
</script>

<template>
  <section class="trade-panel">
    <div class="trade-panel__header-panel">
      <header class="trade-panel__header">
        <div class="trade-panel__title-block">
          <h2>{{ t(title, title) }}</h2>
          <p v-if="description">{{ t(description, description) }}</p>
        </div>
        <div v-if="$slots.actions" class="trade-panel__actions">
          <slot name="actions" />
        </div>
      </header>
    </div>
    <div class="trade-panel__body-panel">
      <div class="trade-panel__body">
        <div class="trade-panel__scroll">
          <slot />
        </div>
      </div>
    </div>
  </section>
</template>
