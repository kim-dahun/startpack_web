import http from '@/api/client/http'
import type { ApiEnvelope } from '@/types/app'
import type {
  GroupwareAmountSummary,
  GroupwareApprovalActionHistory,
  GroupwareApprovalCreateRequest,
  GroupwareApprovalDocument,
  GroupwareApprovalLine,
  GroupwareApprovalLineRequest,
  GroupwareApprovalLineTemplate,
  GroupwareApprovalLineTemplateCreateRequest,
  GroupwareApprovalLineTemplateItem,
  GroupwareApprovalSearchParams,
  GroupwareArchivePurgeResponse,
  GroupwareChatMessage,
  GroupwareChatMessageCreateRequest,
  GroupwareChatRoom,
  GroupwareChatRoomCreateRequest,
  GroupwareChatRoomMember,
  GroupwareChatRoomUpdateRequest,
  GroupwareCostAccount,
  GroupwareCostAccountCreateRequest,
  GroupwareCostItem,
  GroupwareCostItemCreateRequest,
  GroupwareDirectoryUser,
  GroupwareNotificationCreateRequest,
  GroupwareNotificationItem,
  GroupwareNotificationSearchParams,
  GroupwarePageSlice,
  GroupwareProjectComment,
  GroupwareProjectCommentCreateRequest,
  GroupwareProjectCreateRequest,
  GroupwareProjectItem,
  GroupwareProjectSearchParams,
  GroupwareProjectTask,
  GroupwareProjectTaskCreateRequest,
  GroupwareReadRequest,
  GroupwareRealtimeStatus,
  GroupwareScheduleCost,
  GroupwareScheduleCostCreateRequest,
  GroupwareScheduleCostSearchParams,
  GroupwareScheduleCreateRequest,
  GroupwareScheduleItem,
  GroupwareScheduleOccurrence,
  GroupwareScheduleOccurrenceExclusion,
  GroupwareScheduleOccurrenceExclusionRequest,
  GroupwareScheduleRecurrenceRule,
  GroupwareScheduleSearchParams,
  GroupwareUnreadCount,
  GroupwareUpdatedCount,
} from '@/types/groupware'

const GROUPWARE_BASE_PATH = '/api/groupware'

const unwrap = <T>(response: { data: ApiEnvelope<T> }) => response.data.data

export const listNotifications = async (status?: 'UNREAD' | 'READ') => {
  const response = await http.get<ApiEnvelope<GroupwareNotificationItem[]>>(`${GROUPWARE_BASE_PATH}/notifications`, {
    params: status ? { status } : {},
  })
  return unwrap(response) ?? []
}

export const searchNotifications = async (params: GroupwareNotificationSearchParams) => {
  const response = await http.get<ApiEnvelope<GroupwareNotificationItem[]>>(`${GROUPWARE_BASE_PATH}/notifications/search`, {
    params,
  })
  return unwrap(response) ?? []
}

export const getUnreadNotificationCount = async () => {
  const response = await http.get<ApiEnvelope<GroupwareUnreadCount>>(`${GROUPWARE_BASE_PATH}/notifications/unread-count`)
  return unwrap(response)?.count ?? 0
}

export const createNotification = async (payload: GroupwareNotificationCreateRequest) => {
  const response = await http.post<ApiEnvelope<GroupwareNotificationItem>>(`${GROUPWARE_BASE_PATH}/notifications`, payload)
  return unwrap(response)
}

export const markNotificationRead = async (notificationId: string) => {
  const response = await http.patch<ApiEnvelope<GroupwareNotificationItem>>(`${GROUPWARE_BASE_PATH}/notifications/${notificationId}/read`)
  return unwrap(response)
}

export const deleteNotification = async (notificationId: string) => {
  await http.delete(`${GROUPWARE_BASE_PATH}/notifications/${notificationId}`)
}

export const markAllNotificationsRead = async () => {
  const response = await http.patch<ApiEnvelope<GroupwareUpdatedCount>>(`${GROUPWARE_BASE_PATH}/notifications/read-all`)
  return unwrap(response)
}

export const archiveAllNotifications = async (retentionDays = 30) => {
  const response = await http.patch<ApiEnvelope<GroupwareUpdatedCount>>(
    `${GROUPWARE_BASE_PATH}/notifications/archive-all`,
    undefined,
    { params: { retentionDays } },
  )
  return unwrap(response)
}

