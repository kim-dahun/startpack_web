<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import { useToast } from 'primevue/usetoast'

import BaseDialog from '@/components/common/BaseDialog.vue'
import BaseEmptyState from '@/components/common/BaseEmptyState.vue'
import BasePageHeader from '@/components/common/BasePageHeader.vue'
import BaseStatCard from '@/components/common/BaseStatCard.vue'
import { useAppI18n } from '@/composables/useAppI18n'
import GroupwarePanel from '@/components/groupware/GroupwarePanel.vue'
import GroupwareRecordTable from '@/components/groupware/GroupwareRecordTable.vue'
import { useSessionStore } from '@/stores/session'
import type {
  GroupwareApprovalActionHistory,
  GroupwareApprovalDocument,
  GroupwareApprovalLine,
  GroupwareApprovalLineRequest,
  GroupwareApprovalLineTemplate,
  GroupwareDirectoryUser,
} from '@/types/groupware'
import {
  applyApprovalLineTemplate,
  approveApproval,
  createApproval,
  createApprovalLineTemplate,
  deleteApproval,
  deleteApprovalLineTemplate,
  getApprovalActions,
  getApprovalLines,
  listApprovalLineTemplates,
  listApprovals,
  listDirectoryUsers,
  rejectApproval,
  searchApprovals,
  submitApproval,
  updateApproval,
  updateApprovalLineTemplate,
} from './api/api'

const props = defineProps<{
  title: string
  description: string
  viewMode: 'drafts' | 'inbox' | 'lines'
}>()

type DocumentDialogMode = 'create' | 'edit'
type TemplateDialogMode = 'create' | 'edit'

const sessionStore = useSessionStore()
const toast = useToast()
const { t } = useAppI18n()

const permissions = computed(() => sessionStore.getPermissions(sessionStore.persisted.currentMenuId))
const currentUserId = computed(() => sessionStore.persisted.user?.userId ?? '')

const documents = ref<GroupwareApprovalDocument[]>([])
const selectedDocumentId = ref('')
const lines = ref<GroupwareApprovalLine[]>([])
const actions = ref<GroupwareApprovalActionHistory[]>([])
const templates = ref<GroupwareApprovalLineTemplate[]>([])
const directoryUsers = ref<GroupwareDirectoryUser[]>([])
const documentDialogVisible = ref(false)
const templateDialogVisible = ref(false)
const documentDialogMode = ref<DocumentDialogMode>('create')
const templateDialogMode = ref<TemplateDialogMode>('create')
const editingTemplateId = ref('')
const searchKeyword = ref('')
const searchStatus = ref('')
const searchDocumentType = ref('')

const documentForm = reactive({
  title: '',
  content: '',
  documentType: 'GENERAL',
  documentJson: '{}',
  approverUserIds: [] as string[],
  approvalLines: [] as GroupwareApprovalLineRequest[],
})

const templateForm = reactive({
  templateName: '',
  approvalLines: [] as GroupwareApprovalLineRequest[],
})

const selectedDocument = computed(() => filteredDocuments.value.find((item) => item.documentId === selectedDocumentId.value) ?? null)
const filteredDocuments = computed(() => {
  if (props.viewMode === 'drafts') {
    return documents.value.filter((item) => item.drafterUserId === currentUserId.value || ['DRAFT', 'SUBMITTED'].includes(item.status))
  }

  if (props.viewMode === 'inbox') {
    return documents.value.filter((item) => item.status !== 'DRAFT')
  }

  return documents.value
})
const lineRows = computed(() => lines.value.map((line) => ({
  approvalLineId: line.approvalLineId,
  approvalRoleType: line.approvalRoleType,
  lineStage: line.lineStage,
  lineSeq: line.lineSeq,
  targetType: line.targetType,
  targetUserId: line.targetUserId || '-',
  decisionStatus: line.decisionStatus || '-',
})))
const actionRows = computed(() => actions.value.map((item, index) => ({
  actionHistoryId: item.actionHistoryId ?? String(index),
  actionType: item.actionType,
  actionUserId: item.actionUserId || '-',
  actionComment: item.actionComment || '-',
  createdAt: item.createdAt ? item.createdAt.replace('T', ' ').slice(0, 16) : '-',
})))
const templateRows = computed(() => templates.value.map((template) => ({
  templateId: template.templateId,
  templateName: template.templateName,
  createdAt: template.createdAt ? template.createdAt.replace('T', ' ').slice(0, 16) : '-',
})))
const userOptions = computed(() => directoryUsers.value.map((user) => ({
  label: `${user.userName} (${user.userId})`,
  value: user.userId,
})))

