<script setup lang="ts">
import { useAppI18n } from '@/composables/useAppI18n'

const { t } = useAppI18n()
</script>

<template>
  <section class="management-card">
    <div class="management-card__header">
      <div>
        <h2>{{ t('menu.erp.print') }}</h2>
        <p>{{ t('erp.summary.print') }}</p>
      </div>
    </div>
  </section>
</template>