export const listArchivedNotifications = async () => {
  const response = await http.get<ApiEnvelope<GroupwareNotificationItem[]>>(`${GROUPWARE_BASE_PATH}/notifications/archive`)
  return unwrap(response) ?? []
}

export const archiveNotification = async (notificationId: string, retentionDays = 30) => {
  const response = await http.patch<ApiEnvelope<GroupwareNotificationItem>>(
    `${GROUPWARE_BASE_PATH}/notifications/${notificationId}/archive`,
    undefined,
    { params: { retentionDays } },
  )
  return unwrap(response)
}

export const purgeExpiredNotifications = async () => {
  const response = await http.post<ApiEnvelope<GroupwareArchivePurgeResponse>>(`${GROUPWARE_BASE_PATH}/notifications/archive/purge-expired`)
  return unwrap(response)
}

export const createDirectMessage = async (payload: { receiverUserId: string; content: string }) => {
  const response = await http.post<ApiEnvelope<Record<string, unknown>>>(`${GROUPWARE_BASE_PATH}/messages`, payload)
  return unwrap(response)
}

export const listConversation = async (peerUserId: string, page = 0, size = 20) => {
  const response = await http.get<ApiEnvelope<GroupwarePageSlice<Record<string, unknown>>>>(`${GROUPWARE_BASE_PATH}/messages/conversation`, {
    params: { peerUserId, page, size },
  })
  return unwrap(response)?.content ?? []
}

export const markConversationRead = async (peerUserId: string, payload: { lastReadMessageId: string }) => {
  const response = await http.patch<ApiEnvelope<{ readCount: number }>>(`${GROUPWARE_BASE_PATH}/messages/conversation/read`, payload, {
    params: { peerUserId },
  })
  return unwrap(response)
}

export const createChatRoom = async (payload: GroupwareChatRoomCreateRequest) => {
  const response = await http.post<ApiEnvelope<GroupwareChatRoom>>(`${GROUPWARE_BASE_PATH}/chats/rooms`, payload)
  return unwrap(response)
}

export const listChatRooms = async () => {
  const response = await http.get<ApiEnvelope<GroupwareChatRoom[]>>(`${GROUPWARE_BASE_PATH}/chats/rooms`)
  return unwrap(response) ?? []
}

export const getChatRoom = async (roomId: string) => {
  const response = await http.get<ApiEnvelope<GroupwareChatRoom>>(`${GROUPWARE_BASE_PATH}/chats/rooms/${roomId}`)
  return unwrap(response)
}

export const updateChatRoom = async (roomId: string, payload: GroupwareChatRoomUpdateRequest) => {
  const response = await http.patch<ApiEnvelope<GroupwareChatRoom>>(`${GROUPWARE_BASE_PATH}/chats/rooms/${roomId}`, payload)
  return unwrap(response)
}

export const deleteChatRoom = async (roomId: string) => {
  const response = await http.delete<ApiEnvelope<GroupwareChatRoom>>(`${GROUPWARE_BASE_PATH}/chats/rooms/${roomId}`)
  return unwrap(response)
}

export const leaveChatRoom = async (roomId: string) => {
  const response = await http.patch<ApiEnvelope<GroupwareChatRoomMember>>(`${GROUPWARE_BASE_PATH}/chats/rooms/${roomId}/leave`)
  return unwrap(response)
}

export const addChatRoomMember = async (roomId: string, payload: { userId: string }) => {
  const response = await http.post<ApiEnvelope<GroupwareChatRoomMember>>(`${GROUPWARE_BASE_PATH}/chats/rooms/${roomId}/members`, payload)
  return unwrap(response)
}

export const listChatRoomMembers = async (roomId: string) => {
  const response = await http.get<ApiEnvelope<GroupwareChatRoomMember[]>>(`${GROUPWARE_BASE_PATH}/chats/rooms/${roomId}/members`)
  return unwrap(response) ?? []
}

export const removeChatRoomMember = async (roomId: string, memberUserId: string) => {
  await http.delete(`${GROUPWARE_BASE_PATH}/chats/rooms/${roomId}/members/${memberUserId}`)
}

export const createChatRoomMessage = async (roomId: string, payload: GroupwareChatMessageCreateRequest) => {
  const response = await http.post<ApiEnvelope<GroupwareChatMessage>>(`${GROUPWARE_BASE_PATH}/chats/rooms/${roomId}/messages`, payload)
  return unwrap(response)
}

