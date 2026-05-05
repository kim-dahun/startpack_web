import { getMenuTreeRows, saveMenuTreeRows } from '@/api/modules/user'
import type { CrudPayload } from '@/types/app'

export interface UserMenuRow {
  comCd: string
  serviceId: string
  menuId: string
  menuParentId: string
  menuName: string
  menuUrl: string
  i18nCode: string
  icon: string
  menuLevel: number
  sortSeq: number
  enabled: boolean
  children?: UserMenuRow[]
}

export const listMenuTree = async (params: { comCd: string; serviceId: string }) =>
  getMenuTreeRows(params) as Promise<UserMenuRow[]>

export const saveMenuTree = async (
  payload: CrudPayload<Record<string, unknown>>,
  params: { comCd: string; serviceId: string },
) => saveMenuTreeRows(payload, params) as Promise<UserMenuRow[]>
