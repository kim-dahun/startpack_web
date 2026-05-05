<script setup lang="ts">
import Button from 'primevue/button'
import { useRouter } from 'vue-router'

import { useAppI18n } from '@/composables/useAppI18n'
import { getRedirectPath } from './api/api'
import StateView from './sub-view/StateView.vue'

const router = useRouter()
const { t } = useAppI18n()
</script>

<template>
  <div class="page-stack">
    <StateView />
    <div class="cluster-inline">
      <Button :label="t('common.goHome')" icon="pi pi-home" @click="router.push(getRedirectPath())" />
    </div>
  </div>
</template>
