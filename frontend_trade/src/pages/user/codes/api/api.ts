import { getDropdownOptions, getManagementRows, saveManagementRows } from '@/api/modules/user'
import type { CrudPayload, DataGridOption, ManagementQueryParams } from '@/types/app'

export interface CodeGroupRow {
  comCd: string
  serviceId: string
  codeGroupId: string
  codeGroupName: string
  description: string
  enabled: boolean
}

export interface CodeRow {
  comCd: string
  serviceId: string
  codeGroupId: string
  codeId: string
  codeName: string
  parentCodeGroupId: string
  parentCodeId: string
  subInfo1: string
  subInfo2: string
  subInfo3: string
  sortSeq: number
  enabled: boolean
}

export const listCodeGroups = async (params: Pick<ManagementQueryParams, 'comCd' | 'serviceId'>) =>
  getManagementRows('codeGroups', params) as unknown as Promise<CodeGroupRow[]>

export const saveCodeGroups = async (
  payload: CrudPayload<Record<string, unknown>>,
  params: Pick<ManagementQueryParams, 'comCd' | 'serviceId'>,
) => saveManagementRows('codeGroups', payload, params) as unknown as Promise<CodeGroupRow[]>

export const listCodes = async (params: Pick<ManagementQueryParams, 'comCd' | 'serviceId' | 'codeGroupId'>) =>
  getManagementRows('codes', params) as unknown as Promise<CodeRow[]>

export const saveCodes = async (
  payload: CrudPayload<Record<string, unknown>>,
  params: Pick<ManagementQueryParams, 'comCd' | 'serviceId' | 'codeGroupId'>,
) => saveManagementRows('codes', payload, params) as unknown as Promise<CodeRow[]>

export const listCodeDropdownOptions = async (
  params: Pick<ManagementQueryParams, 'comCd' | 'serviceId' | 'codeGroupId'>,
) => getDropdownOptions('codes', params) as Promise<DataGridOption[]>
