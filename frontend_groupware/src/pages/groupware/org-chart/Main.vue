<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'

import BasePageHeader from '@/components/common/BasePageHeader.vue'
import BaseStatCard from '@/components/common/BaseStatCard.vue'
import { listDirectoryUsers } from './api/api'
import { filterDirectoryRows } from './model/pageModel'
import DirectoryTableView from './sub-view/DirectoryTableView.vue'
import type { GroupwareDirectoryUser } from '@/types/groupware'

const keyword = ref('')
const users = ref<GroupwareDirectoryUser[]>([])

const filteredRows = computed(() => filterDirectoryRows(users.value, keyword.value))
const departmentCount = computed(() => new Set(users.value.map((user) => user.primaryDepartmentName).filter(Boolean)).size)

const loadRows = async () => {
  users.value = await listDirectoryUsers(keyword.value)
}

onMounted(async () => {
  await loadRows()
})
</script>

<template>
  <div class="page-stack">
    <BasePageHeader title="Org Chart" description="Directory view backed by /api/groupware/directory/users." />

    <section class="stats-grid">
      <BaseStatCard label="Users" :value="String(users.length)" />
      <BaseStatCard label="Departments" :value="String(departmentCount)" />
      <BaseStatCard label="Source" value="GROUPWARE + backend_user" />
    </section>

    <DirectoryTableView
      :keyword="keyword"
      :rows="filteredRows as unknown as Array<Record<string, unknown>>"
      @update-keyword="keyword = $event"
      @search="loadRows"
    />
  </div>
</template>
