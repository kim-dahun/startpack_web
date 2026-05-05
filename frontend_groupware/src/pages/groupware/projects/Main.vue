<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import Button from 'primevue/button'
import InputNumber from 'primevue/inputnumber'
import InputText from 'primevue/inputtext'
import { useConfirm } from 'primevue/useconfirm'
import { useToast } from 'primevue/usetoast'

import BaseDialog from '@/components/common/BaseDialog.vue'
import BaseEmptyState from '@/components/common/BaseEmptyState.vue'
import BasePageHeader from '@/components/common/BasePageHeader.vue'
import { useAppI18n } from '@/composables/useAppI18n'
import GroupwarePanel from '@/components/groupware/GroupwarePanel.vue'
import GroupwareRecordTable from '@/components/groupware/GroupwareRecordTable.vue'
import { useSessionStore } from '@/stores/session'
import type {
  GroupwareDirectoryUser,
  GroupwareProjectComment,
  GroupwareProjectItem,
  GroupwareProjectTask,
} from '@/types/groupware'
import {
  createProject,
  createProjectComment,
  createProjectTask,
  deleteProject,
  deleteProjectComment,
  deleteProjectTask,
  listDirectoryUsers,
  listProjectComments,
  listProjectTaskComments,
  listProjectTasks,
  listProjects,
  searchProjects,
  updateProject,
  updateProjectComment,
  updateProjectProgressRate,
  updateProjectStatus,
  updateProjectTask,
  updateProjectTaskStatus,
} from './api/api'

const props = defineProps<{
  title: string
  description: string
  focusMode: 'overview' | 'tasks'
}>()

type ProjectDialogMode = 'create' | 'edit'
type TaskDialogMode = 'create' | 'edit'
type CommentDialogMode = 'create' | 'edit'

const sessionStore = useSessionStore()
const toast = useToast()
const confirm = useConfirm()
const { t } = useAppI18n()

const permissions = computed(() => sessionStore.getPermissions(sessionStore.persisted.currentMenuId))

const projects = ref<GroupwareProjectItem[]>([])
const selectedProjectId = ref('')
const selectedTaskId = ref('')
const tasks = ref<GroupwareProjectTask[]>([])
const comments = ref<GroupwareProjectComment[]>([])
const directoryUsers = ref<GroupwareDirectoryUser[]>([])
const projectDialogVisible = ref(false)
const taskDialogVisible = ref(false)
const commentDialogVisible = ref(false)
const projectDialogMode = ref<ProjectDialogMode>('create')
const taskDialogMode = ref<TaskDialogMode>('create')
const commentDialogMode = ref<CommentDialogMode>('create')
const searchKeyword = ref('')
const searchStatus = ref('')
const commentTaskFilter = ref('')
const editingCommentId = ref('')
const editingTaskId = ref('')

const projectForm = reactive({
  name: '',
  description: '',
  memberUserIds: [] as string[],
  referenceUserIds: [] as string[],
  status: 'PLANNED' as 'PLANNED' | 'IN_PROGRESS' | 'DONE' | 'ON_HOLD',
  progressRate: 0,
})

const taskForm = reactive({
  title: '',
  description: '',
  assigneeUserId: '',
  dueDate: '',
  status: 'TODO' as 'TODO' | 'IN_PROGRESS' | 'DONE' | 'ON_HOLD',
})

const commentForm = reactive({
  taskId: '',
  content: '',
})

