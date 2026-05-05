<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from 'primevue/usetoast'

import { useSessionStore } from '@/stores/session'

import { submitLogin } from './api/api'
import { createLoginForm, loadLoginDraft, saveLoginDraft } from './model/pageModel'
import LoginFormView from './sub-view/LoginFormView.vue'

const sessionStore = useSessionStore()
const router = useRouter()
const toast = useToast()

const form = reactive(createLoginForm())
const loading = ref(false)

const handleLogin = async () => {
  loading.value = true

  try {
    await submitLogin(sessionStore, form)
    saveLoginDraft({
      comCd: form.comCd,
      userId: form.userId,
    })
    toast.add({
      severity: 'success',
      summary: '로그인 성공',
      detail: '사용자 정보를 불러왔습니다.',
      life: 2500,
    })
    await router.push('/dashboard')
  } catch (error) {
    toast.add({
      severity: 'error',
      summary: '로그인 실패',
      detail: error instanceof Error ? error.message : '로그인 처리에 실패했습니다.',
      life: 3000,
    })
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  const draft = loadLoginDraft()

  if (!draft) {
    return
  }

  form.comCd = draft.comCd || form.comCd
  form.userId = draft.userId || form.userId
})
</script>

<template>
  <div class="login-page">
    <section class="login-page__panel">
      <div class="login-page__intro">
        <p class="base-page-header__eyebrow">Trade Management</p>
        <h1>거래 관리 시스템</h1>
        <p>마지막 로그인 정보를 불러와 회사 코드와 사용자 ID를 자동으로 채웁니다.</p>
      </div>

      <LoginFormView :form="form" :loading="loading" @submit="handleLogin" />
    </section>
  </div>
</template>
