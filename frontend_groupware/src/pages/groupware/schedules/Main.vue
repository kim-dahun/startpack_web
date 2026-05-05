<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import Button from 'primevue/button'
import Checkbox from 'primevue/checkbox'
import InputNumber from 'primevue/inputnumber'
import InputText from 'primevue/inputtext'
import { useConfirm } from 'primevue/useconfirm'
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
  GroupwareProjectItem,
  GroupwareScheduleCreateRequest,
  GroupwareScheduleItem,
  GroupwareScheduleOccurrence,
  GroupwareScheduleRecurrenceRule,
} from '@/types/groupware'
import {
  createSchedule,
  createScheduleOccurrenceExclusion,
  deleteSchedule,
  deleteScheduleRecurrence,
  getScheduleRecurrence,
  listProjects,
  listScheduleOccurrences,
  listSchedules,
  listSchedulesByProjectCode,
  saveScheduleRecurrence,
  searchSchedules,
  updateSchedule,
  updateScheduleRecurrence,
} from './api/api'

function parseDateTime(value?: string | null) {
  if (!value) {
    return null
  }

  const normalized = value.length === 16 ? `${value}:00` : value
  const date = new Date(normalized)
  return Number.isNaN(date.getTime()) ? null : date
}

function normalizeDateTimeLocal(value: string) {
  return value.slice(0, 16)
}

function normalizeBackendDateTime(value: string) {
  return value.length === 16 ? `${value}:00` : value
}

function toDateTimeLocalString(date: Date) {
  const next = new Date(date)
  next.setMilliseconds(0)
  return `${next.getFullYear()}-${String(next.getMonth() + 1).padStart(2, '0')}-${String(next.getDate()).padStart(2, '0')}T${String(next.getHours()).padStart(2, '0')}:${String(next.getMinutes()).padStart(2, '0')}`
}

function formatDateTime(value: string) {
  return value.replace('T', ' ').slice(0, 16)
}

function addHours(date: Date, hours: number) {
  const next = new Date(date)
  next.setHours(next.getHours() + hours)
  return next
}

function addMonths(date: Date, months: number) {
  const next = new Date(date)
  next.setMonth(next.getMonth() + months)
  return next
}

function startOfMonth(date: Date) {
  return new Date(date.getFullYear(), date.getMonth(), 1)
}

function endOfMonth(date: Date) {
  return new Date(date.getFullYear(), date.getMonth() + 1, 0, 23, 59, 59)
}

function startOfCalendar(date: Date) {
  const first = startOfMonth(date)
  const result = new Date(first)
  result.setDate(first.getDate() - first.getDay())
  return result
}

function endOfCalendar(date: Date) {
  const last = endOfMonth(date)
  const result = new Date(last)
  result.setDate(last.getDate() + (6 - last.getDay()))
  return result
}

