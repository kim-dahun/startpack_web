<script setup lang="ts">
import GroupwarePanel from '@/components/groupware/GroupwarePanel.vue'
import type { GroupwareProjectItem } from '@/types/groupware'

defineProps<{
  projects: GroupwareProjectItem[]
  selectedProjectId: string
}>()

const emit = defineEmits<{
  select: [projectId: string]
}>()
</script>

<template>
  <GroupwarePanel title="Projects" description="Visible project list from /api/groupware/projects.">
    <div class="selection-list">
      <button
        v-for="project in projects"
        :key="project.projectId"
        type="button"
        class="selection-list__item"
        :class="{ 'is-active': project.projectId === selectedProjectId }"
        @click="emit('select', project.projectId)"
      >
        <strong>{{ project.name }}</strong>
        <span>{{ project.status }} / progress {{ project.progressRate }}%</span>
      </button>
    </div>
  </GroupwarePanel>
</template>
