import { getManagementRows, saveManagementRows } from '@/api/modules/user'
import type { CrudPayload, ManagementQueryParams } from '@/types/app'

const resourceKey = 'groupMembers'

export const listRows = async (params: ManagementQueryParams) => getManagementRows(resourceKey, params)

export const saveRows = async (
  payload: CrudPayload<Record<string, unknown>>,
  params: ManagementQueryParams,
) => saveManagementRows(resourceKey, payload, params)
