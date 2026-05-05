<script setup lang="ts">
import { computed } from 'vue'
import Button from 'primevue/button'
import Tag from 'primevue/tag'
import { useToast } from 'primevue/usetoast'
import { useAppI18n } from '@/composables/useAppI18n'
import { IconMoonStars, IconSunHigh, IconLayoutSidebarLeftCollapse, IconLogout2 } from '@tabler/icons-vue'
import { useRouter } from 'vue-router'

import { useRealtimeStore } from '@/stores/realtime'
import { useSessionStore } from '@/stores/session'
import { useUiStore } from '@/stores/ui'
import { buildMenuLookup, findMenuAncestorIds } from '@/utils/menuUtils'

const router = useRouter()
const toast = useToast()
const sessionStore = useSessionStore()
const uiStore = useUiStore()
const realtimeStore = useRealtimeStore()
const { localeOptions, resolveMenuLabel, t } = useAppI18n()

const connectionSeverity = {
  CONNECTING: 'warn',
  CONNECTED: 'success',
  RECONNECTING: 'contrast',
  DISCONNECTED: 'danger',
} as const

const connectionLabel = computed(() => {
  const labels = {
    CONNECTING: 'CONNECTING',
    CONNECTED: 'CONNECTED',
    RECONNECTING: 'RECONNECTING',
    DISCONNECTED: 'DISCONNECTED',
  }
  return labels[realtimeStore.connectionState]
})

const currentMenuLabel = computed(() => {
  const currentMenuId = sessionStore.persisted.currentMenuId

  if (!currentMenuId) {
    return t('menu.dashboard')
  }

  const lookup = buildMenuLookup(sessionStore.persisted.menus)
  const currentMenu = lookup.get(currentMenuId)
  return currentMenu ? resolveMenuLabel(currentMenu.menuName, currentMenu.i18nCode) : currentMenuId
})

const currentMenuTrail = computed(() => {
  const currentMenuId = sessionStore.persisted.currentMenuId

  if (!currentMenuId) {
    return ''
  }

  const lookup = buildMenuLookup(sessionStore.persisted.menus)
  const trailIds = [...findMenuAncestorIds(sessionStore.persisted.menus, currentMenuId).reverse(), currentMenuId]

  return trailIds
    .map((menuId) => {
      const menu = lookup.get(menuId)
      return menu ? resolveMenuLabel(menu.menuName, menu.i18nCode) : ''
    })
    .filter((menuName): menuName is string => Boolean(menuName))
    .join(' / ')
})

const handleLogout = async () => {
  try {
    await sessionStore.logout()
  } catch (error) {
    toast.add({
      severity: 'warn',
      summary: t('topbar.logoutFailed'),
      detail: error instanceof Error ? error.message : t('topbar.logoutFailedDetail'),
      life: 3000,
    })
  } finally {
    realtimeStore.stop()
    await router.push('/login')
  }
}
</script>

<template>
  <header class="app-topbar">
    <div>
      <p class="app-topbar__eyebrow">{{ t('topbar.currentMenu') }}</p>
      <strong>{{ currentMenuLabel }}</strong>
      <span v-if="currentMenuTrail">{{ currentMenuTrail }}</span>
      <span>{{ t('topbar.companyUser') }}: {{ sessionStore.persisted.user?.comCd }} / {{ sessionStore.persisted.user?.userName }}</span>
    </div>

    <div class="app-topbar__actions">
      <label class="app-topbar__locale">
        <span>{{ t('topbar.language') }}</span>
        <select
          :value="uiStore.locale"
          class="native-select"
          @change="uiStore.setLocale(($event.target as HTMLSelectElement).value as 'ko' | 'en' | 'ja' | 'zh')"
        >
          <option v-for="option in localeOptions" :key="option.value" :value="option.value">
            {{ option.label }}
          </option>
        </select>
      </label>
      <Tag :value="connectionLabel" :severity="connectionSeverity[realtimeStore.connectionState]" />
      <Button text rounded aria-label="sidebar" @click="uiStore.toggleSidebar()">
        <template #icon>
          <IconLayoutSidebarLeftCollapse :size="18" />
        </template>
      </Button>
      <Button text rounded aria-label="theme" @click="uiStore.toggleTheme()">
        <template #icon>
          <IconSunHigh v-if="uiStore.theme === 'dark'" :size="18" />
          <IconMoonStars v-else :size="18" />
        </template>
      </Button>
      <Button text rounded aria-label="logout" @click="handleLogout">
        <template #icon>
          <IconLogout2 :size="18" />
        </template>
      </Button>
    </div>
  </header>
</template>
