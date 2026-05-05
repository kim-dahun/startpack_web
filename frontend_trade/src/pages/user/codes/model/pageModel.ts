import type { DataGridColumn } from '@/types/app'

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

export const buildCodeColumns = (t: (key: string, fallback?: string) => string) => ([
  { title: t('codes.field.comCd'), field: 'comCd', editor: false, defaultValue: 'COM001' },
  { title: t('codes.field.serviceId'), field: 'serviceId', editor: false, defaultValue: 'TRADE' },
  { title: t('codes.field.codeGroupId'), field: 'codeGroupId', editor: false },
  { title: t('codes.field.codeId'), field: 'codeId', editor: 'input' },
  { title: t('codes.field.codeName'), field: 'codeName', editor: 'input' },
  { title: t('codes.field.parentCodeGroupId'), field: 'parentCodeGroupId', editor: false },
  { title: t('codes.field.parentCodeId'), field: 'parentCodeId', editor: 'select' },
  { title: t('codes.field.subInfo1'), field: 'subInfo1', editor: 'input' },
  { title: t('codes.field.subInfo2'), field: 'subInfo2', editor: 'input' },
  { title: t('codes.field.subInfo3'), field: 'subInfo3', editor: 'input' },
  { title: t('codes.field.sortSeq'), field: 'sortSeq', editor: 'number', defaultValue: 1 },
  { title: t('codes.field.enabled'), field: 'enabled', editor: 'tickCross', defaultValue: true },
]) satisfies DataGridColumn[]
