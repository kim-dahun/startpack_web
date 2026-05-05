export interface GroupwareUnreadCount {
  count: number
}

export interface GroupwareUpdatedCount {
  updatedCount: number
}

export interface GroupwareArchivePurgeResponse {
  deletedCount: number
}

export interface GroupwareAmountSummary {
  totalAmount: number
}

export interface GroupwareNotificationItem {
  notificationId: string
  title: string
  content: string
  status: 'UNREAD' | 'READ' | string
  referenceType: string | null
  referenceId: string | null
  createdAt: string
  archivedAt?: string | null
}

export interface GroupwareNotificationCreateRequest {
  targetUserId: string
  title: string
  content: string
  referenceType: string
  referenceId: string
}

export interface GroupwareNotificationSearchParams {
  status?: string
  referenceType?: string
  from?: string
  to?: string
}

export interface GroupwarePageSlice<T> {
  content: T[]
  page: number
  size: number
  hasNext: boolean
}

export interface GroupwareChatRoom {
  roomId: string
  roomType: 'DIRECT' | 'GROUP'
  roomName: string | null
  createdByUserId: string
  memberCount?: number
  unreadCount?: number
}

export interface GroupwareChatRoomCreateRequest {
  roomType: 'DIRECT' | 'GROUP'
  roomName: string | null
  memberUserIds: string[]
}

export interface GroupwareChatRoomUpdateRequest {
  roomName: string | null
}

export interface GroupwareChatRoomMember {
  roomMemberId?: string
  roomId: string
  userId: string
  joinedAt?: string
  lastReadMessageId?: string | null
}

export interface GroupwareChatMessage {
  messageId: string
  roomId: string
  messageType: string
  content: string
  createdByUserId: string
  createdAt: string
}

export interface GroupwareChatMessageCreateRequest {
  messageType: string
  content: string
}

export interface GroupwareReadRequest {
  lastReadMessageId: string
}

export interface GroupwareScheduleItem {
  scheduleId: string
  projectId: string | null
  projectCode: string | null
  title: string
  memo: string | null
  startAt: string
  endAt: string
  allDay?: boolean
  scope: 'PERSONAL' | 'COMPANY' | string
}

export interface GroupwareScheduleCreateRequest {
  title: string
  projectId: string | null
  projectCode: string | null
  memo: string
  startAt: string
  endAt: string
  allDay: boolean
  scope: 'PERSONAL' | 'COMPANY'
}

export interface GroupwareScheduleSearchParams {
  keyword?: string
  scope?: string
  projectId?: string
  projectCode?: string
  from?: string
  to?: string
}

export interface GroupwareScheduleRecurrenceRule {
  recurrenceRuleId?: string
  scheduleId?: string
  frequency: 'DAILY' | 'WEEKLY' | 'MONTHLY' | 'YEARLY'
  intervalValue: number
  untilDate?: string | null
  countLimit?: number | null
}

export interface GroupwareScheduleOccurrence {
  occurrenceId: string
  scheduleId: string
  startAt: string
  endAt: string
}

export interface GroupwareScheduleOccurrenceExclusionRequest {
  occurrenceDate: string
  reason: string
}

export interface GroupwareScheduleOccurrenceExclusion {
  exclusionId?: string
  scheduleId?: string
  occurrenceDate: string
  reason?: string | null
}

export interface GroupwareCostItem {
  costItemId: string
  costItemName: string
  enabled: boolean
}

export interface GroupwareCostAccount {
  accountId: string
  accountName: string
  enabled: boolean
}

export interface GroupwareCostItemCreateRequest {
  costItemName: string
  enabled: boolean
}

export interface GroupwareCostAccountCreateRequest {
  accountName: string
  enabled: boolean
}

export interface GroupwareScheduleCost {
  scheduleCostId: string
  scheduleId?: string
  projectId: string | null
  projectCode: string | null
  costDate: string
  costItemId?: string
  accountId?: string
  amount: number
  description: string | null
}

export interface GroupwareScheduleCostCreateRequest {
  scheduleId: string
  projectId: string | null
  projectCode: string | null
  costDate: string
  costItemId: string
  accountId: string
  amount: number
  description: string
}

export interface GroupwareScheduleCostSearchParams {
  projectId?: string
  projectCode?: string
  costItemId?: string
  accountId?: string
  from?: string
  to?: string
}

