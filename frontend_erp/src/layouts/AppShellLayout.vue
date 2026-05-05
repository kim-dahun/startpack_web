<script setup lang="ts">
import { RouterView } from 'vue-router'

import AppFooter from '@/components/layout/AppFooter.vue'
import AppSideBar from '@/components/layout/AppSideBar.vue'
import AppTopBar from '@/components/layout/AppTopBar.vue'
import { useUiStore } from '@/stores/ui'

const uiStore = useUiStore()
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