export const listChatRoomMessages = async (roomId: string, page = 0, size = 20) => {
  const response = await http.get<ApiEnvelope<GroupwarePageSlice<GroupwareChatMessage>>>(`${GROUPWARE_BASE_PATH}/chats/rooms/${roomId}/messages`, {
    params: { page, size },
  })
  return unwrap(response)?.content ?? []
}

export const updateChatRoomMessage = async (roomId: string, messageId: string, payload: GroupwareChatMessageCreateRequest) => {
  const response = await http.patch<ApiEnvelope<GroupwareChatMessage>>(`${GROUPWARE_BASE_PATH}/chats/rooms/${roomId}/messages/${messageId}`, payload)
  return unwrap(response)
}

export const deleteChatRoomMessage = async (roomId: string, messageId: string) => {
  await http.delete(`${GROUPWARE_BASE_PATH}/chats/rooms/${roomId}/messages/${messageId}`)
}

export const searchChatRoomMessages = async (roomId: string, keyword = '', page = 0, size = 20) => {
  const response = await http.get<ApiEnvelope<GroupwarePageSlice<GroupwareChatMessage>>>(`${GROUPWARE_BASE_PATH}/chats/rooms/${roomId}/search-messages`, {
    params: { keyword, page, size },
  })
  return unwrap(response)?.content ?? []
}

export const markChatRoomRead = async (roomId: string, payload: GroupwareReadRequest) => {
  const response = await http.patch<ApiEnvelope<GroupwareChatRoomMember>>(`${GROUPWARE_BASE_PATH}/chats/rooms/${roomId}/read`, payload)
  return unwrap(response)
}

export const getChatRoomUnreadCount = async (roomId: string) => {
  const response = await http.get<ApiEnvelope<GroupwareUnreadCount>>(`${GROUPWARE_BASE_PATH}/chats/rooms/${roomId}/unread-count`)
  return unwrap(response)?.count ?? 0
}

export const listSchedules = async (params: { from?: string; to?: string }) => {
  const response = await http.get<ApiEnvelope<GroupwareScheduleItem[]>>(`${GROUPWARE_BASE_PATH}/schedules`, {
    params,
  })
  return unwrap(response) ?? []
}

export const searchSchedules = async (params: GroupwareScheduleSearchParams) => {
  const response = await http.get<ApiEnvelope<GroupwareScheduleItem[]>>(`${GROUPWARE_BASE_PATH}/schedules/search`, {
    params,
  })
  return unwrap(response) ?? []
}

export const createSchedule = async (payload: GroupwareScheduleCreateRequest) => {
  const response = await http.post<ApiEnvelope<GroupwareScheduleItem>>(`${GROUPWARE_BASE_PATH}/schedules`, payload)
  return unwrap(response)
}

export const updateSchedule = async (scheduleId: string, payload: GroupwareScheduleCreateRequest) => {
  const response = await http.patch<ApiEnvelope<GroupwareScheduleItem>>(`${GROUPWARE_BASE_PATH}/schedules/${scheduleId}`, payload)
  return unwrap(response)
}

export const getSchedule = async (scheduleId: string) => {
  const response = await http.get<ApiEnvelope<GroupwareScheduleItem>>(`${GROUPWARE_BASE_PATH}/schedules/${scheduleId}`)
  return unwrap(response)
}

export const deleteSchedule = async (scheduleId: string) => {
  await http.delete(`${GROUPWARE_BASE_PATH}/schedules/${scheduleId}`)
}

export const listSchedulesByProjectId = async (projectId: string) => {
  const response = await http.get<ApiEnvelope<GroupwareScheduleItem[]>>(`${GROUPWARE_BASE_PATH}/schedules/by-project-id/${projectId}`)
  return unwrap(response) ?? []
}

export const listSchedulesByProjectCode = async (projectCode: string) => {
  const response = await http.get<ApiEnvelope<GroupwareScheduleItem[]>>(`${GROUPWARE_BASE_PATH}/schedules/by-project-code/${projectCode}`)
  return unwrap(response) ?? []
}

export const saveScheduleRecurrence = async (scheduleId: string, payload: GroupwareScheduleRecurrenceRule) => {
  const response = await http.post<ApiEnvelope<GroupwareScheduleRecurrenceRule>>(`${GROUPWARE_BASE_PATH}/schedules/${scheduleId}/recurrence`, payload)
  return unwrap(response)
}

