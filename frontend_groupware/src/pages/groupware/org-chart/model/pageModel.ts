import type { GroupwareDirectoryUser } from '@/types/groupware'

export const directoryColumns = [
  { field: 'userName', title: 'User Name' },
  { field: 'userId', title: 'User ID' },
  { field: 'primaryDepartmentName', title: 'Department' },
  { field: 'primaryPositionName', title: 'Position' },
  { field: 'jobGradeName', title: 'Job Grade' },
  { field: 'source', title: 'Source' },
]

export const filterDirectoryRows = (users: GroupwareDirectoryUser[], keyword: string) => {
  const normalized = keyword.trim().toLowerCase()

  if (!normalized) {
    return users
  }

  return users.filter((user) =>
    [user.userId, user.userName, user.primaryDepartmentName, user.primaryPositionName]
      .some((value) => String(value ?? '').toLowerCase().includes(normalized)),
  )
}