const selectedProject = computed(() => projects.value.find((project) => project.projectId === selectedProjectId.value) ?? null)
const memberOptions = computed(() => directoryUsers.value.map((user) => ({
  label: `${user.userName} (${user.userId})`,
  value: user.userId,
})))
const taskRows = computed(() => tasks.value.map((task) => ({
  taskId: task.taskId,
  title: task.title,
  assigneeUserId: task.assigneeUserId || '-',
  dueDate: task.dueDate || '-',
  status: task.status,
})))
const commentRows = computed(() => comments.value.map((comment) => ({
  commentId: comment.commentId,
  taskId: comment.taskId || '-',
  content: comment.content,
  createdByUserId: comment.createdByUserId || '-',
  createdAt: comment.createdAt ? comment.createdAt.replace('T', ' ').slice(0, 16) : '-',
})))
const projectStats = computed(() => {
  if (!selectedProject.value) {
    return []
  }

  return [
    { label: t('common.project'), value: selectedProject.value.name },
    { label: t('common.status'), value: selectedProject.value.status },
    { label: t('groupware.progressRate'), value: `${selectedProject.value.progressRate}%` },
    { label: t('groupware.status.tasks'), value: String(tasks.value.length) },
  ]
})

const taskColumns = [
  { field: 'title', title: 'common.title' },
  { field: 'assigneeUserId', title: 'common.user' },
  { field: 'dueDate', title: 'Due Date' },
  { field: 'status', title: 'common.status' },
]

const commentColumns = [
  { field: 'taskId', title: 'Task' },
  { field: 'content', title: 'common.content' },
  { field: 'createdByUserId', title: 'common.author' },
  { field: 'createdAt', title: 'common.createdAt' },
]

function resetProjectForm() {
  projectForm.name = ''
  projectForm.description = ''
  projectForm.memberUserIds = []
  projectForm.referenceUserIds = []
  projectForm.status = 'PLANNED'
  projectForm.progressRate = 0
}

function resetTaskForm() {
  taskForm.title = ''
  taskForm.description = ''
  taskForm.assigneeUserId = ''
  taskForm.dueDate = ''
  taskForm.status = 'TODO'
}

function resetCommentForm() {
  commentForm.taskId = ''
  commentForm.content = ''
}

function openCreateProject() {
  projectDialogMode.value = 'create'
  resetProjectForm()
  projectDialogVisible.value = true
}

function openEditProject() {
  if (!selectedProject.value) {
    return
  }

  projectDialogMode.value = 'edit'
  projectForm.name = selectedProject.value.name
  projectForm.description = selectedProject.value.description ?? ''
  projectForm.memberUserIds = [...(selectedProject.value.memberUserIds ?? [])]
  projectForm.referenceUserIds = [...(selectedProject.value.referenceUserIds ?? [])]
  projectForm.status = ['DONE', 'ON_HOLD', 'IN_PROGRESS'].includes(selectedProject.value.status)
    ? selectedProject.value.status as typeof projectForm.status
    : 'PLANNED'
  projectForm.progressRate = Number(selectedProject.value.progressRate ?? 0)
  projectDialogVisible.value = true
}

function openCreateTask() {
  taskDialogMode.value = 'create'
  editingTaskId.value = ''
  resetTaskForm()
  taskDialogVisible.value = true
}

function openEditTask(taskId: string) {
  const target = tasks.value.find((task) => task.taskId === taskId)
  if (!target) {
    return
  }

  taskDialogMode.value = 'edit'
  editingTaskId.value = taskId
  taskForm.title = target.title
  taskForm.description = target.description ?? ''
  taskForm.assigneeUserId = target.assigneeUserId ?? ''
  taskForm.dueDate = target.dueDate ?? ''
  taskForm.status = ['IN_PROGRESS', 'DONE', 'ON_HOLD'].includes(target.status) ? target.status as typeof taskForm.status : 'TODO'
  taskDialogVisible.value = true
}

function openCreateComment() {
  commentDialogMode.value = 'create'
  editingCommentId.value = ''
  resetCommentForm()
  commentDialogVisible.value = true
}

function openEditComment(commentId: string) {
  const target = comments.value.find((comment) => comment.commentId === commentId)
  if (!target) {
    return
  }

  commentDialogMode.value = 'edit'
  editingCommentId.value = commentId
  commentForm.taskId = target.taskId ?? ''
  commentForm.content = target.content
  commentDialogVisible.value = true
}

