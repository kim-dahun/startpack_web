import type { GroupwareScheduleItem } from '@/types/groupware'

export const scheduleColumns = [
  { field: 'title', title: 'Title' },
  { field: 'projectCode', title: 'Project' },
  { field: 'scope', title: 'Scope' },
  { field: 'startAt', title: 'Start' },
  { field: 'endAt', title: 'End' },
]

export const toScheduleRows = (schedules: GroupwareScheduleItem[]) =>
  schedules.map((item) => ({
    scheduleId: item.scheduleId,
    title: item.title,
    projectCode: item.projectCode || '-',
    scope: item.scope,
    startAt: item.startAt,
    endAt: item.endAt,
  }))
