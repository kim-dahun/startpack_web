<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { IconChevronDown, IconChevronRight, IconPointFilled, IconStarFilled } from '@tabler/icons-vue'

import { useAppI18n } from '@/composables/useAppI18n'
import type { MenuTreeNode } from '@/utils/menuUtils'

const props = defineProps<{
  node: MenuTreeNode
  expandedIds: string[]
  activeMenuId: string | null
  ancestorMenuIds: string[]
  menuMode: 'all' | 'favorites'
}>()

const emit = defineEmits<{
  toggle: [menuId: string]
  contextAction: [payload: { node: MenuTreeNode; x: number; y: number }]
  invalidFavorite: []
}>()

const router = useRouter()
const route = useRoute()
const { resolveMenuLabel } = useAppI18n()

const isExpanded = computed(() => props.expandedIds.includes(props.node.menuId))
const isActive = computed(() => props.activeMenuId === props.node.menuId || route.path === props.node.menuUrl)
const isActiveTrail = computed(() => props.ancestorMenuIds.includes(props.node.menuId))
const isToggleNode = computed(() => !props.node.menuUrl || props.node.children.length > 0)
const depthClass = computed(() => `menu-node--depth-${props.node.depth}`)

const handleClick = async () => {
  if (isToggleNode.value) {
    emit('toggle', props.node.menuId)
    return
  }

  if (props.node.menuUrl) {
    await router.push(props.node.menuUrl)
  }
}

const handleContextMenu = (event: MouseEvent) => {
  event.preventDefault()

  if (!props.node.isLeaf) {
    emit('invalidFavorite')
    return
  }

  emit('contextAction', {
    node: props.node,
    x: event.clientX,
    y: event.clientY,
  })
}
</script>

<template>
  <li class="menu-node" :class="[depthClass, { 'is-expanded': isExpanded, 'is-active-trail': isActiveTrail }]">
    <button
      type="button"
      class="menu-node__button"
      :class="{
        'is-active': isActive,
        'is-group': !node.isLeaf,
        'is-active-trail': isActiveTrail,
        'is-favorite-leaf': menuMode === 'favorites' && node.isLeaf,
      }"
      @click="handleClick"
      @contextmenu="handleContextMenu"
    >
      <span class="menu-node__content">
        <span class="menu-node__prefix">
          <IconChevronDown v-if="!node.isLeaf && isExpanded" :size="16" />
          <IconChevronRight v-else-if="!node.isLeaf" :size="16" />
          <IconStarFilled v-else-if="menuMode === 'favorites'" :size="14" />
          <IconPointFilled v-else :size="10" />
        </span>
        <span class="menu-node__text">
          <strong>{{ resolveMenuLabel(node.menuName, node.i18nCode) }}</strong>
        </span>
      </span>
    </button>
    <ul v-if="node.children.length && isExpanded" class="menu-node__children">
      <AppMenuNode
        v-for="child in node.children"
        :key="child.menuId"
        :node="child"
        :expanded-ids="expandedIds"
        :active-menu-id="activeMenuId"
        :ancestor-menu-ids="ancestorMenuIds"
        :menu-mode="menuMode"
        @toggle="emit('toggle', $event)"
        @context-action="emit('contextAction', $event)"
        @invalid-favorite="emit('invalidFavorite')"
      />
    </ul>
  </li>
</template>
