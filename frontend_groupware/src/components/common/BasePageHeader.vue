<script setup lang="ts">
import { computed } from 'vue'

import { useAppI18n } from '@/composables/useAppI18n'
import { useSessionStore } from '@/stores/session'
import { buildMenuLookup, findMenuAncestorIds } from '@/utils/menuUtils'

const props = defineProps<{
  title: string
  description?: string
}>()

const sessionStore = useSessionStore()
const { resolveMenuLabel, t } = useAppI18n()

const menuTrail = computed(() => {
  const currentMenuId = sessionStore.persisted.currentMenuId

  if (!currentMenuId) {
    return []
  }

  const lookup = buildMenuLookup(sessionStore.persisted.menus)
  const ancestorIds = findMenuAncestorIds(sessionStore.persisted.menus, currentMenuId).reverse()
  const trailIds = [...ancestorIds, currentMenuId]

  return trailIds
    .map((menuId) => lookup.get(menuId))
    .filter((menu): menu is NonNullable<typeof menu> => Boolean(menu))
})

const translatedTrail = computed(() =>
  menuTrail.value.map((menu) => resolveMenuLabel(menu.menuName, menu.i18nCode)),
)

const displayTitle = computed(() => {
  const currentMenu = menuTrail.value.at(-1)
  if (currentMenu) {
    return resolveMenuLabel(currentMenu.menuName, currentMenu.i18nCode)
  }
  return t(props.title, props.title)
})

const trailLabel = computed(() => translatedTrail.value.join(' / '))
</script>

<template>
  <header class="base-page-header">
    <div class="base-page-header__main-panel">
      <div class="base-page-header__content">
        <div v-if="menuTrail.length" class="base-page-header__trail">
          <span
            v-for="(menu, index) in menuTrail"
            :key="menu.menuId"
            class="base-page-header__trail-item"
          >
            <strong>{{ resolveMenuLabel(menu.menuName, menu.i18nCode) }}</strong>
            <span v-if="index < menuTrail.length - 1"> > </span>
                      </span>
        </div>
        <h1>{{ displayTitle }}</h1>
        <p v-if="trailLabel" class="base-page-header__subtitle">{{ trailLabel }}</p>
        <p v-if="description" class="base-page-header__description">{{ t(description, description) }}</p>
      </div>
    </div>
    <div v-if="$slots.actions" class="base-page-header__action-panel">
      <div class="base-page-header__actions">
        <slot name="actions" />
      </div>
    </div>
  </header>
</template>