export interface GroupwareProjectItem {
  projectId: string
  projectCode?: string | null
  name: string
  description: string | null
  status: 'PLANNED' | 'IN_PROGRESS' | 'DONE' | 'ON_HOLD' | string
  progressRate: number
  ownerUserId?: string
  memberUserIds?: string[]
  referenceUserIds?: string[]
}

export interface GroupwareProjectSearchParams {
  keyword?: string
  status?: string
  memberUserId?: string
  referenceUserId?: string
  ownerUserId?: string
}

export interface GroupwareProjectCreateRequest {
  name: string
  description: string
  memberUserIds: string[]
  referenceUserIds: string[]
  status: 'PLANNED' | 'IN_PROGRESS' | 'DONE' | 'ON_HOLD'
  progressRate: number
}

export interface GroupwareProjectTask {
  taskId: string
  title: string
  description: string | null
  assigneeUserId: string | null
  dueDate: string | null
  status: 'TODO' | 'IN_PROGRESS' | 'DONE' | 'ON_HOLD' | string
}

export interface GroupwareProjectTaskCreateRequest {
  title: string
  description: string
  assigneeUserId: string | null
  dueDate: string | null
  status: 'TODO' | 'IN_PROGRESS' | 'DONE' | 'ON_HOLD'
}

export interface GroupwareProjectComment {
  commentId: string
  projectId: string
  taskId: string | null
  content: string
  createdByUserId?: string
  createdAt?: string
}

export interface GroupwareProjectCommentCreateRequest {
  taskId: string | null
  content: string
}

export interface GroupwareApprovalDocument {
  documentId: string
  title: string
  content?: string
  documentType: string
  documentJson?: string
  status: 'DRAFT' | 'SUBMITTED' | 'APPROVED' | 'REJECTED' | string
  drafterUserId: string
  approverUserIds?: string[]
  createdAt: string
}

export interface GroupwareApprovalSearchParams {
  status?: string
  documentType?: string
  writerUserId?: string
  keyword?: string
}

export interface GroupwareApprovalLineRequest {
  lineStage: 'PRIMARY' | 'SECONDARY' | string
  lineSeq: number
  approvalRoleType: 'APPROVAL' | 'CONSULT' | 'REFERENCE' | string
  decisionMode: 'NORMAL' | string
  targetType: 'USER' | 'DEPARTMENT' | 'DEPARTMENT_POSITION' | string
  targetUserId: string | null
  targetDepartmentId: string | null
  targetPositionId: string | null
}

export interface GroupwareApprovalCreateRequest {
  title: string
  content: string
  documentType: string
  documentJson: string
  approverUserIds: string[]
  approvalLines: GroupwareApprovalLineRequest[]
}

export interface GroupwareApprovalLine {
  approvalLineId: string
  approvalRoleType: string
  lineStage: string
  lineSeq: number
  decisionStatus?: string
  targetType: string
  targetUserId: string | null
  targetDepartmentId: string | null
  targetPositionId: string | null
  signedUserId?: string | null
  signedAt?: string | null
}

export interface GroupwareApprovalActionHistory {
  actionHistoryId?: string
  documentId?: string
  actionType: string
  actionUserId?: string | null
  actionComment?: string | null
  createdAt?: string | null
}

export interface GroupwareApprovalLineTemplate {
  templateId: string
  templateName: string
  createdAt?: string
}

export interface GroupwareApprovalLineTemplateCreateRequest {
  templateName: string
  approvalLines: GroupwareApprovalLineRequest[]
}

export interface GroupwareApprovalLineTemplateItem extends GroupwareApprovalLineRequest {
  templateItemId?: string
  templateId?: string
}

export interface GroupwareDirectoryUser {
  userId: string
  userName: string
  jobGradeId?: string | null
  jobGradeName: string | null
  primaryDepartmentId?: string | null
  primaryDepartmentName: string | null
  primaryPositionId?: string | null
  primaryPositionName: string | null
  affiliations?: Array<Record<string, unknown>>
  source: string
}

export interface GroupwareRealtimeStatus {
  unreadCount: number
  roomCount: number
}

export interface GroupwareRealtimeEvent {
  comCd: string
  userId: string
  channel: 'notifications' | 'messages' | string
  eventType: string
  payload: Record<string, unknown>
  occurredAt: string
}
