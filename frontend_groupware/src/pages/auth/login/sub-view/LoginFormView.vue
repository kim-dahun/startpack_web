<script setup lang="ts">
import Button from 'primevue/button'
import Card from 'primevue/card'
import InputText from 'primevue/inputtext'
import Password from 'primevue/password'

import { useAppI18n } from '@/composables/useAppI18n'
import type { LoginRequest } from '@/types/app'

defineProps<{
  form: LoginRequest
  loading: boolean
}>()

const emit = defineEmits<{
  submit: []
}>()

const { t } = useAppI18n()
</script>

<template>
  <Card class="login-card">
    <template #title>{{ t('auth.login') }}</template>
    <template #content>
      <div class="login-form">
        <label>
          <span>comCd</span>
          <InputText v-model="form.comCd" />
        </label>
        <label>
          <span>userId</span>
          <InputText v-model="form.userId" />
        </label>
        <label>
          <span>password</span>
          <Password v-model="form.password" toggle-mask :feedback="false" fluid />
        </label>
        <label>
          <span>serviceId</span>
          <InputText v-model="form.serviceId" disabled />
        </label>
        <Button :label="t('auth.login')" icon="pi pi-sign-in" :loading="loading" @click="emit('submit')" />
      </div>
    </template>
  </Card>
</template>