async function loadProjectsData() {
  projects.value = searchKeyword.value.trim() || searchStatus.value
    ? await searchProjects({
      keyword: searchKeyword.value.trim() || undefined,
      status: searchStatus.value || undefined,
    })
    : await listProjects()

  if (!projects.value.some((project) => project.projectId === selectedProjectId.value)) {
    selectedProjectId.value = projects.value[0]?.projectId ?? ''
  }
}

async function loadTasks() {
  tasks.value = selectedProjectId.value ? await listProjectTasks(selectedProjectId.value) : []
  if (!tasks.value.some((task) => task.taskId === selectedTaskId.value)) {
    selectedTaskId.value = tasks.value[0]?.taskId ?? ''
  }
}

async function loadComments() {
  if (!selectedProjectId.value) {
    comments.value = []
    return
  }

  comments.value = commentTaskFilter.value
    ? await listProjectTaskComments(selectedProjectId.value, commentTaskFilter.value)
    : await listProjectComments(selectedProjectId.value)
}

async function loadDirectoryUsers() {
  directoryUsers.value = await listDirectoryUsers()
}

async function refreshCurrentProject() {
  await Promise.all([loadProjectsData(), loadTasks(), loadComments()])
}

async function handleSaveProject() {
  if (!permissions.value.permitWrite || !projectForm.name.trim()) {
    return
  }

  const payload = {
    name: projectForm.name.trim(),
    description: projectForm.description.trim(),
    memberUserIds: projectForm.memberUserIds,
    referenceUserIds: projectForm.referenceUserIds,
    status: projectForm.status,
    progressRate: Number(projectForm.progressRate ?? 0),
  }

  if (projectDialogMode.value === 'edit' && selectedProjectId.value) {
    await updateProject(selectedProjectId.value, payload)
  } else {
    const created = await createProject(payload)
    selectedProjectId.value = created?.projectId ?? ''
  }

  projectDialogVisible.value = false
  await refreshCurrentProject()
  toast.add({ severity: 'success', summary: t('common.summary.saved'), detail: t('common.detail.saved'), life: 2200 })
}

async function handleDeleteProject() {
  if (!permissions.value.permitDelete || !selectedProject.value) {
    return
  }

  confirm.require({
    message: t('common.confirm.deleteMessage', undefined, { name: selectedProject.value.name }),
    header: t('common.confirm.deleteTitle'),
    acceptLabel: t('common.delete'),
    rejectLabel: t('common.cancel'),
    accept: async () => {
      await deleteProject(selectedProject.value!.projectId)
      selectedProjectId.value = ''
      await refreshCurrentProject()
    },
  })
}

async function handleSaveTask() {
  if (!permissions.value.permitWrite || !selectedProjectId.value || !taskForm.title.trim()) {
    return
  }

  const payload = {
    title: taskForm.title.trim(),
    description: taskForm.description.trim(),
    assigneeUserId: taskForm.assigneeUserId || null,
    dueDate: taskForm.dueDate || null,
    status: taskForm.status,
  }

  if (taskDialogMode.value === 'edit' && editingTaskId.value) {
    await updateProjectTask(selectedProjectId.value, editingTaskId.value, payload)
  } else {
    await createProjectTask(selectedProjectId.value, payload)
  }

  taskDialogVisible.value = false
  resetTaskForm()
  await loadTasks()
}

async function handleDeleteTask(taskId: string) {
  if (!permissions.value.permitDelete || !selectedProjectId.value) {
    return
  }

  await deleteProjectTask(selectedProjectId.value, taskId)
  await Promise.all([loadTasks(), loadComments()])
}

async function handleQuickTaskStatus(taskId: string, status: string) {
  if (!permissions.value.permitWrite || !selectedProjectId.value) {
    return
  }

  await updateProjectTaskStatus(selectedProjectId.value, taskId, status)
  await loadTasks()
}

