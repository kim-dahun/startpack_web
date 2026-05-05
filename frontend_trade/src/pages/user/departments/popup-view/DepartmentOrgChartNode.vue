<script setup lang="ts">
import { useAppI18n } from '@/composables/useAppI18n'

type DepartmentOrgNode = {
  key: string
  label: string
  data: Record<string, unknown>
  children: DepartmentOrgNode[]
}

defineProps<{
  node: DepartmentOrgNode
}>()

const { t } = useAppI18n()
</script>

<template>
  <li class="department-org-node" :class="{ 'has-children': node.children.length > 0 }">
    <div class="department-org-card">
      <strong>{{ node.label }}</strong>
      <span>{{ String(node.data.departmentId ?? '-') }}</span>
      <small v-if="node.data.departmentHeadUserId">
        {{ t('departments.orgChart.headUser') }}: {{ String(node.data.departmentHeadUserId) }}
      </small>
    </div>

    <ul v-if="node.children.length" class="department-org-children">
      <DepartmentOrgChartNode
        v-for="child in node.children"
        :key="child.key"
        :node="child"
      />
    </ul>
  </li>
</template>
