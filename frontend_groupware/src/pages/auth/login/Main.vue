<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from 'primevue/usetoast'

import { useAppI18n } from '@/composables/useAppI18n'
import { useSessionStore } from '@/stores/session'

import { submitLogin } from './api/api'
import { createLoginForm, loadLoginDraft, saveLoginDraft } from './model/pageModel'
import LoginFormView from './sub-view/LoginFormView.vue'

const sessionStore = useSessionStore()
const router = useRouter()
const toast = useToast()
const { t } = useAppI18n()

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
      summary: t('auth.loginSuccess'),
      detail: t('auth.loginSuccessDetail'),
      life: 2500,
    })
    await router.push('/groupware/notifications')
  } catch (error) {
    toast.add({
      severity: 'error',
      summary: t('auth.loginFailed'),
      detail: error instanceof Error ? error.message : t('auth.loginFailedDetail'),
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
        <p class="base-page-header__eyebrow">Groupware Service</p>
        <h1>{{ t('auth.loginTitle') }}</h1>
        <p>{{ t('auth.loginIntro') }}</p>
      </div>

      <LoginFormView :form="form" :loading="loading" @submit="handleLogin" />
    </section>
  </div>
</template>