async function handleSaveComment() {
  if (!permissions.value.permitWrite || !selectedProjectId.value || !commentForm.content.trim()) {
    return
  }

  const payload = {
    taskId: commentForm.taskId || null,
    content: commentForm.content.trim(),
  }

  if (commentDialogMode.value === 'edit' && editingCommentId.value) {
    await updateProjectComment(selectedProjectId.value, editingCommentId.value, payload)
  } else {
    await createProjectComment(selectedProjectId.value, payload)
  }

  commentDialogVisible.value = false
  resetCommentForm()
  await loadComments()
}

async function handleDeleteComment(commentId: string) {
  if (!permissions.value.permitDelete || !selectedProjectId.value) {
    return
  }

  await deleteProjectComment(selectedProjectId.value, commentId)
  await loadComments()
}

async function handleQuickProjectStatus(status: string) {
  if (!permissions.value.permitWrite || !selectedProjectId.value) {
    return
  }

  await updateProjectStatus(selectedProjectId.value, status)
  await loadProjectsData()
}

async function handleQuickProgress() {
  if (!permissions.value.permitWrite || !selectedProjectId.value) {
    return
  }

  await updateProjectProgressRate(selectedProjectId.value, Number(projectForm.progressRate ?? 0))
  await loadProjectsData()
}

onMounted(async () => {
  await Promise.all([loadDirectoryUsers(), loadProjectsData()])
  await Promise.all([loadTasks(), loadComments()])
})

watch(selectedProjectId, () => {
  void Promise.all([loadTasks(), loadComments()])
})

watch(commentTaskFilter, () => {
  void loadComments()
})
</script>

