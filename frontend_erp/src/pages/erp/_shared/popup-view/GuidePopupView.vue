<script setup lang="ts">
import { computed } from 'vue'

import BaseDialog from '@/components/common/BaseDialog.vue'
import { useAppI18n } from '@/composables/useAppI18n'
import type { ErpResourceDefinition } from '@/types/erp'

const props = defineProps<{
  visible: boolean
  definition: ErpResourceDefinition
  endpointText: string
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
}>()

const { t } = useAppI18n()
const title = computed(() => `${t(props.definition.title, props.definition.title)} ${t('erp.apiConnection')}`)
</script>

<template>
  <BaseDialog
    :visible="visible"
    :title="title"
    @update:visible="emit('update:visible', $event)"
  >
    <div class="form-popup-stack">
      <dl class="erp-detail-list">
        <div>
          <dt>{{ t('erp.baseApi') }}</dt>
          <dd>{{ endpointText }}</dd>
        </div>
        <div>
          <dt>{{ t('erp.gateway') }}</dt>
          <dd>{{ t('erp.gatewayDetail') }}</dd>
        </div>
        <div>
          <dt>{{ t('erp.pagePackage') }}</dt>
          <dd>{{ t('erp.pagePackageDetail') }}</dd>
        </div>
      </dl>
    </div>
  </BaseDialog>
</template>