const lineColumns = [
  { field: 'approvalRoleType', title: 'Role' },
  { field: 'lineStage', title: 'Stage' },
  { field: 'lineSeq', title: 'Seq' },
  { field: 'targetType', title: 'Target Type' },
  { field: 'targetUserId', title: 'Target User' },
  { field: 'decisionStatus', title: 'Decision' },
]

const actionColumns = [
  { field: 'actionType', title: 'Action' },
  { field: 'actionUserId', title: 'common.user' },
  { field: 'actionComment', title: 'common.content' },
  { field: 'createdAt', title: 'common.createdAt' },
]

const templateColumns = [
  { field: 'templateName', title: 'Template' },
  { field: 'createdAt', title: 'common.createdAt' },
]

function createEmptyLine(lineSeq: number): GroupwareApprovalLineRequest {
  return {
    lineStage: 'PRIMARY',
    lineSeq,
    approvalRoleType: 'APPROVAL',
    decisionMode: 'NORMAL',
    targetType: 'USER',
    targetUserId: '',
    targetDepartmentId: null,
    targetPositionId: null,
  }
}

function resetDocumentForm() {
  documentForm.title = ''
  documentForm.content = ''
  documentForm.documentType = 'GENERAL'
  documentForm.documentJson = '{}'
  documentForm.approverUserIds = []
  documentForm.approvalLines = [createEmptyLine(1)]
}

function resetTemplateForm() {
  templateForm.templateName = ''
  templateForm.approvalLines = [createEmptyLine(1)]
  editingTemplateId.value = ''
}

function openCreateDocument() {
  documentDialogMode.value = 'create'
  resetDocumentForm()
  documentDialogVisible.value = true
}

function openEditDocument() {
  if (!selectedDocument.value) {
    return
  }

  documentDialogMode.value = 'edit'
  documentForm.title = selectedDocument.value.title
  documentForm.content = selectedDocument.value.content ?? ''
  documentForm.documentType = selectedDocument.value.documentType
  documentForm.documentJson = selectedDocument.value.documentJson ?? '{}'
  documentForm.approverUserIds = [...(selectedDocument.value.approverUserIds ?? [])]
  documentForm.approvalLines = lines.value.length
    ? lines.value.map((line) => ({
      lineStage: line.lineStage,
      lineSeq: line.lineSeq,
      approvalRoleType: line.approvalRoleType,
      decisionMode: 'NORMAL',
      targetType: line.targetType,
      targetUserId: line.targetUserId,
      targetDepartmentId: line.targetDepartmentId,
      targetPositionId: line.targetPositionId,
    }))
    : [createEmptyLine(1)]
  documentDialogVisible.value = true
}

function openCreateTemplate() {
  templateDialogMode.value = 'create'
  resetTemplateForm()
  templateDialogVisible.value = true
}

function openEditTemplate(templateId: string) {
  const target = templates.value.find((item) => item.templateId === templateId)
  if (!target) {
    return
  }

  templateDialogMode.value = 'edit'
  editingTemplateId.value = templateId
  templateForm.templateName = target.templateName
  templateForm.approvalLines = [createEmptyLine(1)]
  templateDialogVisible.value = true
}

async function loadDirectoryUsers() {
  directoryUsers.value = await listDirectoryUsers()
}

async function loadDocuments() {
  documents.value = searchKeyword.value.trim() || searchStatus.value || searchDocumentType.value
    ? await searchApprovals({
      keyword: searchKeyword.value.trim() || undefined,
      status: searchStatus.value || undefined,
      documentType: searchDocumentType.value || undefined,
      writerUserId: props.viewMode === 'drafts' ? currentUserId.value : undefined,
    })
    : await listApprovals()

  if (!filteredDocuments.value.some((item) => item.documentId === selectedDocumentId.value)) {
    selectedDocumentId.value = filteredDocuments.value[0]?.documentId ?? ''
  }
}

async function loadLines() {
  if (!selectedDocumentId.value) {
    lines.value = []
    actions.value = []
    return
  }

  const [nextLines, nextActions] = await Promise.all([
    getApprovalLines(selectedDocumentId.value),
    getApprovalActions(selectedDocumentId.value),
  ])
  lines.value = nextLines
  actions.value = nextActions
}

async function loadTemplates() {
  templates.value = await listApprovalLineTemplates()
}

function addDocumentLine() {
  documentForm.approvalLines.push(createEmptyLine(documentForm.approvalLines.length + 1))
}

