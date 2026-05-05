import type { GroupwareProjectItem, GroupwareProjectTask } from '@/types/groupware'

export const taskColumns = [
  { field: 'title', title: 'Task' },
  { field: 'assigneeUserId', title: 'Assignee' },
  { field: 'dueDate', title: 'Due Date' },
  { field: 'status', title: 'Status' },
]

export const toTaskRows = (tasks: GroupwareProjectTask[]) =>
  tasks.map((task) => ({
    taskId: task.taskId,
    title: task.title,
    assigneeUserId: task.assigneeUserId || '-',
    dueDate: task.dueDate || '-',
    status: task.status,
  }))

export const buildProjectStats = (project: GroupwareProjectItem | null) => {
  if (!project) {
    return []
  }

  return [
    { label: 'Project', value: project.name },
    { label: 'Status', value: project.status },
    { label: 'Progress', value: `${project.progressRate}%` },
  ]
}