export const updateScheduleRecurrence = async (scheduleId: string, payload: GroupwareScheduleRecurrenceRule) => {
  const response = await http.patch<ApiEnvelope<GroupwareScheduleRecurrenceRule>>(`${GROUPWARE_BASE_PATH}/schedules/${scheduleId}/recurrence`, payload)
  return unwrap(response)
}

export const deleteScheduleRecurrence = async (scheduleId: string) => {
  await http.delete(`${GROUPWARE_BASE_PATH}/schedules/${scheduleId}/recurrence`)
}

export const getScheduleRecurrence = async (scheduleId: string) => {
  const response = await http.get<ApiEnvelope<GroupwareScheduleRecurrenceRule | null>>(`${GROUPWARE_BASE_PATH}/schedules/${scheduleId}/recurrence`)
  return unwrap(response)
}

export const listScheduleOccurrences = async (scheduleId: string, params: { from?: string; to?: string }) => {
  const response = await http.get<ApiEnvelope<GroupwareScheduleOccurrence[]>>(`${GROUPWARE_BASE_PATH}/schedules/${scheduleId}/occurrences`, {
    params,
  })
  return unwrap(response) ?? []
}

export const createScheduleOccurrenceExclusion = async (scheduleId: string, payload: GroupwareScheduleOccurrenceExclusionRequest) => {
  const response = await http.post<ApiEnvelope<GroupwareScheduleOccurrenceExclusion>>(
    `${GROUPWARE_BASE_PATH}/schedules/${scheduleId}/occurrences/exclusions`,
    payload,
  )
  return unwrap(response)
}

export const createCostItem = async (payload: GroupwareCostItemCreateRequest) => {
  const response = await http.post<ApiEnvelope<GroupwareCostItem>>(`${GROUPWARE_BASE_PATH}/costs/items`, payload)
  return unwrap(response)
}

export const listCostItems = async () => {
  const response = await http.get<ApiEnvelope<GroupwareCostItem[]>>(`${GROUPWARE_BASE_PATH}/costs/items`)
  return unwrap(response) ?? []
}

export const updateCostItem = async (costItemId: string, payload: GroupwareCostItemCreateRequest) => {
  const response = await http.patch<ApiEnvelope<GroupwareCostItem>>(`${GROUPWARE_BASE_PATH}/costs/items/${costItemId}`, payload)
  return unwrap(response)
}

export const deleteCostItem = async (costItemId: string) => {
  await http.delete(`${GROUPWARE_BASE_PATH}/costs/items/${costItemId}`)
}

export const createCostAccount = async (payload: GroupwareCostAccountCreateRequest) => {
  const response = await http.post<ApiEnvelope<GroupwareCostAccount>>(`${GROUPWARE_BASE_PATH}/costs/accounts`, payload)
  return unwrap(response)
}

export const listCostAccounts = async () => {
  const response = await http.get<ApiEnvelope<GroupwareCostAccount[]>>(`${GROUPWARE_BASE_PATH}/costs/accounts`)
  return unwrap(response) ?? []
}

export const updateCostAccount = async (accountId: string, payload: GroupwareCostAccountCreateRequest) => {
  const response = await http.patch<ApiEnvelope<GroupwareCostAccount>>(`${GROUPWARE_BASE_PATH}/costs/accounts/${accountId}`, payload)
  return unwrap(response)
}

export const deleteCostAccount = async (accountId: string) => {
  await http.delete(`${GROUPWARE_BASE_PATH}/costs/accounts/${accountId}`)
}

export const createScheduleCost = async (payload: GroupwareScheduleCostCreateRequest) => {
  const response = await http.post<ApiEnvelope<GroupwareScheduleCost>>(`${GROUPWARE_BASE_PATH}/costs/schedule-costs`, payload)
  return unwrap(response)
}

export const listScheduleCostsByScheduleId = async (scheduleId: string) => {
  const response = await http.get<ApiEnvelope<GroupwareScheduleCost[]>>(`${GROUPWARE_BASE_PATH}/costs/schedule-costs/by-schedule/${scheduleId}`)
  return unwrap(response) ?? []
}

export const getScheduleCost = async (scheduleCostId: string) => {
  const response = await http.get<ApiEnvelope<GroupwareScheduleCost>>(`${GROUPWARE_BASE_PATH}/costs/schedule-costs/${scheduleCostId}`)
  return unwrap(response)
}

