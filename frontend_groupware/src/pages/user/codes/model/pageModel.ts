import type { DataGridColumn } from '@/types/app'

export const pageTitle = '코드 관리'

export interface CodeGroupForm {
  codeGroupId: string
  codeGroupName: string
  description: string
  enabled: boolean
}

export const createEmptyCodeGroupForm = (): CodeGroupForm => ({
  codeGroupId: '',
  codeGroupName: '',
  description: '',
  enabled: true,
})

export const buildCodeColumns = () => ([
  { title: 'comCd', field: 'comCd', editor: false, defaultValue: 'COM001' },
  { title: 'serviceId', field: 'serviceId', editor: false, defaultValue: 'GROUPWARE' },
  { title: 'codeGroupId', field: 'codeGroupId', editor: false },
  { title: 'codeId', field: 'codeId', editor: 'input' },
  { title: 'codeName', field: 'codeName', editor: 'input' },
  { title: 'parentCodeGroupId', field: 'parentCodeGroupId', editor: false },
  { title: 'parentCodeId', field: 'parentCodeId', editor: 'select' },
  { title: 'subInfo1', field: 'subInfo1', editor: 'input' },
  { title: 'subInfo2', field: 'subInfo2', editor: 'input' },
  { title: 'subInfo3', field: 'subInfo3', editor: 'input' },
  { title: 'sortSeq', field: 'sortSeq', editor: 'number', defaultValue: 1 },
  { title: 'enabled', field: 'enabled', editor: 'tickCross', defaultValue: true },
]) satisfies DataGridColumn[]
