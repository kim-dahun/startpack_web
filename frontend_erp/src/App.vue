<script setup lang="ts">
import { RouterView } from 'vue-router'

import BaseConfirmDialog from '@/components/feedback/BaseConfirmDialog.vue'
import BaseToastBridge from '@/components/feedback/BaseToastBridge.vue'
import { useUiStore } from '@/stores/ui'

const uiStore = useUiStore()
</script>

<template>
  <div class="app-root" :class="uiStore.appThemeClass">
    <RouterView />
    <BaseToastBridge />
    <BaseConfirmDialog />
  </div>
</template>
