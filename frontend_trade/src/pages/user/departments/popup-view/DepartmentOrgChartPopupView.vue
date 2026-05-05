<script setup lang="ts">
import { computed } from 'vue'

import BaseDialog from '@/components/common/BaseDialog.vue'
import BaseEmptyState from '@/components/common/BaseEmptyState.vue'
import { useAppI18n } from '@/composables/useAppI18n'
import DepartmentOrgChartNode from './DepartmentOrgChartNode.vue'

type DepartmentOrgNode = {
  key: string
  label: string
  data: Record<string, unknown>
  children: DepartmentOrgNode[]
}

const props = defineProps<{
  visible: boolean
  nodes: Array<Record<string, unknown>>
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
}>()

const { t } = useAppI18n()

const normalizeNode = (node: Record<string, unknown>): DepartmentOrgNode => ({
  key: String(node.key ?? node.label ?? ''),
  label: String(node.label ?? node.key ?? ''),
  data: (node.data as Record<string, unknown> | undefined) ?? {},
  children: Array.isArray(node.children)
    ? node.children.map((child) => normalizeNode(child as Record<string, unknown>))
    : [],
})

const orgChartNodes = computed(() =>
  Array.isArray(props.nodes) ? props.nodes.map((node) => normalizeNode(node)) : [],
)
</script>

<template>
  <BaseDialog :visible="visible" :title="t('departments.orgChart.title')" @update:visible="emit('update:visible', $event)">
    <div class="org-chart-popup">
      <BaseEmptyState
        v-if="!orgChartNodes.length"
        :title="t('departments.orgChart.emptyTitle')"
        :description="t('departments.orgChart.emptyDescription')"
      />

      <div v-else class="department-org-chart-shell">
        <ul class="department-org-chart">
          <DepartmentOrgChartNode
            v-for="node in orgChartNodes"
            :key="node.key"
            :node="node"
          />
        </ul>
      </div>
    </div>
  </BaseDialog>
</template>
