import type { TreeNode } from 'primevue/treenode'

import type { MenuPermissionTreeRow } from '../api/api'

export const permissionFields = ['permitRead', 'permitWrite', 'permitDelete', 'permitExcel'] as const
export type PermissionField = (typeof permissionFields)[number]

export interface PermissionTreeNode extends TreeNode {
  key: string
  label: string
  data: MenuPermissionTreeRow
  children?: PermissionTreeNode[]
}

export const clonePermissionTree = (rows: MenuPermissionTreeRow[]): MenuPermissionTreeRow[] =>
  rows.map((row) => ({
    ...row,
    children: clonePermissionTree(row.children ?? []),
  }))

export const buildPermissionTreeNodes = (rows: MenuPermissionTreeRow[]): PermissionTreeNode[] =>
  rows.map((row) => ({
    key: row.menuId,
    label: row.menuName,
    data: row,
    children: buildPermissionTreeNodes(row.children ?? []),
  }))

export const collectExpandedKeys = (rows: MenuPermissionTreeRow[], result: Record<string, boolean> = {}) => {
  rows.forEach((row) => {
    if (row.children?.length) {
      result[row.menuId] = true
      collectExpandedKeys(row.children, result)
    }
  })

  return result
}

export const flattenPermissionTree = (rows: MenuPermissionTreeRow[]): MenuPermissionTreeRow[] =>
  rows.flatMap((row) => [row, ...flattenPermissionTree(row.children ?? [])])

export const filterPermissionTree = (rows: MenuPermissionTreeRow[], keyword: string): MenuPermissionTreeRow[] => {
  const normalized = keyword.trim().toLowerCase()

  if (!normalized) {
    return rows
  }

  return rows.reduce<MenuPermissionTreeRow[]>((accumulator, row) => {
    const children = filterPermissionTree(row.children ?? [], keyword)
    const matched = [row.menuId, row.menuName].some((value) => String(value).toLowerCase().includes(normalized))

    if (matched || children.length) {
      accumulator.push({
        ...row,
        children,
      })
    }

    return accumulator
  }, [])
}
