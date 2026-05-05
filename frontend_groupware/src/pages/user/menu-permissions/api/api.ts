import { getManagementRows, getMenuPermissionTreeRows, saveMenuPermissionTreeRows } from '@/api/modules/user'
import type { CrudPayload, ManagementQueryParams } from '@/types/app'

export interface GroupRow {
  comCd: string
  serviceId: string
  groupId: string
  groupName: string
  description?: string
  enabled?: boolean
}

export interface MenuPermissionTreeRow {
  comCd: string
  serviceId: string
  groupId: string
  menuId: string
  menuParentId: string
  menuName: string
  permitRead: boolean
  permitWrite: boolean
  permitDelete: boolean
  permitExcel: boolean
  children?: MenuPermissionTreeRow[]
}

export const listGroups = async (params: Pick<ManagementQueryParams, 'comCd' | 'serviceId'>) =>
  getManagementRows('groups', params) as unknown as Promise<GroupRow[]>

export const listPermissionTree = async (params: Pick<ManagementQueryParams, 'comCd' | 'serviceId' | 'groupId'>) =>
  getMenuPermissionTreeRows(params) as Promise<MenuPermissionTreeRow[]>

export const savePermissionTree = async (
  payload: CrudPayload<Record<string, unknown>>,
  params: Pick<ManagementQueryParams, 'comCd' | 'serviceId' | 'groupId'>,
) => saveMenuPermissionTreeRows(payload, params) as Promise<MenuPermissionTreeRow[]>
