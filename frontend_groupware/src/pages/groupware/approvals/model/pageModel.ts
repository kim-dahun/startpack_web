import type {
  GroupwareApprovalDocument,
  GroupwareApprovalLine,
  GroupwareApprovalLineTemplate,
} from '@/types/groupware'

export const approvalLineColumns = [
  { field: 'approvalRoleType', title: 'Role' },
  { field: 'lineStage', title: 'Stage' },
  { field: 'lineSeq', title: 'Seq' },
  { field: 'targetType', title: 'Target Type' },
  { field: 'targetUserId', title: 'Target User' },
]

export const filterDocumentsByMode = (
  documents: GroupwareApprovalDocument[],
  viewMode: 'drafts' | 'inbox' | 'lines',
) => {
  if (viewMode === 'drafts') {
    return documents.filter((item) => ['DRAFT', 'SUBMITTED'].includes(item.status))
  }

  return documents
}

export const toApprovalLineRows = (lines: GroupwareApprovalLine[]) => lines as unknown as Array<Record<string, unknown>>
export const toTemplateLabels = (templates: GroupwareApprovalLineTemplate[]) =>
  templates.map((template) => `${template.templateName} (${template.templateId})`)
