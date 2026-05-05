import type { GroupwareNotificationItem } from '@/types/groupware'

export const notificationColumns = [
  { field: 'title', title: 'Title' },
  { field: 'status', title: 'Status' },
  { field: 'referenceType', title: 'Reference Type' },
  { field: 'createdAt', title: 'Created At' },
]

export const toNotificationRows = (notifications: GroupwareNotificationItem[]) =>
  notifications.map((item) => ({
    notificationId: item.notificationId,
    title: item.title,
    status: item.status,
    referenceType: item.referenceType || '-',
    createdAt: item.createdAt,
  }))
