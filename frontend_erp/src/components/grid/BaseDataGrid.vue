<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref, shallowRef, watch } from 'vue'
import Button from 'primevue/button'
import Tag from 'primevue/tag'
import { useBreakpoints } from '@vueuse/core'
import { useConfirm } from 'primevue/useconfirm'
import { useToast } from 'primevue/usetoast'
import { TabulatorFull as Tabulator } from 'tabulator-tables'

import { useAppI18n } from '@/composables/useAppI18n'
import {
  buildBulkPayload,
  createBlankRow,
  normalizeGridRow,
  normalizeGridValue,
} from '@/utils/gridUtils'
import type { CrudPayload, DataGridColumn, GridRow, PermissionFlags } from '@/types/app'

const props = withDefaults(defineProps<{
  caption: string
  columns: DataGridColumn[]
  rows: Array<Record<string, unknown>>
  permissions: PermissionFlags
  rowDefaults?: Record<string, unknown>
  groupBy?: string
}>(), {
  rows: () => [],
  rowDefaults: () => ({}),
  groupBy: undefined,
})

const emit = defineEmits<{
  save: [payload: CrudPayload<Record<string, unknown>>]
}>()

const toast = useToast()
const confirm = useConfirm()
const { t } = useAppI18n()
const breakpoints = useBreakpoints({
  mobile: 0,
  desktop: 920,
})
const isMobile = breakpoints.smaller('desktop')
const tableRef = ref<HTMLDivElement | null>(null)
const table = shallowRef<any>(null)
const internalRows = ref<GridRow[]>([])
const selectedRowIds = ref<string[]>([])

const hasWrite = computed(() => props.permissions.permitWrite)
const hasDelete = computed(() => props.permissions.permitDelete)

const isBooleanColumn = (column: DataGridColumn) => column.editor === 'tickCross'

const normalizeRows = (rows: Array<Record<string, unknown>>) => rows.map((row) => normalizeGridRow(
  props.columns.reduce<Record<string, unknown>>((accumulator, column) => {
    accumulator[column.field] = normalizeGridValue(row[column.field], column)
    return accumulator
  }, { ...row }),
))

const syncRows = async () => {
  internalRows.value = normalizeRows(props.rows)

  if (table.value) {
    await table.value.replaceData(internalRows.value)
  }
}

const updateInternalRows = async () => {
  internalRows.value = ((table.value?.getData() ?? []) as Array<Record<string, unknown>>).map((row) => normalizeGridRow(row))
}

const markRowUpdated = (rowId: string) => {
  internalRows.value = internalRows.value.map((row) => {
    if (row.rowId !== rowId || row.__rowStatus === 'CREATED' || row.__rowStatus === 'DELETED') {
      return row
    }

    return {
      ...row,
      __rowStatus: 'UPDATED',
    }
  })
}

const buildSelectEditor = (column: DataGridColumn) => (
  (cell: { getValue: () => unknown }, onRendered: (callback: () => void) => void, success: (value: unknown) => void) => {
    const select = document.createElement('select')
    select.className = 'grid-inline-select'

    ;(column.editorOptions ?? []).forEach((option) => {
      const optionElement = document.createElement('option')
      optionElement.value = String(option.value ?? '')
      optionElement.textContent = option.label
      if (String(option.value ?? '') === String(cell.getValue() ?? '')) {
        optionElement.selected = true
      }
      select.append(optionElement)
    })

    onRendered(() => {
      select.focus()
    })

    select.addEventListener('change', () => {
      const matchedOption = (column.editorOptions ?? []).find((option) => String(option.value ?? '') === select.value)
      success(matchedOption?.value ?? select.value)
    })

    return select
  }
)

