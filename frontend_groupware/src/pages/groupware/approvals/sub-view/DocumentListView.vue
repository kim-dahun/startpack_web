<script setup lang="ts">
import GroupwarePanel from '@/components/groupware/GroupwarePanel.vue'
import type { GroupwareApprovalDocument } from '@/types/groupware'

defineProps<{
  documents: GroupwareApprovalDocument[]
  selectedDocumentId: string
}>()

const emit = defineEmits<{
  select: [documentId: string]
}>()
</script>

<template>
  <GroupwarePanel title="Documents" description="Approval documents visible to the current user.">
    <div class="selection-list">
      <button
        v-for="document in documents"
        :key="document.documentId"
        type="button"
        class="selection-list__item"
        :class="{ 'is-active': document.documentId === selectedDocumentId }"
        @click="emit('select', document.documentId)"
      >
        <strong>{{ document.title }}</strong>
        <span>{{ document.status }} / {{ document.documentType }}</span>
      </button>
    </div>
  </GroupwarePanel>
</template>
