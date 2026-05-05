import type { DataGridColumn } from '@/types/app'

export interface ErpReferenceDefinition {
  key: string
  title: string
  endpoint: string
  columns: string[]
  filters: Array<{
    key: string
    label: string
    placeholder: string
  }>
}

export interface ErpSaveDefinition {
  createEndpoint?: string
  updateEndpoint?: string
  deleteEndpoint?: string
  idField?: string
  readonly?: boolean
  softDeleteField?: string
  softDeleteValue?: string | boolean
}

export interface ErpResourceDefinition {
  key: string
  title: string
  description: string
  listEndpoint?: string
  searchEndpoint?: string
  documentEndpoint?: string
  primaryAction: string
  columns: DataGridColumn[]
  filters: Array<{
    key: string
    label: string
    placeholder: string
  }>
  rowDefaults?: Record<string, unknown>
  groupBy?: string
  save?: ErpSaveDefinition
  references?: ErpReferenceDefinition[]
}

export type ErpResourceRow = Record<string, unknown>
