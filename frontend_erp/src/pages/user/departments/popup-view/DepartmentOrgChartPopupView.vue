<script setup lang="ts">
import { computed } from 'vue'
import OrganizationChart from 'primevue/organizationchart'

import BaseDialog from '@/components/common/BaseDialog.vue'
import BaseEmptyState from '@/components/common/BaseEmptyState.vue'
import { useAppI18n } from '@/composables/useAppI18n'

type OrgChartNode = {
  key: string
  label: string
  expanded?: boolean
  data?: Record<string, unknown>
  children?: OrgChartNode[]
}

const props = defineProps<{
  visible: boolean
  nodes: Array<Record<string, unknown>>
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
}>()

const { t } = useAppI18n()

const normalizeNode = (node: Record<string, unknown>): OrgChartNode => ({
  key: String(node.key ?? node.label ?? ''),
  label: String(node.label ?? node.key ?? ''),
  expanded: true,
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
  <BaseDialog :visible="visible" title="departments.orgChart.title" @update:visible="emit('update:visible', $event)">
    <div class="org-chart-popup">
      <BaseEmptyState
        v-if="!orgChartNodes.length"
        title="departments.orgChart.emptyTitle"
        description="departments.orgChart.emptyDescription"
      />
      <OrganizationChart v-else :value="orgChartNodes as any" class="department-org-chart">
        <template #default="slotProps">
          <div class="department-org-card">
            <strong>{{ slotProps.node.label }}</strong>
            <span>{{ String(slotProps.node.data?.departmentId ?? '-') }}</span>
            <small v-if="slotProps.node.data?.departmentHeadUserId">
              {{ t('departments.orgChart.headUser') }}: {{ String(slotProps.node.data.departmentHeadUserId) }}
            </small>
          </div>
        </template>
      </OrganizationChart>
    </div>
  </BaseDialog>
</template>
