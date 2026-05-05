import { getDropdownOptions, getManagementRows, saveManagementRows } from '@/api/modules/user'
import type { CrudPayload, DataGridOption } from '@/types/app'

export interface GroupRow {
  comCd: string
  serviceId: string
  groupId: string
  groupName: string
  description: string
  enabled: boolean
}

export interface GroupMemberRow {
  comCd: string
  serviceId: string
  groupId: string
  userId: string
}

export const listGroups = async (params: { comCd: string; serviceId: string }) =>
  getManagementRows('groups', params) as unknown as Promise<GroupRow[]>

export const saveGroups = async (
  payload: CrudPayload<Record<string, unknown>>,
  params: { comCd: string; serviceId: string },
) => saveManagementRows('groups', payload, params) as unknown as Promise<GroupRow[]>

export const listGroupMembers = async (params: { comCd: string; serviceId: string; groupId: string }) =>
  getManagementRows('groupMembers', params) as unknown as Promise<GroupMemberRow[]>

export const saveGroupMembers = async (
  payload: CrudPayload<Record<string, unknown>>,
  params: { comCd: string; serviceId: string; groupId: string },
) => saveManagementRows('groupMembers', payload, params) as unknown as Promise<GroupMemberRow[]>

export const listUserOptions = async (params: { comCd: string }) =>
  getDropdownOptions('users', params) as Promise<DataGridOption[]>
