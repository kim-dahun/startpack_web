<script setup lang="ts">
import { RouterView } from 'vue-router'
import { onBeforeUnmount, onMounted } from 'vue'

import AppFooter from '@/components/layout/AppFooter.vue'
import AppSideBar from '@/components/layout/AppSideBar.vue'
import AppTopBar from '@/components/layout/AppTopBar.vue'
import { useRealtimeStore } from '@/stores/realtime'
import { useUiStore } from '@/stores/ui'

const uiStore = useUiStore()
const realtimeStore = useRealtimeStore()

onMounted(() => {
  realtimeStore.start()
})

onBeforeUnmount(() => {
  realtimeStore.stop()
})
</script>

<template>
  <div class="app-shell" :class="{ 'sidebar-collapsed': !uiStore.sidebarOpen }">
    <AppSideBar />
    <div class="app-shell__body">
      <AppTopBar />
      <main class="app-shell__main">
        <RouterView />
      </main>
      <AppFooter />
    </div>
  </div>
</template>
