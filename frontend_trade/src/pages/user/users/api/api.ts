import { getDropdownOptions, getManagementRows, saveManagementRows, getUserAffiliations, updateUserPrimaryYn, type UserPositionRow } from '@/api/modules/user'
import type { CrudPayload, DataGridOption } from '@/types/app'

export interface UserRow {
  comCd: string
  userId: string
  userName: string
  jobGradeId: string
  status: string
  email: string
  phone: string
  address: string
  password?: string
}

export const listUsers = async (params: { comCd: string }) =>
  getManagementRows('users', params) as unknown as Promise<UserRow[]>

export const saveUsers = async (
  payload: CrudPayload<Record<string, unknown>>,
  params: { comCd: string },
) => saveManagementRows('users', payload, params) as unknown as Promise<UserRow[]>

export const listJobGradeOptions = async (params: { comCd: string }) =>
  getDropdownOptions('jobGrades', params) as Promise<DataGridOption[]>

export const listAffiliations = async (params: { comCd: string; userId: string }) =>
  getUserAffiliations(params) as Promise<UserPositionRow[]>

export const savePrimaryYn = async (payload: {
  comCd: string
  userId: string
  departmentId: string
  positionId: string
  primaryYn: boolean
}) => updateUserPrimaryYn(payload)