<template>
  <div class="page-stack">
    <BasePageHeader :title="title" :description="description" />

    <div class="admin-split-layout">
      <GroupwarePanel title="groupware.projects" description="Create, search, update, and delete visible projects.">
        <template #actions>
          <div class="trade-inline-actions">
            <Button v-if="permissions.permitWrite" size="small" icon="pi pi-plus" :label="t('common.add')" @click="openCreateProject" />
            <Button v-if="permissions.permitWrite" size="small" severity="secondary" :label="t('common.edit')" :disabled="!selectedProjectId" @click="openEditProject" />
            <Button v-if="permissions.permitDelete" size="small" severity="danger" :label="t('common.delete')" :disabled="!selectedProjectId" @click="handleDeleteProject" />
          </div>
        </template>

        <div class="base-search-form__body">
          <div class="base-search-form__fields">
            <label class="inline-input">
              <span>{{ t('common.keyword') }}</span>
              <InputText v-model="searchKeyword" />
            </label>
            <label class="inline-input">
              <span>{{ t('common.status') }}</span>
              <select v-model="searchStatus" class="native-select">
                <option value="">{{ t('common.all') }}</option>
                <option value="PLANNED">PLANNED</option>
                <option value="IN_PROGRESS">IN_PROGRESS</option>
                <option value="DONE">DONE</option>
                <option value="ON_HOLD">ON_HOLD</option>
              </select>
            </label>
          </div>
          <div class="base-search-form__actions">
            <Button :label="t('common.search')" severity="secondary" @click="loadProjectsData" />
            <Button :label="t('common.refresh')" @click="refreshCurrentProject" />
          </div>
        </div>

        <div class="selection-list">
          <button
            v-for="project in projects"
            :key="project.projectId"
            type="button"
            class="selection-list__item"
            :class="{ 'is-active': project.projectId === selectedProjectId }"
            @click="selectedProjectId = project.projectId"
          >
            <strong>{{ project.name }}</strong>
            <span>{{ project.status }} / progress {{ project.progressRate }}%</span>
          </button>
        </div>
      </GroupwarePanel>

      <div class="page-stack">
        <GroupwarePanel :title="focusMode === 'tasks' ? 'groupware.projectTasks' : 'common.project'" description="Project summary with quick status and progress actions.">
          <template #actions>
            <div class="trade-inline-actions">
              <Button v-if="permissions.permitWrite" size="small" icon="pi pi-plus" :label="t('common.add')" :disabled="!selectedProjectId" @click="openCreateTask" />
              <Button v-if="permissions.permitWrite" size="small" severity="secondary" :label="t('groupware.progressRate')" :disabled="!selectedProjectId" @click="handleQuickProgress" />
              <Button v-if="permissions.permitWrite" size="small" severity="secondary" label="DONE" :disabled="!selectedProjectId" @click="handleQuickProjectStatus('DONE')" />
            </div>
          </template>

          <template v-if="projectStats.length">
            <div class="trade-summary-strip">
              <div v-for="item in projectStats" :key="item.label" class="trade-summary-strip__item">
                <span>{{ item.label }}</span>
                <strong>{{ item.value }}</strong>
              </div>
            </div>

            <GroupwareRecordTable :columns="taskColumns" :rows="taskRows" row-key="taskId">
              <template #actions="{ row }">
                <div class="trade-inline-actions">
                  <Button size="small" severity="secondary" :label="t('common.edit')" :disabled="!permissions.permitWrite" @click="openEditTask(String(row.taskId))" />
                  <Button size="small" severity="secondary" label="DONE" :disabled="!permissions.permitWrite" @click="handleQuickTaskStatus(String(row.taskId), 'DONE')" />
                  <Button size="small" severity="danger" :label="t('common.delete')" :disabled="!permissions.permitDelete" @click="handleDeleteTask(String(row.taskId))" />
                </div>
              </template>
            </GroupwareRecordTable>
          </template>
          <BaseEmptyState
            v-else
            title="No Project"
            description="Select a project to inspect related tasks."
          />
        </GroupwarePanel>

        <GroupwarePanel title="groupware.projectComments" description="Comments can be attached to the whole project or a specific task.">
          <template #actions>
            <div class="trade-inline-actions">
              <select v-model="commentTaskFilter" class="native-select">
                <option value="">{{ t('common.all') }}</option>
                <option v-for="task in tasks" :key="task.taskId" :value="task.taskId">{{ task.title }}</option>
              </select>
              <Button v-if="permissions.permitWrite" size="small" icon="pi pi-plus" :label="t('common.add')" :disabled="!selectedProjectId" @click="openCreateComment" />
            </div>
          </template>

          <GroupwareRecordTable :columns="commentColumns" :rows="commentRows" row-key="commentId">
            <template #actions="{ row }">
              <div class="trade-inline-actions">
                <Button size="small" severity="secondary" :label="t('common.edit')" :disabled="!permissions.permitWrite" @click="openEditComment(String(row.commentId))" />
                <Button size="small" severity="danger" :label="t('common.delete')" :disabled="!permissions.permitDelete" @click="handleDeleteComment(String(row.commentId))" />
              </div>
            </template>
          </GroupwareRecordTable>
        </GroupwarePanel>
      </div>
    </div>

    <BaseDialog :visible="projectDialogVisible" :title="projectDialogMode === 'create' ? t('common.add') : t('common.edit')" @update:visible="projectDialogVisible = $event">
      <div class="form-popup-stack">
        <label class="inline-input">
          <span>{{ t('common.title') }}</span>
          <InputText v-model="projectForm.name" />
        </label>
        <label class="inline-input">
          <span>{{ t('common.description') }}</span>
          <textarea v-model="projectForm.description" class="trade-textarea"></textarea>
        </label>
        <label class="inline-input">
          <span>{{ t('common.members') }}</span>
          <select
            multiple
            class="native-select"
            :value="projectForm.memberUserIds"
            @change="projectForm.memberUserIds = Array.from(($event.target as HTMLSelectElement).selectedOptions).map((option) => option.value)"
          >
            <option v-for="user in memberOptions" :key="user.value" :value="user.value">{{ user.label }}</option>
          </select>
        </label>
        <label class="inline-input">
          <span>Reference Users</span>
          <select
            multiple
            class="native-select"
            :value="projectForm.referenceUserIds"
            @change="projectForm.referenceUserIds = Array.from(($event.target as HTMLSelectElement).selectedOptions).map((option) => option.value)"
          >
            <option v-for="user in memberOptions" :key="`ref-${user.value}`" :value="user.value">{{ user.label }}</option>
          </select>
        </label>
        <label class="inline-input">
          <span>{{ t('common.status') }}</span>
          <select v-model="projectForm.status" class="native-select">
            <option value="PLANNED">PLANNED</option>
            <option value="IN_PROGRESS">IN_PROGRESS</option>
            <option value="DONE">DONE</option>
            <option value="ON_HOLD">ON_HOLD</option>
          </select>
        </label>
        <label class="inline-input">
          <span>{{ t('groupware.progressRate') }}</span>
          <InputNumber v-model="projectForm.progressRate" :min="0" :max="100" fluid />
        </label>
        <div class="dialog-actions">
          <Button :label="t('common.close')" severity="secondary" @click="projectDialogVisible = false" />
          <Button :label="t('common.save')" :disabled="!permissions.permitWrite" @click="handleSaveProject" />
        </div>
      </div>
    </BaseDialog>

    <BaseDialog :visible="taskDialogVisible" :title="taskDialogMode === 'create' ? t('common.add') : t('common.edit')" @update:visible="taskDialogVisible = $event">
      <div class="form-popup-stack">
        <label class="inline-input">
          <span>{{ t('common.title') }}</span>
          <InputText v-model="taskForm.title" />
        </label>
        <label class="inline-input">
          <span>{{ t('common.description') }}</span>
          <textarea v-model="taskForm.description" class="trade-textarea"></textarea>
        </label>
        <label class="inline-input">
          <span>{{ t('common.user') }}</span>
          <select v-model="taskForm.assigneeUserId" class="native-select">
            <option value="">{{ t('common.notSelected') }}</option>
            <option v-for="user in memberOptions" :key="user.value" :value="user.value">{{ user.label }}</option>
          </select>
        </label>
        <label class="inline-input">
          <span>Due Date</span>
          <input v-model="taskForm.dueDate" class="native-select" type="date">
        </label>
        <label class="inline-input">
          <span>{{ t('common.status') }}</span>
          <select v-model="taskForm.status" class="native-select">
            <option value="TODO">TODO</option>
            <option value="IN_PROGRESS">IN_PROGRESS</option>
            <option value="DONE">DONE</option>
            <option value="ON_HOLD">ON_HOLD</option>
          </select>
        </label>
        <div class="dialog-actions">
          <Button :label="t('common.close')" severity="secondary" @click="taskDialogVisible = false" />
          <Button :label="taskDialogMode === 'create' ? t('common.create') : t('common.save')" :disabled="!permissions.permitWrite" @click="handleSaveTask" />
        </div>
      </div>
    </BaseDialog>

    <BaseDialog :visible="commentDialogVisible" :title="commentDialogMode === 'create' ? t('common.add') : t('common.edit')" @update:visible="commentDialogVisible = $event">
      <div class="form-popup-stack">
        <label class="inline-input">
          <span>Task</span>
          <select v-model="commentForm.taskId" class="native-select">
            <option value="">{{ t('groupware.projectLevelComment') }}</option>
            <option v-for="task in tasks" :key="task.taskId" :value="task.taskId">{{ task.title }}</option>
          </select>
        </label>
        <label class="inline-input">
          <span>{{ t('common.content') }}</span>
          <textarea v-model="commentForm.content" class="trade-textarea"></textarea>
        </label>
        <div class="dialog-actions">
          <Button :label="t('common.close')" severity="secondary" @click="commentDialogVisible = false" />
          <Button :label="commentDialogMode === 'create' ? t('common.create') : t('common.save')" :disabled="!permissions.permitWrite" @click="handleSaveComment" />
        </div>
      </div>
    </BaseDialog>
  </div>
</template>
