import type { TreeNode } from 'primevue/treenode'

import type { UserMenuRow } from '../api/api'

export interface MenuEditorForm {
  menuId: string
  menuName: string
  menuUrl: string
  i18nCode: string
  icon: string
  sortSeq: number
  enabled: boolean
}

export interface UserMenuTreeNode extends TreeNode {
  key: string
  label: string
  data: UserMenuRow
  children?: UserMenuTreeNode[]
}

export const createEmptyMenuForm = (): MenuEditorForm => ({
  menuId: '',
  menuName: '',
  menuUrl: '',
  i18nCode: '',
  icon: 'pi pi-circle',
  sortSeq: 1,
  enabled: true,
})

const normalizeMenuRow = (row: UserMenuRow): UserMenuRow => ({
  ...row,
  menuParentId: row.menuParentId ?? '',
  children: (row.children ?? []).map(normalizeMenuRow),
})

export const normalizeMenuTree = (rows: UserMenuRow[]) => rows.map(normalizeMenuRow)

export const buildMenuTreeNodes = (rows: UserMenuRow[]): UserMenuTreeNode[] =>
  rows
    .slice()
    .sort((left, right) => left.sortSeq - right.sortSeq)
    .map((row) => ({
      key: row.menuId,
      label: row.menuName,
      data: row,
      children: buildMenuTreeNodes(row.children ?? []),
    }))

export const collectExpandedKeys = (rows: UserMenuRow[], result: Record<string, boolean> = {}) => {
  rows.forEach((row) => {
    if (row.children?.length) {
      result[row.menuId] = true
      collectExpandedKeys(row.children, result)
    }
  })

  return result
}

export const filterMenuTree = (rows: UserMenuRow[], keyword: string): UserMenuRow[] => {
  const normalized = keyword.trim().toLowerCase()

  if (!normalized) {
    return rows
  }

  return rows.reduce<UserMenuRow[]>((accumulator, row) => {
    const children = filterMenuTree(row.children ?? [], keyword)
    const matched = [row.menuId, row.menuName, row.menuUrl, row.i18nCode].some((value) =>
      String(value ?? '').toLowerCase().includes(normalized),
    )

    if (matched || children.length) {
      accumulator.push({
        ...row,
        children,
      })
    }

    return accumulator
  }, [])
}

export const flattenMenus = (rows: UserMenuRow[]): UserMenuRow[] =>
  rows.flatMap((row) => [row, ...flattenMenus(row.children ?? [])])