function toDateKey(date: Date) {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

function includesDate(item: { startAt: string; endAt: string }, date: Date) {
  const start = parseDateTime(item.startAt)
  const end = parseDateTime(item.endAt)

  if (!start || !end) {
    return false
  }

  const startKey = toDateKey(start)
  const endKey = toDateKey(end)
  const targetKey = toDateKey(date)
  return startKey <= targetKey && endKey >= targetKey
}

function diffDays(start: Date, end: Date) {
  const normalizedStart = new Date(start.getFullYear(), start.getMonth(), start.getDate())
  const normalizedEnd = new Date(end.getFullYear(), end.getMonth(), end.getDate())
  return Math.floor((normalizedEnd.getTime() - normalizedStart.getTime()) / 86400000)
}

function clampDayIndex(value: number, min: number, max: number) {
  return Math.min(Math.max(value, min), max)
}

function addDaysString(value: string, days: number) {
  const parsed = parseDateTime(value) ?? new Date()
  parsed.setDate(parsed.getDate() + days)
  return normalizeBackendDateTime(toDateTimeLocalString(parsed))
}

const props = defineProps<{
  title: string
  description: string
  projectMode: boolean
}>()

type ScheduleViewMode = 'list' | 'calendar' | 'gantt'
type ScheduleDialogMode = 'create' | 'edit'

const sessionStore = useSessionStore()
const toast = useToast()
const confirm = useConfirm()
const { t } = useAppI18n()

const permissions = computed(() => sessionStore.getPermissions(sessionStore.persisted.currentMenuId))

const projects = ref<GroupwareProjectItem[]>([])
const selectedProjectCode = ref('')
const from = ref('')
const to = ref('')
const keyword = ref('')
const scope = ref('')
const schedules = ref<GroupwareScheduleItem[]>([])
const selectedScheduleId = ref('')
const currentMonth = ref(new Date())
const viewMode = ref<ScheduleViewMode>('list')
const loading = ref(false)
const dialogVisible = ref(false)
const dialogMode = ref<ScheduleDialogMode>('create')
const recurrenceVisible = ref(false)
const recurrenceLoading = ref(false)
const recurrenceOccurrences = ref<GroupwareScheduleOccurrence[]>([])
const exclusionReason = ref('')
const selectedOccurrenceDate = ref('')

const scheduleForm = reactive({
  title: '',
  projectId: '',
  projectCode: '',
  memo: '',
  startAt: '',
  endAt: '',
  allDay: false,
  scope: 'PERSONAL' as 'PERSONAL' | 'COMPANY',
})

const recurrenceForm = reactive<GroupwareScheduleRecurrenceRule>({
  frequency: 'DAILY',
  intervalValue: 1,
  untilDate: '',
  countLimit: null,
})

const projectOptions = computed(() =>
  projects.value.map((project) => ({
    label: project.projectCode || project.name,
    value: project.projectCode || '',
    projectId: project.projectId,
  })).filter((project) => Boolean(project.value)),
)

const selectedSchedule = computed(() =>
  schedules.value.find((item) => item.scheduleId === selectedScheduleId.value) ?? null,
)

const stats = computed(() => [
  { label: t('groupware.status.schedules'), value: String(schedules.value.length) },
  { label: t('groupware.status.projectFilter'), value: props.projectMode ? (selectedProjectCode.value || t('common.notSelected')) : t('common.all') },
  { label: t('groupware.status.companyScope'), value: String(schedules.value.filter((item) => item.scope === 'COMPANY').length) },
])

const scheduleRows = computed(() => schedules.value.map((item) => ({
  scheduleId: item.scheduleId,
  title: item.title,
  projectCode: item.projectCode || '-',
  scope: item.scope,
  startAt: formatDateTime(item.startAt),
  endAt: formatDateTime(item.endAt),
  allDay: item.allDay ? 'Y' : 'N',
})) )

const listColumns = [
  { field: 'title', title: 'common.title' },
  { field: 'projectCode', title: 'common.project' },
  { field: 'scope', title: 'common.scope' },
  { field: 'startAt', title: 'common.start' },
  { field: 'endAt', title: 'common.end' },
  { field: 'allDay', title: 'All Day' },
]

const selectedProject = computed(() =>
  projects.value.find((project) => (project.projectCode || '') === selectedProjectCode.value) ?? null,
)

const rangeDays = computed(() => {
  const start = parseDateTime(from.value) ?? startOfMonth(currentMonth.value)
  const end = parseDateTime(to.value) ?? endOfMonth(currentMonth.value)
  const days: Date[] = []
  const cursor = new Date(start)

  while (cursor <= end) {
    days.push(new Date(cursor))
    cursor.setDate(cursor.getDate() + 1)
  }

  return days
})

const calendarDays = computed(() => {
  const start = startOfCalendar(currentMonth.value)
  const end = endOfCalendar(currentMonth.value)
  const cells: Array<{
    key: string
    date: Date
    label: number
    inMonth: boolean
    items: GroupwareScheduleItem[]
  }> = []
  const cursor = new Date(start)

  while (cursor <= end) {
    const key = toDateKey(cursor)
    cells.push({
      key,
      date: new Date(cursor),
      label: cursor.getDate(),
      inMonth: cursor.getMonth() === currentMonth.value.getMonth(),
      items: schedules.value.filter((item) => includesDate(item, cursor)),
    })
    cursor.setDate(cursor.getDate() + 1)
  }

  return cells
})

const ganttRows = computed(() => {
  const firstDay = rangeDays.value[0]
  const dayCount = Math.max(rangeDays.value.length, 1)

  return schedules.value.map((item) => {
    const start = parseDateTime(item.startAt) ?? firstDay
    const end = parseDateTime(item.endAt) ?? start
    const startIndex = clampDayIndex(diffDays(firstDay, start), 0, dayCount - 1)
    const endIndex = clampDayIndex(diffDays(firstDay, end), startIndex, dayCount - 1)
    const left = (startIndex / dayCount) * 100
    const width = ((endIndex - startIndex + 1) / dayCount) * 100

    return {
      ...item,
      left,
      width: Math.max(width, 4),
    }
  })
})

function resetForm() {
  scheduleForm.title = ''
  scheduleForm.projectId = ''
  scheduleForm.projectCode = props.projectMode ? selectedProjectCode.value : ''
  scheduleForm.memo = ''
  scheduleForm.startAt = toDateTimeLocalString(new Date())
  scheduleForm.endAt = toDateTimeLocalString(addHours(new Date(), 1))
  scheduleForm.allDay = false
  scheduleForm.scope = 'PERSONAL'
}

function resetRecurrenceForm() {
  recurrenceForm.frequency = 'DAILY'
  recurrenceForm.intervalValue = 1
  recurrenceForm.untilDate = ''
  recurrenceForm.countLimit = null
  exclusionReason.value = ''
  selectedOccurrenceDate.value = ''
}

function populateForm(item: GroupwareScheduleItem) {
  scheduleForm.title = item.title
  scheduleForm.projectId = item.projectId ?? ''
  scheduleForm.projectCode = item.projectCode ?? ''
  scheduleForm.memo = item.memo ?? ''
  scheduleForm.startAt = normalizeDateTimeLocal(item.startAt)
  scheduleForm.endAt = normalizeDateTimeLocal(item.endAt)
  scheduleForm.allDay = Boolean(item.allDay)
  scheduleForm.scope = item.scope === 'COMPANY' ? 'COMPANY' : 'PERSONAL'
}

function buildSchedulePayload(): GroupwareScheduleCreateRequest {
  const selectedProjectOption = projectOptions.value.find((option) => option.value === scheduleForm.projectCode)

  return {
    title: scheduleForm.title.trim(),
    projectId: scheduleForm.projectId || selectedProjectOption?.projectId || null,
    projectCode: scheduleForm.projectCode || null,
    memo: scheduleForm.memo.trim(),
    startAt: normalizeBackendDateTime(scheduleForm.startAt),
    endAt: normalizeBackendDateTime(scheduleForm.endAt),
    allDay: scheduleForm.allDay,
    scope: scheduleForm.scope,
  }
}

function openCreateDialog(seedDate?: Date) {
  dialogMode.value = 'create'
  resetForm()

  if (seedDate) {
    scheduleForm.startAt = toDateTimeLocalString(seedDate)
    scheduleForm.endAt = toDateTimeLocalString(addHours(seedDate, 1))
  }

  if (props.projectMode && selectedProject.value) {
    scheduleForm.projectCode = selectedProject.value.projectCode ?? ''
    scheduleForm.projectId = selectedProject.value.projectId
  }

  dialogVisible.value = true
}

function openEditDialog(item: GroupwareScheduleItem) {
  dialogMode.value = 'edit'
  selectedScheduleId.value = item.scheduleId
  populateForm(item)
  dialogVisible.value = true
}

async function loadProjects() {
  projects.value = await listProjects()
  if (!projects.value.some((project) => project.projectCode === selectedProjectCode.value)) {
    selectedProjectCode.value = projects.value.find((project) => Boolean(project.projectCode))?.projectCode ?? ''
  }
}

async function loadSchedules() {
  loading.value = true

  try {
    if (props.projectMode) {
      schedules.value = selectedProjectCode.value ? await listSchedulesByProjectCode(selectedProjectCode.value) : []
    } else if (keyword.value.trim() || scope.value || selectedProjectCode.value || from.value || to.value) {
      schedules.value = await searchSchedules({
        keyword: keyword.value.trim() || undefined,
        scope: scope.value || undefined,
        projectCode: selectedProjectCode.value || undefined,
        from: from.value ? normalizeBackendDateTime(from.value) : undefined,
        to: to.value ? normalizeBackendDateTime(to.value) : undefined,
      })
    } else {
      schedules.value = await listSchedules({
        from: from.value ? normalizeBackendDateTime(from.value) : undefined,
        to: to.value ? normalizeBackendDateTime(to.value) : undefined,
      })
    }

    if (!schedules.value.some((item) => item.scheduleId === selectedScheduleId.value)) {
      selectedScheduleId.value = schedules.value[0]?.scheduleId ?? ''
    }
  } finally {
    loading.value = false
  }
}

async function handleSaveSchedule() {
  if (!permissions.value.permitWrite) {
    return
  }

  if (!scheduleForm.title.trim() || !scheduleForm.startAt || !scheduleForm.endAt) {
    toast.add({
      severity: 'warn',
      summary: t('common.requiredFields'),
      detail: t('common.requiredFieldsDetail'),
      life: 2200,
    })
    return
  }

  const payload = buildSchedulePayload()

  if (dialogMode.value === 'edit' && selectedScheduleId.value) {
    await updateSchedule(selectedScheduleId.value, payload)
  } else {
    const created = await createSchedule(payload)
    selectedScheduleId.value = created?.scheduleId ?? ''
  }

  dialogVisible.value = false
  await loadSchedules()
  toast.add({
    severity: 'success',
    summary: t('common.summary.saved'),
    detail: t('common.detail.saved'),
    life: 2200,
  })
}

function handleDeleteSchedule(item: GroupwareScheduleItem) {
  if (!permissions.value.permitDelete) {
    return
  }

  confirm.require({
    message: t('common.confirm.deleteMessage', undefined, { name: item.title }),
    header: t('common.confirm.deleteTitle'),
    icon: 'pi pi-exclamation-triangle',
    acceptLabel: t('common.delete'),
    rejectLabel: t('common.cancel'),
    accept: async () => {
      await deleteSchedule(item.scheduleId)
      if (selectedScheduleId.value === item.scheduleId) {
        selectedScheduleId.value = ''
      }
      await loadSchedules()
      toast.add({
        severity: 'success',
        summary: t('common.summary.deleted'),
        detail: t('common.detail.deleted'),
        life: 2200,
      })
    },
  })
}

async function openRecurrenceDialog(item: GroupwareScheduleItem) {
  selectedScheduleId.value = item.scheduleId
  recurrenceVisible.value = true
  recurrenceLoading.value = true

  try {
    resetRecurrenceForm()
    const [rule, occurrences] = await Promise.all([
      getScheduleRecurrence(item.scheduleId),
      listScheduleOccurrences(item.scheduleId, {
        from: item.startAt,
        to: addDaysString(item.endAt, 30),
      }),
    ])

    if (rule) {
      recurrenceForm.frequency = rule.frequency
      recurrenceForm.intervalValue = rule.intervalValue
      recurrenceForm.untilDate = rule.untilDate ?? ''
      recurrenceForm.countLimit = rule.countLimit ?? null
    }

    recurrenceOccurrences.value = occurrences
  } finally {
    recurrenceLoading.value = false
  }
}

async function handleSaveRecurrence() {
  if (!permissions.value.permitWrite || !selectedScheduleId.value) {
    return
  }

  const payload = {
    frequency: recurrenceForm.frequency,
    intervalValue: Number(recurrenceForm.intervalValue ?? 1),
    untilDate: recurrenceForm.untilDate || null,
    countLimit: recurrenceForm.countLimit ? Number(recurrenceForm.countLimit) : null,
  }

  if (selectedSchedule.value && recurrenceOccurrences.value.length) {
    await updateScheduleRecurrence(selectedScheduleId.value, payload)
  } else {
    await saveScheduleRecurrence(selectedScheduleId.value, payload)
  }

  recurrenceVisible.value = false
  toast.add({
    severity: 'success',
    summary: t('common.summary.saved'),
    detail: t('common.detail.saved'),
    life: 2200,
  })
}

async function handleDeleteRecurrence() {
  if (!permissions.value.permitDelete || !selectedScheduleId.value) {
    return
  }

  await deleteScheduleRecurrence(selectedScheduleId.value)
  recurrenceOccurrences.value = []
  resetRecurrenceForm()
}

async function handleExcludeOccurrence() {
  if (!permissions.value.permitWrite || !selectedScheduleId.value || !selectedOccurrenceDate.value) {
    return
  }

  await createScheduleOccurrenceExclusion(selectedScheduleId.value, {
    occurrenceDate: selectedOccurrenceDate.value,
    reason: exclusionReason.value.trim(),
  })

  recurrenceOccurrences.value = await listScheduleOccurrences(selectedScheduleId.value, {
    from: selectedSchedule.value?.startAt,
    to: selectedSchedule.value ? addDaysString(selectedSchedule.value.endAt, 30) : undefined,
  })
}

function handleProjectCodeChange(nextProjectCode: string) {
  selectedProjectCode.value = nextProjectCode
  const match = projectOptions.value.find((project) => project.value === nextProjectCode)
  scheduleForm.projectCode = nextProjectCode
  scheduleForm.projectId = match?.projectId ?? ''
}

onMounted(async () => {
  resetForm()
  await loadProjects()
  await loadSchedules()
})

watch(selectedProjectCode, () => {
  if (props.projectMode) {
    void loadSchedules()
  }
})
</script>

<template>
  <div class="page-stack">
    <BasePageHeader :title="title" :description="description" />

    <section class="stats-grid">
      <BaseStatCard v-for="item in stats" :key="item.label" :label="item.label" :value="item.value" />
    </section>

    <GroupwarePanel title="groupware.scheduleControls" description="Project-linked schedule CRUD with list, calendar, and gantt views.">
      <template #actions>
        <div class="trade-inline-actions">
          <Button
            v-for="mode in ['list', 'calendar', 'gantt']"
            :key="mode"
            size="small"
            :severity="viewMode === mode ? 'contrast' : 'secondary'"
            :label="t(`groupware.${mode === 'list' ? 'scheduleList' : mode === 'calendar' ? 'calendarView' : 'ganttView'}`)"
            @click="viewMode = mode as ScheduleViewMode"
          />
          <Button
            v-if="permissions.permitWrite"
            icon="pi pi-plus"
            :label="t('common.add')"
            size="small"
            @click="openCreateDialog()"
          />
        </div>
      </template>

      <div class="base-search-form__body">
        <div class="base-search-form__fields">
          <label class="inline-input">
            <span>{{ t('common.keyword') }}</span>
            <InputText v-model="keyword" />
          </label>
          <label v-if="projectMode || projectOptions.length" class="inline-input">
            <span>{{ t('common.project') }}</span>
            <select
              :value="selectedProjectCode"
              class="native-select"
              @change="selectedProjectCode = String(($event.target as HTMLSelectElement).value)"
            >
              <option value="">{{ t('common.all') }}</option>
              <option v-for="project in projectOptions" :key="project.value" :value="project.value">
                {{ project.label }}
              </option>
            </select>
          </label>
          <label class="inline-input">
            <span>{{ t('common.scope') }}</span>
            <select v-model="scope" class="native-select">
              <option value="">{{ t('common.all') }}</option>
              <option value="PERSONAL">PERSONAL</option>
              <option value="COMPANY">COMPANY</option>
            </select>
          </label>
          <label class="inline-input">
            <span>{{ t('common.from') }}</span>
            <input v-model="from" class="native-select" type="datetime-local">
          </label>
          <label class="inline-input">
            <span>{{ t('common.to') }}</span>
            <input v-model="to" class="native-select" type="datetime-local">
          </label>
        </div>
        <div class="base-search-form__actions">
          <Button :label="t('common.search')" severity="secondary" @click="loadSchedules" />
          <Button :label="t('groupware.thisMonth')" severity="secondary" @click="currentMonth = new Date(); from = ''; to = ''; void loadSchedules()" />
        </div>
      </div>
    </GroupwarePanel>

    <GroupwarePanel v-if="viewMode === 'list'" title="groupware.scheduleList" description="Row-level create, edit, delete, and recurrence management.">
      <GroupwareRecordTable :columns="listColumns" :rows="scheduleRows" row-key="scheduleId">
        <template #actions="{ row }">
          <div class="trade-inline-actions">
            <Button size="small" severity="secondary" :label="t('common.edit')" :disabled="!permissions.permitWrite" @click="openEditDialog(schedules.find((item) => item.scheduleId === row.scheduleId)!)" />
            <Button size="small" severity="secondary" :label="t('groupware.recurrence')" :disabled="!permissions.permitWrite" @click="openRecurrenceDialog(schedules.find((item) => item.scheduleId === row.scheduleId)!)" />
            <Button size="small" severity="danger" :label="t('common.delete')" :disabled="!permissions.permitDelete" @click="handleDeleteSchedule(schedules.find((item) => item.scheduleId === row.scheduleId)!)" />
          </div>
        </template>
      </GroupwareRecordTable>
    </GroupwarePanel>

    <GroupwarePanel v-else-if="viewMode === 'calendar'" title="groupware.calendarView" description="Monthly calendar with direct schedule create/edit access.">
      <template #actions>
        <div class="trade-inline-actions">
          <Button icon="pi pi-angle-left" size="small" severity="secondary" @click="currentMonth = addMonths(currentMonth, -1)" />
          <span class="trade-chip-static">{{ currentMonth.getFullYear() }}-{{ String(currentMonth.getMonth() + 1).padStart(2, '0') }}</span>
          <Button icon="pi pi-angle-right" size="small" severity="secondary" @click="currentMonth = addMonths(currentMonth, 1)" />
        </div>
      </template>

      <div class="groupware-calendar">
        <div class="groupware-calendar__weekday" v-for="weekday in ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']" :key="weekday">
          {{ weekday }}
        </div>
        <button
          v-for="cell in calendarDays"
          :key="cell.key"
          type="button"
          class="groupware-calendar__cell"
          :class="{ 'is-outside': !cell.inMonth }"
          @dblclick="permissions.permitWrite ? openCreateDialog(cell.date) : null"
        >
          <span class="groupware-calendar__date">{{ cell.label }}</span>
          <div class="groupware-calendar__events">
            <button
              v-for="item in cell.items.slice(0, 3)"
              :key="item.scheduleId"
              type="button"
              class="groupware-calendar__event"
              @click.stop="openEditDialog(item)"
            >
              {{ item.title }}
            </button>
            <span v-if="cell.items.length > 3" class="muted">+{{ cell.items.length - 3 }} more</span>
          </div>
        </button>
      </div>
    </GroupwarePanel>

    <GroupwarePanel v-else title="groupware.ganttView" description="Timeline-style schedule view using the current date range.">
      <div v-if="rangeDays.length" class="groupware-gantt">
        <div class="groupware-gantt__header">
          <span v-for="day in rangeDays" :key="toDateKey(day)">{{ String(day.getMonth() + 1).padStart(2, '0') }}/{{ String(day.getDate()).padStart(2, '0') }}</span>
        </div>
        <div v-for="item in ganttRows" :key="item.scheduleId" class="groupware-gantt__row">
          <div class="groupware-gantt__label">
            <strong>{{ item.title }}</strong>
            <span>{{ item.projectCode || '-' }}</span>
          </div>
          <div class="groupware-gantt__track">
            <button
              type="button"
              class="groupware-gantt__bar"
              :style="{ left: `${item.left}%`, width: `${item.width}%` }"
              @click="openEditDialog(item)"
            >
              {{ item.scope }}
            </button>
          </div>
        </div>
      </div>
      <BaseEmptyState
        v-else
        title="No Range"
        description="Set a date range to render the gantt view."
      />
    </GroupwarePanel>

    <BaseDialog :visible="dialogVisible" :title="dialogMode === 'create' ? t('common.add') : t('common.edit')" @update:visible="dialogVisible = $event">
      <div class="form-popup-stack">
        <label class="inline-input">
          <span>{{ t('common.title') }}</span>
          <InputText v-model="scheduleForm.title" />
        </label>
        <label class="inline-input">
          <span>{{ t('common.project') }}</span>
          <select :value="scheduleForm.projectCode" class="native-select" @change="handleProjectCodeChange(String(($event.target as HTMLSelectElement).value))">
            <option value="">{{ t('common.notSelected') }}</option>
            <option v-for="project in projectOptions" :key="project.value" :value="project.value">{{ project.label }}</option>
          </select>
        </label>
        <label class="inline-input">
          <span>{{ t('common.start') }}</span>
          <input v-model="scheduleForm.startAt" class="native-select" type="datetime-local">
        </label>
        <label class="inline-input">
          <span>{{ t('common.end') }}</span>
          <input v-model="scheduleForm.endAt" class="native-select" type="datetime-local">
        </label>
        <label class="inline-input">
          <span>{{ t('common.memo') }}</span>
          <textarea v-model="scheduleForm.memo" class="trade-textarea"></textarea>
        </label>
        <label class="inline-input">
          <span>{{ t('common.scope') }}</span>
          <select v-model="scheduleForm.scope" class="native-select">
            <option value="PERSONAL">PERSONAL</option>
            <option value="COMPANY">COMPANY</option>
          </select>
        </label>
        <label class="checkbox-line">
          <Checkbox v-model="scheduleForm.allDay" binary />
          <span>All Day</span>
        </label>
        <div class="dialog-actions">
          <Button :label="t('common.close')" severity="secondary" @click="dialogVisible = false" />
          <Button :label="t('common.save')" :disabled="!permissions.permitWrite" @click="handleSaveSchedule" />
        </div>
      </div>
    </BaseDialog>

    <BaseDialog :visible="recurrenceVisible" :title="t('groupware.recurrence')" @update:visible="recurrenceVisible = $event">
      <div class="form-popup-stack">
        <label class="inline-input">
          <span>Frequency</span>
          <select v-model="recurrenceForm.frequency" class="native-select">
            <option value="DAILY">DAILY</option>
            <option value="WEEKLY">WEEKLY</option>
            <option value="MONTHLY">MONTHLY</option>
            <option value="YEARLY">YEARLY</option>
          </select>
        </label>
        <label class="inline-input">
          <span>Interval</span>
          <InputNumber v-model="recurrenceForm.intervalValue" :min="1" fluid />
        </label>
        <label class="inline-input">
          <span>Until Date</span>
          <input v-model="recurrenceForm.untilDate" class="native-select" type="date">
        </label>
        <label class="inline-input">
          <span>Count Limit</span>
          <InputNumber v-model="recurrenceForm.countLimit" :min="1" fluid />
        </label>

        <div class="plain-list">
          <strong>{{ t('groupware.occurrences') }}</strong>
          <span v-if="recurrenceLoading">{{ t('common.loading') }}</span>
          <button
            v-for="item in recurrenceOccurrences"
            :key="item.occurrenceId"
            type="button"
            class="selection-list__item"
            :class="{ 'is-active': selectedOccurrenceDate === item.startAt.slice(0, 10) }"
            @click="selectedOccurrenceDate = item.startAt.slice(0, 10)"
          >
            {{ formatDateTime(item.startAt) }} -> {{ formatDateTime(item.endAt) }}
          </button>
        </div>

        <label class="inline-input">
          <span>{{ t('groupware.excludeOccurrence') }}</span>
          <InputText v-model="exclusionReason" />
        </label>

        <div class="dialog-actions">
          <Button :label="t('common.delete')" severity="danger" :disabled="!permissions.permitDelete" @click="handleDeleteRecurrence" />
          <Button :label="t('groupware.excludeOccurrence')" severity="secondary" :disabled="!permissions.permitWrite || !selectedOccurrenceDate" @click="handleExcludeOccurrence" />
          <Button :label="t('common.close')" severity="secondary" @click="recurrenceVisible = false" />
          <Button :label="t('common.save')" :disabled="!permissions.permitWrite" @click="handleSaveRecurrence" />
        </div>
      </div>
    </BaseDialog>
  </div>
</template>
