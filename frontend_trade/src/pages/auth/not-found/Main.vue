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
      <Button label="이전 화면으로 이동" icon="pi pi-arrow-left" @click="router.push(getRedirectPath())" />
    </div>
  </div>
</template>
