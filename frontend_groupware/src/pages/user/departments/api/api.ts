import { getDepartmentMembers, getDropdownOptions, getManagementRows, saveDepartmentMembers, saveManagementRows, type UserPositionRow } from '@/api/modules/user'
import type { CrudPayload, DataGridOption } from '@/types/app'

export interface DepartmentRow {
  comCd: string
  departmentId: string
  departmentName: string
  parentDepartmentId: string
  departmentHeadUserId: string
  departmentHeadPositionId: string
  sortSeq: number
  enabled: boolean
}

export const listDepartments = async (params: { comCd: string }) =>
  getManagementRows('departments', params) as unknown as Promise<DepartmentRow[]>

export const saveDepartments = async (
  payload: CrudPayload<Record<string, unknown>>,
  params: { comCd: string },
) => saveManagementRows('departments', payload, params) as unknown as Promise<DepartmentRow[]>

export const listDepartmentMembers = async (params: { comCd: string; departmentId: string }) =>
  getDepartmentMembers(params) as Promise<UserPositionRow[]>

export const saveMembers = async (
  payload: CrudPayload<Record<string, unknown>>,
  params: { comCd: string; departmentId: string },
) => saveDepartmentMembers(payload, params) as Promise<UserPositionRow[]>

export const listDepartmentOptions = async (params: { comCd: string }) =>
  getDropdownOptions('departments', params) as Promise<DataGridOption[]>

export const listUserOptions = async (params: { comCd: string }) =>
  getDropdownOptions('users', params) as Promise<DataGridOption[]>

export const listPositionOptions = async (params: { comCd: string }) =>
  getDropdownOptions('positions', params) as Promise<DataGridOption[]>