export const updateScheduleCost = async (scheduleCostId: string, payload: GroupwareScheduleCostCreateRequest) => {
  const response = await http.patch<ApiEnvelope<GroupwareScheduleCost>>(`${GROUPWARE_BASE_PATH}/costs/schedule-costs/${scheduleCostId}`, payload)
  return unwrap(response)
}

export const deleteScheduleCost = async (scheduleCostId: string) => {
  await http.delete(`${GROUPWARE_BASE_PATH}/costs/schedule-costs/${scheduleCostId}`)
}

export const searchScheduleCosts = async (params: GroupwareScheduleCostSearchParams) => {
  const response = await http.get<ApiEnvelope<GroupwareScheduleCost[]>>(`${GROUPWARE_BASE_PATH}/costs/schedule-costs/search`, {
    params,
  })
  return unwrap(response) ?? []
}

export const getScheduleCostSummaryByScheduleId = async (scheduleId: string) => {
  const response = await http.get<ApiEnvelope<GroupwareAmountSummary>>(`${GROUPWARE_BASE_PATH}/costs/schedule-costs/summary/by-schedule/${scheduleId}`)
  return unwrap(response) ?? { totalAmount: 0 }
}

export const listScheduleCostsByProjectId = async (projectId: string) => {
  const response = await http.get<ApiEnvelope<GroupwareScheduleCost[]>>(`${GROUPWARE_BASE_PATH}/costs/schedule-costs/by-project-id/${projectId}`)
  return unwrap(response) ?? []
}

export const getScheduleCostSummaryByProjectId = async (projectId: string) => {
  const response = await http.get<ApiEnvelope<GroupwareAmountSummary>>(`${GROUPWARE_BASE_PATH}/costs/schedule-costs/summary/by-project-id/${projectId}`)
  return unwrap(response) ?? { totalAmount: 0 }
}

export const listScheduleCostsByProjectCode = async (projectCode: string) => {
  const response = await http.get<ApiEnvelope<GroupwareScheduleCost[]>>(`${GROUPWARE_BASE_PATH}/costs/schedule-costs/by-project-code/${projectCode}`)
  return unwrap(response) ?? []
}

export const getScheduleCostSummaryByProjectCode = async (projectCode: string) => {
  const response = await http.get<ApiEnvelope<GroupwareAmountSummary>>(`${GROUPWARE_BASE_PATH}/costs/schedule-costs/summary/by-project-code/${projectCode}`)
  return unwrap(response) ?? { totalAmount: 0 }
}

export const listProjects = async () => {
  const response = await http.get<ApiEnvelope<GroupwareProjectItem[]>>(`${GROUPWARE_BASE_PATH}/projects`)
  return unwrap(response) ?? []
}

export const searchProjects = async (params: GroupwareProjectSearchParams) => {
  const response = await http.get<ApiEnvelope<GroupwareProjectItem[]>>(`${GROUPWARE_BASE_PATH}/projects/search`, {
    params,
  })
  return unwrap(response) ?? []
}

export const createProject = async (payload: GroupwareProjectCreateRequest) => {
  const response = await http.post<ApiEnvelope<GroupwareProjectItem>>(`${GROUPWARE_BASE_PATH}/projects`, payload)
  return unwrap(response)
}

export const getProject = async (projectId: string) => {
  const response = await http.get<ApiEnvelope<GroupwareProjectItem>>(`${GROUPWARE_BASE_PATH}/projects/${projectId}`)
  return unwrap(response)
}

export const updateProject = async (projectId: string, payload: GroupwareProjectCreateRequest) => {
  const response = await http.patch<ApiEnvelope<GroupwareProjectItem>>(`${GROUPWARE_BASE_PATH}/projects/${projectId}`, payload)
  return unwrap(response)
}

export const deleteProject = async (projectId: string) => {
  await http.delete(`${GROUPWARE_BASE_PATH}/projects/${projectId}`)
}

export const updateProjectStatus = async (projectId: string, status: string) => {
  const response = await http.patch<ApiEnvelope<GroupwareProjectItem>>(`${GROUPWARE_BASE_PATH}/projects/${projectId}/status`, { status })
  return unwrap(response)
}

export const updateProjectProgressRate = async (projectId: string, progressRate: number) => {
  const response = await http.patch<ApiEnvelope<GroupwareProjectItem>>(`${GROUPWARE_BASE_PATH}/projects/${projectId}/progress-rate`, { progressRate })
  return unwrap(response)
}

