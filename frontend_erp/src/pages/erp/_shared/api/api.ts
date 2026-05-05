import http from '@/api/client/http'
import type { CrudPayload } from '@/types/app'
import type { ApiEnvelope } from '@/types/app'
import type { ErpReferenceDefinition, ErpResourceDefinition, ErpResourceRow } from '@/types/erp'
import { ERP_BASE_PATH } from '@/pages/erp/_shared/model/pageModel'

const normalizeData = (value: unknown): ErpResourceRow[] => {
  if (Array.isArray(value)) {
    return value as ErpResourceRow[]
  }

  if (!value || typeof value !== 'object') {
    return []
  }

  return [value as ErpResourceRow]
}

const compactParams = (params: Record<string, string>) =>
  Object.fromEntries(Object.entries(params).filter(([, value]) => value !== ''))

export const fetchErpResourceRows = async (
  definition: ErpResourceDefinition,
  filters: Record<string, string>,
) => {
  if (!definition.listEndpoint && !definition.documentEndpoint) {
    return []
  }

  const documentKey = filters.documentKey || filters.barcodeValue

  if (definition.documentEndpoint && documentKey) {
    const response = await http.get<ApiEnvelope<unknown>>(`${definition.documentEndpoint}/${encodeURIComponent(documentKey)}`)
    return normalizeData(response.data.data)
  }

  const endpoint = definition.searchEndpoint && Object.values(filters).some(Boolean)
    ? definition.searchEndpoint
    : definition.listEndpoint

  if (!endpoint) {
    return []
  }

  const resolvedEndpoint = definition.key === 'productionConsumptions' && filters.productionResultId
    ? `${ERP_BASE_PATH}/production/results/${encodeURIComponent(filters.productionResultId)}/consumptions`
    : endpoint

  const response = await http.get<ApiEnvelope<unknown>>(resolvedEndpoint, {
    params: compactParams(filters),
  })

  return normalizeData(response.data.data)
}

const resolveByIdEndpoint = (endpoint: string, id: unknown) => `${endpoint}/${encodeURIComponent(String(id))}`

const resolveRowEndpoint = (endpoint: string, row: Record<string, unknown>) =>
  endpoint.replace(/\{(\w+)\}/g, (_matched, key: string) => {
    const value = row[key]

    if (value === undefined || value === null || value === '') {
      throw new Error(`Missing required path value: ${key}`)
    }

    return encodeURIComponent(String(value))
  })

const sanitizePayload = (row: Record<string, unknown>, definition: ErpResourceDefinition) =>
  definition.columns.reduce<Record<string, unknown>>((payload, column) => {
    if (column.editable === false || column.editor === false) {
      return payload
    }

    const value = row[column.field]
    if (value !== undefined && value !== '') {
      payload[column.field] = value
    }
    return payload
  }, {})

const buildSoftDeletePayload = (row: Record<string, unknown>, definition: ErpResourceDefinition) => {
  const field = definition.save?.softDeleteField

  if (!field) {
    return null
  }

  return {
    ...sanitizePayload(row, definition),
    [field]: definition.save?.softDeleteValue,
  }
}

export const saveErpResourceRows = async (
  definition: ErpResourceDefinition,
  payload: CrudPayload<Record<string, unknown>>,
  filters: Record<string, string>,
) => {
  const save = definition.save

  if (!save || save.readonly) {
    throw new Error(`Save API is not connected: ${definition.title}`)
  }

  if (save.createEndpoint) {
    for (const row of payload.added) {
      await http.post<ApiEnvelope<unknown>>(resolveRowEndpoint(save.createEndpoint, row), sanitizePayload(row, definition))
    }
  }

  if (payload.updated.length && !save.updateEndpoint) {
    throw new Error(`Update API is not defined: ${definition.title}`)
  }

  if (save.updateEndpoint) {
    for (const row of payload.updated) {
      const id = row[save.idField ?? 'id']
      if (!id) {
        throw new Error('The row to update has no id.')
      }
      await http.put<ApiEnvelope<unknown>>(resolveByIdEndpoint(save.updateEndpoint, id), sanitizePayload(row, definition))
    }
  }

  for (const row of payload.deleted) {
    const id = row[save.idField ?? 'id']
    if (!id) {
      continue
    }

    if (save.deleteEndpoint) {
      await http.delete<ApiEnvelope<unknown>>(resolveByIdEndpoint(save.deleteEndpoint, id))
      continue
    }

    if (save.updateEndpoint) {
      const softDeletePayload = buildSoftDeletePayload(row, definition)
      if (softDeletePayload) {
        await http.put<ApiEnvelope<unknown>>(resolveByIdEndpoint(save.updateEndpoint, id), softDeletePayload)
        continue
      }
    }

    throw new Error(`Delete API is not defined: ${definition.title}`)
  }

  return fetchErpResourceRows(definition, filters)
}

export const fetchErpReferenceRows = async (
  reference: ErpReferenceDefinition,
  filters: Record<string, string>,
) => {
  const response = await http.get<ApiEnvelope<unknown>>(reference.endpoint, {
    params: compactParams(filters),
  })

  return normalizeData(response.data.data)
}
