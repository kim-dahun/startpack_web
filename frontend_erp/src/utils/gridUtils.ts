import type { CrudPayload, DataGridColumn, GridRow } from '@/types/app'

export const createRowId = () => crypto.randomUUID()

export const normalizeGridRow = (row: Record<string, unknown>): GridRow => ({
  rowId: typeof row.rowId === 'string' ? row.rowId : createRowId(),
  __rowStatus: (row.__rowStatus as GridRow['__rowStatus']) ?? 'NONE',
  ...row,
})

export const createBlankRow = (columns: DataGridColumn[]) => {
  const initial = columns.reduce<Record<string, unknown>>((accumulator, column) => {
    accumulator[column.field] = column.defaultValue ?? ''
    return accumulator
  }, {})

  return normalizeGridRow(initial)
}

export const stripGridMeta = <T extends Record<string, unknown>>(row: T) => {
  const { rowId, __rowStatus, ...rest } = row
  void rowId
  void __rowStatus
  return rest
}

const pad = (value: number) => String(value).padStart(2, '0')

export const formatGridDateTime = (input: Date | string | null | undefined) => {
  if (!input) {
    return null
  }

  const date = input instanceof Date ? input : new Date(input)

  if (Number.isNaN(date.getTime())) {
    return input
  }

  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

export const normalizeGridValue = (value: unknown, column: DataGridColumn) => {
  if (column.editor === 'calendar') {
    if (!value) {
      return null
    }

    return value instanceof Date ? value : new Date(String(value))
  }

  return value
}

export const serializeGridValue = (value: unknown, column: DataGridColumn) => {
  if (column.editor === 'calendar') {
    return formatGridDateTime(value as Date | string | null | undefined)
  }

  return value
}

export const serializeGridRow = (row: Record<string, unknown>, columns: DataGridColumn[]) => columns.reduce<Record<string, unknown>>((accumulator, column) => {
  accumulator[column.field] = serializeGridValue(row[column.field], column)
  return accumulator
}, {})

export const buildBulkPayload = <T extends Record<string, unknown>>(
  rows: Array<T & GridRow>,
  columns?: DataGridColumn[],
): CrudPayload<T> => {
  const payload: CrudPayload<T> = {
    added: [],
    updated: [],
    deleted: [],
  }

  const serializeRow = (row: T & GridRow) => {
    const stripped = stripGridMeta(row) as Record<string, unknown>
    return (columns ? serializeGridRow(stripped, columns) : stripped) as T
  }

  rows.forEach((row) => {
    if (row.__rowStatus === 'CREATED') {
      payload.added.push(serializeRow(row))
      return
    }

    if (row.__rowStatus === 'UPDATED') {
      payload.updated.push(serializeRow(row))
      return
    }

    if (row.__rowStatus === 'DELETED') {
      payload.deleted.push(serializeRow(row))
    }
  })

  return payload
}