export const createProjectTask = async (projectId: string, payload: GroupwareProjectTaskCreateRequest) => {
  const response = await http.post<ApiEnvelope<GroupwareProjectTask>>(`${GROUPWARE_BASE_PATH}/projects/${projectId}/tasks`, payload)
  return unwrap(response)
}

export const listProjectTasks = async (projectId: string) => {
  const response = await http.get<ApiEnvelope<GroupwareProjectTask[]>>(`${GROUPWARE_BASE_PATH}/projects/${projectId}/tasks`)
  return unwrap(response) ?? []
}

export const getProjectTask = async (projectId: string, taskId: string) => {
  const response = await http.get<ApiEnvelope<GroupwareProjectTask>>(`${GROUPWARE_BASE_PATH}/projects/${projectId}/tasks/${taskId}`)
  return unwrap(response)
}

export const updateProjectTask = async (projectId: string, taskId: string, payload: GroupwareProjectTaskCreateRequest) => {
  const response = await http.patch<ApiEnvelope<GroupwareProjectTask>>(`${GROUPWARE_BASE_PATH}/projects/${projectId}/tasks/${taskId}`, payload)
  return unwrap(response)
}

export const updateProjectTaskStatus = async (projectId: string, taskId: string, status: string) => {
  const response = await http.patch<ApiEnvelope<GroupwareProjectTask>>(`${GROUPWARE_BASE_PATH}/projects/${projectId}/tasks/${taskId}/status`, { status })
  return unwrap(response)
}

export const deleteProjectTask = async (projectId: string, taskId: string) => {
  await http.delete(`${GROUPWARE_BASE_PATH}/projects/${projectId}/tasks/${taskId}`)
}

export const createProjectComment = async (projectId: string, payload: GroupwareProjectCommentCreateRequest) => {
  const response = await http.post<ApiEnvelope<GroupwareProjectComment>>(`${GROUPWARE_BASE_PATH}/projects/${projectId}/comments`, payload)
  return unwrap(response)
}

export const listProjectComments = async (projectId: string, taskId?: string) => {
  const response = await http.get<ApiEnvelope<GroupwareProjectComment[]>>(`${GROUPWARE_BASE_PATH}/projects/${projectId}/comments`, {
    params: taskId ? { taskId } : {},
  })
  return unwrap(response) ?? []
}

export const getProjectComment = async (projectId: string, commentId: string) => {
  const response = await http.get<ApiEnvelope<GroupwareProjectComment>>(`${GROUPWARE_BASE_PATH}/projects/${projectId}/comments/${commentId}`)
  return unwrap(response)
}

export const updateProjectComment = async (projectId: string, commentId: string, payload: GroupwareProjectCommentCreateRequest) => {
  const response = await http.patch<ApiEnvelope<GroupwareProjectComment>>(`${GROUPWARE_BASE_PATH}/projects/${projectId}/comments/${commentId}`, payload)
  return unwrap(response)
}

export const deleteProjectComment = async (projectId: string, commentId: string) => {
  await http.delete(`${GROUPWARE_BASE_PATH}/projects/${projectId}/comments/${commentId}`)
}

export const listProjectTaskComments = async (projectId: string, taskId: string) => {
  const response = await http.get<ApiEnvelope<GroupwareProjectComment[]>>(`${GROUPWARE_BASE_PATH}/projects/${projectId}/tasks/${taskId}/comments`)
  return unwrap(response) ?? []
}

export const listApprovals = async () => {
  const response = await http.get<ApiEnvelope<GroupwareApprovalDocument[]>>(`${GROUPWARE_BASE_PATH}/approvals`)
  return unwrap(response) ?? []
}

export const searchApprovals = async (params: GroupwareApprovalSearchParams) => {
  const response = await http.get<ApiEnvelope<GroupwareApprovalDocument[]>>(`${GROUPWARE_BASE_PATH}/approvals/search`, {
    params,
  })
  return unwrap(response) ?? []
}

export const createApproval = async (payload: GroupwareApprovalCreateRequest) => {
  const response = await http.post<ApiEnvelope<GroupwareApprovalDocument>>(`${GROUPWARE_BASE_PATH}/approvals`, payload)
  return unwrap(response)
}

export const getApproval = async (documentId: string) => {
  const response = await http.get<ApiEnvelope<GroupwareApprovalDocument>>(`${GROUPWARE_BASE_PATH}/approvals/${documentId}`)
  return unwrap(response)
}