const buildCalendarEditor = () => (
  (cell: { getValue: () => unknown }, onRendered: (callback: () => void) => void, success: (value: unknown) => void) => {
    const input = document.createElement('input')
    input.type = 'datetime-local'
    input.className = 'grid-inline-calendar'
    const value = cell.getValue()

    if (value instanceof Date) {
      const iso = new Date(value.getTime() - value.getTimezoneOffset() * 60000).toISOString().slice(0, 16)
      input.value = iso
    } else if (value) {
      const normalized = new Date(String(value))
      if (!Number.isNaN(normalized.getTime())) {
        input.value = new Date(normalized.getTime() - normalized.getTimezoneOffset() * 60000).toISOString().slice(0, 16)
      }
    }

    onRendered(() => {
      input.focus()
    })

    input.addEventListener('change', () => {
      success(input.value ? new Date(input.value) : null)
    })

    return input
  }
)

const formatOptionValue = (column: DataGridColumn, value: unknown) => {
  const matchedOption = (column.editorOptions ?? []).find((option) => option.value === value || String(option.value) === String(value))
  return matchedOption?.label ?? value
}

const toggleBooleanValue = async (rowId: string, field: string) => {
  if (!hasWrite.value) {
    return
  }

  internalRows.value = internalRows.value.map((row) => {
    if (row.rowId !== rowId || row.__rowStatus === 'DELETED') {
      return row
    }

    return {
      ...row,
      [field]: !Boolean(row[field]),
      __rowStatus: row.__rowStatus === 'CREATED' ? 'CREATED' : 'UPDATED',
    }
  })

  if (table.value) {
    await table.value.replaceData(internalRows.value)
  }
}

const resolveEditor = (column: DataGridColumn) => {
  if (!hasWrite.value || column.editable === false || isBooleanColumn(column)) {
    return false
  }

  if (column.editor === 'select') {
    return buildSelectEditor(column)
  }

  if (column.editor === 'calendar') {
    return buildCalendarEditor()
  }

  return column.editor ?? 'input'
}

const createTable = async () => {
  if (!tableRef.value || isMobile.value) {
    return
  }

  table.value?.destroy()

  table.value = new Tabulator(tableRef.value, {
    data: internalRows.value,
    reactiveData: true,
    layout: 'fitColumns',
    index: 'rowId',
    groupBy: props.groupBy,
    selectableRows: true,
    rowHeight: 38,
    columns: [
      {
        formatter: 'rowSelection',
        titleFormatter: 'rowSelection',
        hozAlign: 'center',
        headerSort: false,
        width: 44,
        frozen: true,
        cellClick: (_event: unknown, cell: { getRow: () => { toggleSelect: () => void } }) => {
          cell.getRow().toggleSelect()
        },
      },
      {
        title: t('grid.status'),
        field: '__rowStatus',
        width: 80,
        hozAlign: 'center',
        formatter: (cell: { getValue: () => string }) => cell.getValue() === 'NONE' ? '' : cell.getValue(),
      },
      ...props.columns.map((column) => ({
        title: column.title,
        field: column.field,
        editor: resolveEditor(column),
        headerSort: false,
        hozAlign: column.hozAlign,
        width: column.width,
        formatter: isBooleanColumn(column)
          ? 'tickCross'
          : (cell: { getValue: () => unknown }) => column.editor === 'select'
              ? formatOptionValue(column, cell.getValue())
              : cell.getValue(),
        cellDblClick: async (_event: unknown, cell: { getRow: () => { getData: () => GridRow }; getField: () => string }) => {
          if (!isBooleanColumn(column)) {
            return
          }

          await toggleBooleanValue(cell.getRow().getData().rowId, cell.getField())
        },
      })),
    ],
    rowFormatter: (row: { getElement: () => HTMLElement; getData: () => GridRow }) => {
      row.getElement().classList.toggle('is-deleted-row', row.getData().__rowStatus === 'DELETED')
    },
    cellEdited: async (cell: { getRow: () => { getData: () => GridRow } }) => {
      markRowUpdated(cell.getRow().getData().rowId)
      await nextTick()
      await updateInternalRows()
    },
    rowSelectionChanged: (data: GridRow[]) => {
      selectedRowIds.value = data.map((row: GridRow) => row.rowId)
    },
  })
}

const handleAdd = async () => {
  if (!hasWrite.value) {
    return
  }

  const nextRow = {
    ...createBlankRow(props.columns),
    ...props.rowDefaults,
    __rowStatus: 'CREATED' as const,
  }

  internalRows.value = [nextRow, ...internalRows.value]

  if (table.value) {
    await table.value.replaceData(internalRows.value)
  }
}

