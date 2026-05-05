import { computed } from 'vue'

import { appMessages, type AppLocale } from '@/i18n/messages'
import { useUiStore } from '@/stores/ui'

const interpolate = (template: string, params?: Record<string, string | number>) => {
  if (!params) {
    return template
  }

  return Object.entries(params).reduce(
    (message, [key, value]) => message.replaceAll(`{${key}}`, String(value)),
    template,
  )
}

export const useAppI18n = () => {
  const uiStore = useUiStore()

  const locale = computed(() => uiStore.locale)
  const localeOptions = computed(() => ([
    { label: appMessages[locale.value]['locale.ko'], value: 'ko' },
    { label: appMessages[locale.value]['locale.en'], value: 'en' },
    { label: appMessages[locale.value]['locale.ja'], value: 'ja' },
    { label: appMessages[locale.value]['locale.zh'], value: 'zh' },
  ] satisfies Array<{ label: string; value: AppLocale }>))

  const t = (key: string, fallback?: string, params?: Record<string, string | number>) => {
    const translated = appMessages[locale.value][key] ?? fallback ?? key
    return interpolate(translated, params)
  }

  const resolveMenuLabel = (menuName: string, i18nCode?: string | null) =>
    i18nCode ? t(i18nCode, menuName) : menuName

  return {
    locale,
    localeOptions,
    t,
    resolveMenuLabel,
  }
}
