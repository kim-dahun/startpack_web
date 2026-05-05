<script setup lang="ts">
import Button from 'primevue/button'
import { useRouter } from 'vue-router'

import { getRedirectPath } from './api/api'
import StateView from './sub-view/StateView.vue'

const router = useRouter()
</script>

<template>
  <div class="page-stack">
    <StateView />
    <div class="cluster-inline">
      <Button label="대시보드로 이동" icon="pi pi-home" @click="router.push(getRedirectPath())" />
    </div>
  </div>
</template>