const toggleDeleteSelected = () => {
  if (!hasDelete.value) {
    return
  }

  confirm.require({
    message: t('grid.deleteConfirmMessage'),
    header: t('grid.deleteConfirmTitle'),
    icon: 'pi pi-exclamation-triangle',
    acceptLabel: t('common.delete'),
    rejectLabel: t('common.close'),
    accept: async () => {
      const nextRows = internalRows.value
        .map((row) => {
          if (!selectedRowIds.value.includes(row.rowId)) {
            return row
          }

          if (row.__rowStatus === 'CREATED') {
            return null
          }

          return {
            ...row,
            __rowStatus: row.__rowStatus === 'DELETED' ? 'NONE' : 'DELETED',
          }
        })
        .filter((row): row is GridRow => row !== null)

      internalRows.value = nextRows
      selectedRowIds.value = []

      if (table.value) {
        table.value.deselectRow()
        await table.value.setData(internalRows.value)
      }
    },
  })
}

const handleSave = () => {
  const payload = buildBulkPayload(internalRows.value, props.columns)
  const changedRowCount = payload.added.length + payload.updated.length + payload.deleted.length

  if (!changedRowCount) {
    toast.add({
      severity: 'info',
      summary: t('grid.noChanges'),
      detail: t('grid.noChangesDetail'),
      life: 2000,
    })
    return
  }

  emit('save', payload)
}

const toggleSelection = (rowId: string, checked: boolean) => {
  selectedRowIds.value = checked
    ? [...selectedRowIds.value, rowId]
    : selectedRowIds.value.filter((value) => value !== rowId)
}

watch(
  () => props.rows,
  () => {
    void syncRows()
  },
  { deep: true, immediate: true },
)

watch(
  () => props.groupBy,
  async () => {
    await nextTick()
    await createTable()
  },
)

watch(isMobile, async () => {
  await nextTick()
  await createTable()
})

onMounted(async () => {
  await createTable()
})

onBeforeUnmount(() => {
  table.value?.destroy()
})
</script>

<template>
  <section class="base-grid">
    <div class="base-grid__toolbar-panel">
      <header class="base-grid__toolbar">
        <div>
          <h3>{{ t(caption, caption) }}</h3>
          <p>{{ t('grid.rows', undefined, { count: internalRows.length }) }}</p>
        </div>
        <div class="base-grid__actions">
          <Button icon="pi pi-plus" :label="t('grid.add')" size="small" :disabled="!hasWrite" @click="handleAdd" />
          <Button icon="pi pi-trash" :label="t('grid.deleteSelected')" size="small" severity="danger" :disabled="!hasDelete" @click="toggleDeleteSelected" />
          <Button icon="pi pi-save" :label="t('grid.save')" size="small" severity="contrast" :disabled="!hasWrite" @click="handleSave" />
        </div>
      </header>
    </div>
    <div class="base-grid__body-panel">
      <div class="base-grid__scroll">
        <div v-if="isMobile" class="grid-card-list">
          <article v-for="row in internalRows" :key="row.rowId" class="grid-card" :class="{ 'is-deleted-row': row.__rowStatus === 'DELETED' }">
            <label class="grid-card__select">
              <input
                type="checkbox"
                :checked="selectedRowIds.includes(row.rowId)"
                @change="toggleSelection(row.rowId, ($event.target as HTMLInputElement).checked)"
              />
              <span>{{ t('grid.select') }}</span>
            </label>
            <Tag v-if="row.__rowStatus !== 'NONE'" :value="row.__rowStatus" severity="secondary" />
            <dl>
              <template v-for="column in columns" :key="column.field">
                <dt>{{ t(column.title, column.title) }}</dt>
                <dd>{{ column.editor === 'select' ? formatOptionValue(column, row[column.field]) : row[column.field] }}</dd>
              </template>
            </dl>
          </article>
        </div>
        <div v-else ref="tableRef" class="tabulator-host" />
      </div>
    </div>
  </section>
</template>