export const updateApproval = async (documentId: string, payload: GroupwareApprovalCreateRequest) => {
  const response = await http.patch<ApiEnvelope<GroupwareApprovalDocument>>(`${GROUPWARE_BASE_PATH}/approvals/${documentId}`, payload)
  return unwrap(response)
}

export const deleteApproval = async (documentId: string) => {
  await http.delete(`${GROUPWARE_BASE_PATH}/approvals/${documentId}`)
}

export const submitApproval = async (documentId: string) => {
  const response = await http.patch<ApiEnvelope<GroupwareApprovalDocument>>(`${GROUPWARE_BASE_PATH}/approvals/${documentId}/submit`)
  return unwrap(response)
}

export const approveApproval = async (documentId: string) => {
  const response = await http.patch<ApiEnvelope<GroupwareApprovalDocument>>(`${GROUPWARE_BASE_PATH}/approvals/${documentId}/approve`)
  return unwrap(response)
}

export const rejectApproval = async (documentId: string) => {
  const response = await http.patch<ApiEnvelope<GroupwareApprovalDocument>>(`${GROUPWARE_BASE_PATH}/approvals/${documentId}/reject`)
  return unwrap(response)
}

export const getApprovalLines = async (documentId: string) => {
  const response = await http.get<ApiEnvelope<GroupwareApprovalLine[]>>(`${GROUPWARE_BASE_PATH}/approvals/${documentId}/lines`)
  return unwrap(response) ?? []
}

export const getApprovalActions = async (documentId: string) => {
  const response = await http.get<ApiEnvelope<GroupwareApprovalActionHistory[]>>(`${GROUPWARE_BASE_PATH}/approvals/${documentId}/actions`)
  return unwrap(response) ?? []
}

export const createApprovalLineTemplate = async (payload: GroupwareApprovalLineTemplateCreateRequest) => {
  const response = await http.post<ApiEnvelope<GroupwareApprovalLineTemplate>>(`${GROUPWARE_BASE_PATH}/approvals/line-templates`, payload)
  return unwrap(response)
}

export const listApprovalLineTemplates = async () => {
  const response = await http.get<ApiEnvelope<GroupwareApprovalLineTemplate[]>>(`${GROUPWARE_BASE_PATH}/approvals/line-templates`)
  return unwrap(response) ?? []
}

export const getApprovalLineTemplateItems = async (templateId: string) => {
  const response = await http.get<ApiEnvelope<GroupwareApprovalLineTemplateItem[]>>(`${GROUPWARE_BASE_PATH}/approvals/line-templates/${templateId}/items`)
  return unwrap(response) ?? []
}

export const updateApprovalLineTemplate = async (templateId: string, payload: GroupwareApprovalLineTemplateCreateRequest) => {
  const response = await http.patch<ApiEnvelope<GroupwareApprovalLineTemplate>>(`${GROUPWARE_BASE_PATH}/approvals/line-templates/${templateId}`, payload)
  return unwrap(response)
}

export const deleteApprovalLineTemplate = async (templateId: string) => {
  await http.delete(`${GROUPWARE_BASE_PATH}/approvals/line-templates/${templateId}`)
}

export const applyApprovalLineTemplate = async (documentId: string, templateId: string) => {
  const response = await http.post<ApiEnvelope<GroupwareApprovalLine[]>>(`${GROUPWARE_BASE_PATH}/approvals/${documentId}/line-templates/${templateId}/apply`)
  return unwrap(response) ?? []
}

export const resetApprovalConsultLines = async (documentId: string, payload: GroupwareApprovalLineRequest[]) => {
  const response = await http.post<ApiEnvelope<GroupwareApprovalLine[]>>(`${GROUPWARE_BASE_PATH}/approvals/${documentId}/consult-lines/reset`, payload)
  return unwrap(response) ?? []
}

export const listDirectoryUsers = async (keyword = '') => {
  const response = await http.get<ApiEnvelope<GroupwareDirectoryUser[]>>(`${GROUPWARE_BASE_PATH}/directory/users`, {
    params: keyword ? { keyword } : {},
  })
  return unwrap(response) ?? []
}

export const fetchRealtimeStatus = async (): Promise<GroupwareRealtimeStatus> => {
  const [unreadCount, rooms] = await Promise.all([
    getUnreadNotificationCount(),
    listChatRooms(),
  ])

  return {
    unreadCount,
    roomCount: rooms.length,
  }
}
