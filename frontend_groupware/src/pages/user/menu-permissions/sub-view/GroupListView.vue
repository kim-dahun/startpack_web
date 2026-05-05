<script setup lang="ts">
import type { GroupRow } from '../api/api'

defineProps<{
  groups: GroupRow[]
  selectedGroupId: string
}>()

const emit = defineEmits<{
  select: [groupId: string]
}>()
</script>

<template>
  <section class="split-panel">
    <header class="split-panel__header">
      <strong>Groups</strong>
    </header>
    <div class="selection-list">
      <button
        v-for="group in groups"
        :key="group.groupId"
        type="button"
        class="selection-list__item"
        :class="{ 'is-active': selectedGroupId === group.groupId }"
        @click="emit('select', group.groupId)"
      >
        <strong>{{ group.groupName }}</strong>
        <span>{{ group.groupId }}</span>
      </button>
    </div>
  </section>
</template>