function addTemplateLine() {
  templateForm.approvalLines.push(createEmptyLine(templateForm.approvalLines.length + 1))
}

function removeDocumentLine(index: number) {
  documentForm.approvalLines.splice(index, 1)
  documentForm.approvalLines.forEach((line, lineIndex) => {
    line.lineSeq = lineIndex + 1
  })
}

function removeTemplateLine(index: number) {
  templateForm.approvalLines.splice(index, 1)
  templateForm.approvalLines.forEach((line, lineIndex) => {
    line.lineSeq = lineIndex + 1
  })
}

async function handleSaveDocument() {
  if (!permissions.value.permitWrite || !documentForm.title.trim()) {
    return
  }

  const approverUserIds = documentForm.approverUserIds.filter(Boolean)
  const approvalLines = documentForm.approvalLines
    .map((line, index) => ({
      ...line,
      lineSeq: index + 1,
      targetUserId: line.targetUserId || null,
    }))
    .filter((line) => Boolean(line.targetUserId))

  const payload = {
    title: documentForm.title.trim(),
    content: documentForm.content.trim(),
    documentType: documentForm.documentType.trim(),
    documentJson: documentForm.documentJson.trim() || '{}',
    approverUserIds,
    approvalLines,
  }

  const document = documentDialogMode.value === 'edit' && selectedDocumentId.value
    ? await updateApproval(selectedDocumentId.value, payload)
    : await createApproval(payload)

  documentDialogVisible.value = false
  selectedDocumentId.value = document?.documentId ?? ''
  await Promise.all([loadDocuments(), loadLines()])
  toast.add({ severity: 'success', summary: t('common.summary.saved'), detail: t('common.detail.saved'), life: 2200 })
}

async function handleDeleteDocument() {
  if (!permissions.value.permitDelete || !selectedDocumentId.value) {
    return
  }

  await deleteApproval(selectedDocumentId.value)
  selectedDocumentId.value = ''
  await Promise.all([loadDocuments(), loadLines()])
}

async function handleSubmitDocument() {
  if (!permissions.value.permitWrite || !selectedDocumentId.value) {
    return
  }

  await submitApproval(selectedDocumentId.value)
  await Promise.all([loadDocuments(), loadLines()])
}

async function handleApproveDocument() {
  if (!permissions.value.permitWrite || !selectedDocumentId.value) {
    return
  }

  await approveApproval(selectedDocumentId.value)
  await Promise.all([loadDocuments(), loadLines()])
}

async function handleRejectDocument() {
  if (!permissions.value.permitWrite || !selectedDocumentId.value) {
    return
  }

  await rejectApproval(selectedDocumentId.value)
  await Promise.all([loadDocuments(), loadLines()])
}

async function handleSaveTemplate() {
  if (!permissions.value.permitWrite || !templateForm.templateName.trim()) {
    return
  }

  const payload = {
    templateName: templateForm.templateName.trim(),
    approvalLines: templateForm.approvalLines.map((line, index) => ({
      ...line,
      lineSeq: index + 1,
      targetUserId: line.targetUserId || null,
    })).filter((line) => Boolean(line.targetUserId)),
  }

  if (templateDialogMode.value === 'edit' && editingTemplateId.value) {
    await updateApprovalLineTemplate(editingTemplateId.value, payload)
  } else {
    await createApprovalLineTemplate(payload)
  }

  templateDialogVisible.value = false
  await loadTemplates()
  toast.add({ severity: 'success', summary: t('common.summary.saved'), detail: t('common.detail.saved'), life: 2200 })
}

async function handleDeleteTemplate(templateId: string) {
  if (!permissions.value.permitDelete) {
    return
  }

  await deleteApprovalLineTemplate(templateId)
  await loadTemplates()
}

async function handleApplyTemplate(templateId: string) {
  if (!permissions.value.permitWrite || !selectedDocumentId.value) {
    return
  }

  lines.value = await applyApprovalLineTemplate(selectedDocumentId.value, templateId)
  await loadDocuments()
  toast.add({
    severity: 'success',
    summary: t('common.summary.applied'),
    detail: t('common.detail.applied'),
    life: 2200,
  })
}

onMounted(async () => {
  resetDocumentForm()
  resetTemplateForm()
  await Promise.all([loadDirectoryUsers(), loadDocuments(), loadTemplates()])
  await loadLines()
})

watch(selectedDocumentId, () => {
  void loadLines()
})
</script>

