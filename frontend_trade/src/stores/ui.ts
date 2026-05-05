import { computed } from 'vue'
import { defineStore } from 'pinia'
import { useStorage } from '@vueuse/core'
import type { AppLocale } from '@/i18n/messages'

export const useUiStore = defineStore('ui', () => {
  const theme = useStorage<'light' | 'dark'>('frontend_trade.theme', 'light')
  const locale = useStorage<AppLocale>('frontend_trade.locale', 'ko')
  const sidebarOpen = useStorage('frontend_trade.sidebar-open', true)
  const sidebarMenuMode = useStorage<'all' | 'favorites'>('frontend_trade.sidebar-menu-mode', 'all')
  const expandedMenuIds = useStorage<string[]>('frontend_trade.sidebar-expanded-menu-ids', [])

  const appThemeClass = computed(() => (theme.value === 'dark' ? 'app-dark' : 'app-light'))

  const toggleTheme = () => {
    theme.value = theme.value === 'dark' ? 'light' : 'dark'
  }

  const setLocale = (value: AppLocale) => {
    locale.value = value
  }

  const toggleSidebar = () => {
    sidebarOpen.value = !sidebarOpen.value
  }

  const setSidebarMenuMode = (mode: 'all' | 'favorites') => {
    sidebarMenuMode.value = mode
  }

  const toggleExpandedMenu = (menuId: string) => {
    expandedMenuIds.value = expandedMenuIds.value.includes(menuId)
      ? expandedMenuIds.value.filter((value) => value !== menuId)
      : [...expandedMenuIds.value, menuId]
  }

  const expandMenus = (menuIds: string[]) => {
    expandedMenuIds.value = [...new Set([...expandedMenuIds.value, ...menuIds])]
  }

  return {
    theme,
    locale,
    sidebarOpen,
    sidebarMenuMode,
    expandedMenuIds,
    appThemeClass,
    toggleTheme,
    setLocale,
    toggleSidebar,
    setSidebarMenuMode,
    toggleExpandedMenu,
    expandMenus,
  }
})
