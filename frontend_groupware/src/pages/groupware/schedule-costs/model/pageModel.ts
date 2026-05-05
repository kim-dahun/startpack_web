import type { GroupwareScheduleCost } from '@/types/groupware'

export const costColumns = [
  { field: 'projectCode', title: 'Project' },
  { field: 'costDate', title: 'Cost Date' },
  { field: 'amount', title: 'Amount' },
  { field: 'description', title: 'Description' },
]

export const toCostRows = (costs: GroupwareScheduleCost[]) =>
  costs.map((item) => ({
    scheduleCostId: item.scheduleCostId,
    projectCode: item.projectCode || '-',
    costDate: item.costDate,
    amount: item.amount,
    description: item.description || '-',
  }))
