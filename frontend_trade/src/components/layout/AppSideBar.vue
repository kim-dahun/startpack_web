<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, watch } from 'vue'
import Button from 'primevue/button'
import { useToast } from 'primevue/usetoast'

import AppMenuNode from '@/components/layout/AppMenuNode.vue'
import { useAppI18n } from '@/composables/useAppI18n'
import { useSessionStore } from '@/stores/session'
import { useUiStore } from '@/stores/ui'
import {
  buildFavoriteMenuTree,
  buildMenuTree,
  findLeafMenuIds,
  findMenuAncestorIds,
  type MenuTreeNode,
} from '@/utils/menuUtils'

const sessionStore = useSessionStore()
const uiStore = useUiStore()
const toast = useToast()
const { t } = useAppI18n()

const contextMenu = reactive<{
  visible: boolean
  x: number
  y: number
  node: MenuTreeNode | null
}>({
  visible: false,
  x: 0,
  y: 0,
  node: null,
})

const menuTree = computed(() => buildMenuTree(sessionStore.persisted.menus))
const favoriteMenuTree = computed(() =>
  buildFavoriteMenuTree(sessionStore.persisted.menus, sessionStore.favoriteMenuIds),
)
const activeMenuId = computed(() => sessionStore.persisted.currentMenuId)
const allMenuAncestorIds = computed(() =>
  findMenuAncestorIds(sessionStore.persisted.menus, sessionStore.persisted.currentMenuId),
)
const ancestorMenuIds = computed(() => {
  if (uiStore.sidebarMenuMode === 'favorites' && activeMenuId.value && sessionStore.isFavoriteMenu(activeMenuId.value)) {
    return ['FAVORITES_ROOT', 'FAVORITES_GROUP']
  }

  return allMenuAncestorIds.value
})
const visibleMenuTree = computed(() =>
  uiStore.sidebarMenuMode === 'favorites' ? favoriteMenuTree.value : menuTree.value,
)
const isCurrentContextFavorite = computed(() =>
  contextMenu.node ? sessionStore.isFavoriteMenu(contextMenu.node.menuId) : false,
)

const closeContextMenu = () => {
  contextMenu.visible = false
  contextMenu.node = null
}

const handleContextAction = ({ node, x, y }: { node: MenuTreeNode; x: number; y: number }) => {
  contextMenu.visible = true
  contextMenu.node = node
  contextMenu.x = x
  contextMenu.y = y
}

const handleFavoriteAction = () => {
  if (!contextMenu.node) {
    return
  }

  if (uiStore.sidebarMenuMode === 'favorites') {
    sessionStore.removeFavoriteMenu(contextMenu.node.menuId)
    closeContextMenu()
    return
  }

  if (isCurrentContextFavorite.value) {
    closeContextMenu()
    return
  }

  sessionStore.addFavoriteMenu(contextMenu.node.menuId)
  closeContextMenu()
}

const handleInvalidFavorite = () => {
  closeContextMenu()
  toast.add({
    severity: 'info',
    summary: t('sidebar.favorite.invalid'),
    detail: t('sidebar.favorite.invalidDetail'),
    life: 2200,
  })
}

const toggleMenu = (menuId: string) => {
  uiStore.toggleExpandedMenu(menuId)
}

watch(
  () => sessionStore.persisted.menus,
  (menus) => {
    sessionStore.syncFavoriteMenuIds(findLeafMenuIds(menus))
  },
  { immediate: true, deep: true },
)

watch(
  () => [activeMenuId.value, uiStore.sidebarMenuMode],
  () => {
    const mandatoryExpandedIds = [
      ...allMenuAncestorIds.value,
      ...(uiStore.sidebarMenuMode === 'favorites' && sessionStore.favoriteMenuIds.length
        ? ['FAVORITES_ROOT', 'FAVORITES_GROUP']
        : []),
    ]

    uiStore.expandMenus(mandatoryExpandedIds)
  },
  { immediate: true },
)

onMounted(() => {
  window.addEventListener('click', closeContextMenu)
  window.addEventListener('scroll', closeContextMenu)
})

onBeforeUnmount(() => {
  window.removeEventListener('click', closeContextMenu)
  window.removeEventListener('scroll', closeContextMenu)
})
</script>

<template>
  <aside class="app-sidebar">
    <div class="app-sidebar__brand">
      <p>{{ t('sidebar.section.all') }}</p>
      <h1>{{ t('app.title') }}</h1>
      <span>Gateway / User / Trade</span>
    </div>

    <div class="app-sidebar__menu-switch">
      <Button
        :severity="uiStore.sidebarMenuMode === 'all' ? 'contrast' : 'secondary'"
        :label="t('sidebar.mode.all')"
        size="small"
        @click="uiStore.setSidebarMenuMode('all')"
      />
      <Button
        :severity="uiStore.sidebarMenuMode === 'favorites' ? 'contrast' : 'secondary'"
        :label="t('sidebar.mode.favorites')"
        size="small"
        @click="uiStore.setSidebarMenuMode('favorites')"
      />
    </div>

    <nav class="app-sidebar__nav">
      <p class="app-sidebar__section">
        {{ uiStore.sidebarMenuMode === 'all' ? t('sidebar.section.all') : t('sidebar.section.favorites') }}
      </p>
      <p v-if="uiStore.sidebarMenuMode === 'favorites'" class="app-sidebar__hint">
        {{ t('sidebar.hint.favorites') }}
      </p>
      <ul v-if="visibleMenuTree.length">
        <AppMenuNode
          v-for="node in visibleMenuTree"
          :key="node.menuId"
          :node="node"
          :expanded-ids="uiStore.expandedMenuIds"
          :active-menu-id="activeMenuId"
          :ancestor-menu-ids="ancestorMenuIds"
          :menu-mode="uiStore.sidebarMenuMode"
          @toggle="toggleMenu"
          @context-action="handleContextAction"
          @invalid-favorite="handleInvalidFavorite"
        />
      </ul>
      <div v-else class="app-sidebar__empty">
        <strong>{{ t('sidebar.empty.favorites.title') }}</strong>
        <span>{{ t('sidebar.empty.favorites.description') }}</span>
      </div>
    </nav>

    <div
      v-if="contextMenu.visible && contextMenu.node"
      class="menu-context"
      :style="{ top: `${contextMenu.y}px`, left: `${contextMenu.x}px` }"
    >
      <button
        type="button"
        class="menu-context__action"
        :disabled="uiStore.sidebarMenuMode === 'all' && isCurrentContextFavorite"
        @click.stop="handleFavoriteAction"
      >
        {{
          uiStore.sidebarMenuMode === 'favorites'
            ? t('sidebar.favorite.remove')
            : isCurrentContextFavorite
              ? t('sidebar.favorite.exists')
              : t('sidebar.favorite.add')
        }}
      </button>
    </div>
  </aside>
</template>