<template>
  <div class="page-stack">
    <BasePageHeader :title="title" :description="description" />

    <section class="stats-grid">
      <BaseStatCard :label="t('groupware.status.documents')" :value="String(filteredDocuments.length)" />
      <BaseStatCard :label="t('groupware.status.selectedLines')" :value="String(lines.length)" />
      <BaseStatCard :label="t('groupware.status.templates')" :value="String(templates.length)" />
    </section>

    <div class="admin-split-layout">
      <GroupwarePanel title="groupware.documents" description="Create, search, update, submit, approve, reject, and delete documents by state.">
        <template #actions>
          <div class="trade-inline-actions">
            <Button
              v-if="permissions.permitWrite && viewMode !== 'inbox'"
              size="small"
              icon="pi pi-plus"
              :label="t('common.add')"
              @click="openCreateDocument"
            />
            <Button
              v-if="permissions.permitWrite && selectedDocument?.status === 'DRAFT'"
              size="small"
              severity="secondary"
              :label="t('common.edit')"
              @click="openEditDocument"
            />
            <Button
              v-if="permissions.permitDelete && selectedDocument?.status === 'DRAFT'"
              size="small"
              severity="danger"
              :label="t('common.delete')"
              @click="handleDeleteDocument"
            />
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
                <option value="DRAFT">DRAFT</option>
                <option value="SUBMITTED">SUBMITTED</option>
                <option value="APPROVED">APPROVED</option>
                <option value="REJECTED">REJECTED</option>
              </select>
            </label>
            <label class="inline-input">
              <span>{{ t('common.type') }}</span>
              <InputText v-model="searchDocumentType" />
            </label>
          </div>
          <div class="base-search-form__actions">
            <Button :label="t('common.search')" severity="secondary" @click="loadDocuments" />
            <Button :label="t('common.refresh')" @click="loadDocuments" />
          </div>
        </div>

        <div class="selection-list">
          <button
            v-for="document in filteredDocuments"
            :key="document.documentId"
            type="button"
            class="selection-list__item"
            :class="{ 'is-active': document.documentId === selectedDocumentId }"
            @click="selectedDocumentId = document.documentId"
          >
            <strong>{{ document.title }}</strong>
            <span>{{ document.status }} / {{ document.documentType }}</span>
          </button>
        </div>
      </GroupwarePanel>

      <div class="page-stack">
        <GroupwarePanel :title="viewMode === 'lines' ? 'groupware.approvalLines' : 'common.view'" description="Submit, approve, reject, and review approval lines.">
          <template #actions>
            <div class="trade-inline-actions">
              <Button v-if="permissions.permitWrite && selectedDocument?.status === 'DRAFT'" size="small" :label="t('common.save')" @click="handleSubmitDocument" />
              <Button v-if="permissions.permitWrite && selectedDocument?.status === 'SUBMITTED'" size="small" severity="secondary" :label="t('common.apply')" @click="handleApproveDocument" />
              <Button v-if="permissions.permitWrite && selectedDocument?.status === 'SUBMITTED'" size="small" severity="danger" :label="t('common.delete')" @click="handleRejectDocument" />
            </div>
          </template>

          <GroupwareRecordTable v-if="lineRows.length" :columns="lineColumns" :rows="lineRows" row-key="approvalLineId" />
          <BaseEmptyState
            v-else
            title="No Lines"
            description="Select a document to inspect or create approval lines."
          />
        </GroupwarePanel>

        <GroupwarePanel title="groupware.actionsHistory" description="Backend approval action history for the selected document.">
          <GroupwareRecordTable :columns="actionColumns" :rows="actionRows" row-key="actionHistoryId" />
        </GroupwarePanel>

        <GroupwarePanel title="groupware.templates" description="Create reusable templates and apply them to DRAFT documents.">
          <template #actions>
            <Button v-if="permissions.permitWrite" size="small" icon="pi pi-plus" :label="t('common.add')" @click="openCreateTemplate" />
          </template>

          <GroupwareRecordTable :columns="templateColumns" :rows="templateRows" row-key="templateId">
            <template #actions="{ row }">
              <div class="trade-inline-actions">
                <Button
                  size="small"
                  severity="secondary"
                  :label="t('common.apply')"
                  :disabled="!permissions.permitWrite || selectedDocument?.status !== 'DRAFT'"
                  @click="handleApplyTemplate(String(row.templateId))"
                />
                <Button
                  size="small"
                  severity="secondary"
                  :label="t('common.edit')"
                  :disabled="!permissions.permitWrite"
                  @click="openEditTemplate(String(row.templateId))"
                />
                <Button
                  size="small"
                  severity="danger"
                  :label="t('common.delete')"
                  :disabled="!permissions.permitDelete"
                  @click="handleDeleteTemplate(String(row.templateId))"
                />
              </div>
            </template>
          </GroupwareRecordTable>
        </GroupwarePanel>
      </div>
    </div>

    <BaseDialog :visible="documentDialogVisible" :title="documentDialogMode === 'create' ? t('common.add') : t('common.edit')" @update:visible="documentDialogVisible = $event">
      <div class="form-popup-stack">
        <label class="inline-input">
          <span>{{ t('common.title') }}</span>
          <InputText v-model="documentForm.title" />
        </label>
        <label class="inline-input">
          <span>{{ t('common.content') }}</span>
          <textarea v-model="documentForm.content" class="trade-textarea"></textarea>
        </label>
        <label class="inline-input">
          <span>{{ t('common.type') }}</span>
          <InputText v-model="documentForm.documentType" />
        </label>
        <label class="inline-input">
          <span>Document JSON</span>
          <textarea v-model="documentForm.documentJson" class="trade-textarea"></textarea>
        </label>
        <label class="inline-input">
          <span>Approver Users</span>
          <select
            multiple
            class="native-select"
            :value="documentForm.approverUserIds"
            @change="documentForm.approverUserIds = Array.from(($event.target as HTMLSelectElement).selectedOptions).map((option) => option.value)"
          >
            <option v-for="user in userOptions" :key="user.value" :value="user.value">{{ user.label }}</option>
          </select>
        </label>

        <div class="plain-list">
          <strong>{{ t('groupware.approvalLines') }}</strong>
          <div v-for="(line, index) in documentForm.approvalLines" :key="`doc-line-${index}`" class="groupware-line-editor">
            <select v-model="line.approvalRoleType" class="native-select">
              <option value="APPROVAL">APPROVAL</option>
              <option value="CONSULT">CONSULT</option>
              <option value="REFERENCE">REFERENCE</option>
            </select>
            <select v-model="line.targetUserId" class="native-select">
              <option value="">{{ t('common.notSelected') }}</option>
              <option v-for="user in userOptions" :key="user.value" :value="user.value">{{ user.label }}</option>
            </select>
            <Button size="small" severity="danger" icon="pi pi-trash" :disabled="documentForm.approvalLines.length === 1" @click="removeDocumentLine(index)" />
          </div>
          <Button size="small" severity="secondary" :label="t('common.add')" @click="addDocumentLine" />
        </div>

        <div class="dialog-actions">
          <Button :label="t('common.close')" severity="secondary" @click="documentDialogVisible = false" />
          <Button :label="documentDialogMode === 'create' ? t('common.create') : t('common.save')" :disabled="!permissions.permitWrite" @click="handleSaveDocument" />
        </div>
      </div>
    </BaseDialog>

    <BaseDialog :visible="templateDialogVisible" :title="templateDialogMode === 'create' ? t('common.add') : t('common.edit')" @update:visible="templateDialogVisible = $event">
      <div class="form-popup-stack">
        <label class="inline-input">
          <span>Template Name</span>
          <InputText v-model="templateForm.templateName" />
        </label>
        <div class="plain-list">
          <strong>{{ t('groupware.templates') }}</strong>
          <div v-for="(line, index) in templateForm.approvalLines" :key="`tpl-line-${index}`" class="groupware-line-editor">
            <select v-model="line.approvalRoleType" class="native-select">
              <option value="APPROVAL">APPROVAL</option>
              <option value="CONSULT">CONSULT</option>
              <option value="REFERENCE">REFERENCE</option>
            </select>
            <select v-model="line.targetUserId" class="native-select">
              <option value="">{{ t('common.notSelected') }}</option>
              <option v-for="user in userOptions" :key="user.value" :value="user.value">{{ user.label }}</option>
            </select>
            <Button size="small" severity="danger" icon="pi pi-trash" :disabled="templateForm.approvalLines.length === 1" @click="removeTemplateLine(index)" />
          </div>
          <Button size="small" severity="secondary" :label="t('common.add')" @click="addTemplateLine" />
        </div>
        <div class="dialog-actions">
          <Button :label="t('common.close')" severity="secondary" @click="templateDialogVisible = false" />
          <Button :label="templateDialogMode === 'create' ? t('common.create') : t('common.save')" :disabled="!permissions.permitWrite" @click="handleSaveTemplate" />
        </div>
      </div>
    </BaseDialog>
  </div>
</template>
