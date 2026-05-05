import type { MenuItem } from '@/types/app'

export interface MenuTreeNode extends MenuItem {
  depth: number
  isLeaf: boolean
  isSynthetic?: boolean
  favoriteOrder?: number
  children: MenuTreeNode[]
}

export const buildMenuTree = (menus: MenuItem[]) => {
  const nodes = new Map<string, MenuTreeNode>()
  const roots: MenuTreeNode[] = []

  menus
    .slice()
    .sort((left, right) => left.sortSeq - right.sortSeq)
    .forEach((menu) => {
      nodes.set(menu.menuId, {
        ...menu,
        depth: menu.menuLevel,
        isLeaf: true,
        children: [],
      })
    })

  nodes.forEach((node) => {
    if (node.parentMenuId && nodes.has(node.parentMenuId)) {
      const parentNode = nodes.get(node.parentMenuId)
      if (parentNode) {
        parentNode.isLeaf = false
        parentNode.children.push(node)
      }
      return
    }

    roots.push(node)
  })

  return roots
}

export const buildMenuLookup = (menus: MenuItem[]) => {
  const lookup = new Map<string, MenuItem>()
  menus.forEach((menu) => {
    lookup.set(menu.menuId, menu)
  })
  return lookup
}

export const findLeafMenuIds = (menus: MenuItem[]) => {
  const parentIds = new Set(
    menus
      .map((menu) => menu.parentMenuId)
      .filter((value): value is string => Boolean(value)),
  )

  return menus
    .filter((menu) => Boolean(menu.menuUrl) && !parentIds.has(menu.menuId))
    .map((menu) => menu.menuId)
}

export const findMenuAncestorIds = (menus: MenuItem[], menuId: string | null | undefined) => {
  if (!menuId) {
    return []
  }

  const lookup = buildMenuLookup(menus)
  const ancestors: string[] = []
  let current = lookup.get(menuId)

  while (current?.parentMenuId) {
    ancestors.push(current.parentMenuId)
    current = lookup.get(current.parentMenuId)
  }

  return ancestors
}

export const buildFavoriteMenuTree = (menus: MenuItem[], favoriteMenuIds: string[]) => {
  const lookup = buildMenuLookup(menus)
  const favoriteMenus = favoriteMenuIds.reduce<MenuTreeNode[]>((accumulator, menuId, index) => {
    const menu = lookup.get(menuId)

    if (!menu || !menu.menuUrl) {
      return accumulator
    }

    accumulator.push({
      ...menu,
      parentMenuId: 'FAVORITES_GROUP',
      menuLevel: 3,
      depth: 3,
      isLeaf: true,
      favoriteOrder: index,
      children: [],
    } satisfies MenuTreeNode)

    return accumulator
  }, [])

  if (!favoriteMenus.length) {
    return []
  }

  const favoritesGroup: MenuTreeNode = {
    menuId: 'FAVORITES_GROUP',
    parentMenuId: 'FAVORITES_ROOT',
    menuName: '즐겨찾기',
    menuUrl: '',
    i18nCode: 'menu.favoritesGroup',
    icon: 'pi pi-star',
    menuLevel: 2,
    sortSeq: 1,
    depth: 2,
    isLeaf: false,
    isSynthetic: true,
    children: favoriteMenus,
  }

  const favoritesRoot: MenuTreeNode = {
    menuId: 'FAVORITES_ROOT',
    parentMenuId: null,
    menuName: '즐겨찾기',
    menuUrl: '',
    i18nCode: 'menu.favoritesRoot',
    icon: 'pi pi-star-fill',
    menuLevel: 1,
    sortSeq: 0,
    depth: 1,
    isLeaf: false,
    isSynthetic: true,
    children: [favoritesGroup],
  }

  return [favoritesRoot]
}
