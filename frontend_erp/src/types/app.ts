export interface ApiEnvelope<T> {
  success: boolean
  data: T
  responseMessage: string | null
  responseCode: number | null
}

export interface LoginRequest {
  comCd: string
  userId: string
  password: string
  serviceId: string
}

export interface UserProfile {
  comCd: string
  userId: string
  userName: string
  email: string
  phone: string
  address: string
  status: string
  serviceAccesses?: string[]
}

export interface UserGroup {
  comCd: string
  serviceId: string
  groupId: string
  groupName: string
  description?: string
  enabled?: boolean
}

export interface AuthToken {
  userId: string
  loginId: string
  accessToken?: string
  accessTokenExpiresAt: string
  refreshToken?: string
  refreshTokenExpiresAt: string
  roles: string[]
  tokenDeliveryMethod?: string
}

export interface MenuItem {
  menuId: string
  parentMenuId: string | null
  menuName: string
  menuUrl: string
  i18nCode: string
  icon: string
  menuLevel: number
  sortSeq: number
}

export interface MenuPermission {
  menuId: string
  permitRead: boolean
  permitWrite: boolean
  permitDelete: boolean
  permitExcel: boolean
}

export interface LoginResponse {
  user: UserProfile
  serviceId?: string
  serviceAccesses?: string[]
  groups?: UserGroup[]
  token: AuthToken
  menus: MenuItem[]
  menuPermissions: MenuPermission[]
}

export interface LogoutRequest {
  refreshToken?: string
}

export interface LogoutResponse {
  subject: string
  tokenId: string
  reason: string
  revokedAt: string
}

export interface PermissionFlags {
  permitRead: boolean
  permitWrite: boolean
  permitDelete: boolean
  permitExcel: boolean
}

export interface DataGridOption {
  label: string
  value: string | number | boolean | null
}

export interface SessionState {
  user: UserProfile | null
  token: AuthToken | null
  menus: MenuItem[]
  menuPermissions: MenuPermission[]
  serviceId: string | null
  serviceAccesses: string[]
  groups: UserGroup[]
  currentMenuId: string | null
  currentMenuUrl: string | null
}

export interface DataGridColumn {
  title: string
  field: string
  editor?: 'input' | 'number' | 'tickCross' | 'select' | 'calendar' | false
  hozAlign?: 'left' | 'center' | 'right'
  width?: number
  formatter?: string
  defaultValue?: string | number | boolean | null
  editorOptions?: DataGridOption[]
  editable?: boolean
  dateFormat?: 'yyyy-MM-dd HH:mm:ss'
}

export interface GridRowMeta {
  rowId: string
  __rowStatus: 'NONE' | 'CREATED' | 'UPDATED' | 'DELETED'
}

export type GridRow = Record<string, unknown> & GridRowMeta

export interface CrudPayload<T> {
  added: T[]
  updated: T[]
  deleted: T[]
}

export interface ResourceDefinition {
  key: string
  title: string
  description: string
  menuId: string
  columns: DataGridColumn[]
  stats: Array<{ label: string; value: string; hint: string }>
  searchPlaceholder: string
  serviceScoped?: boolean
  requiresUserId?: boolean
}

export interface ManagementQueryParams {
  comCd: string
  serviceId?: string
  userId?: string
  groupId?: string
  codeGroupId?: string
}
