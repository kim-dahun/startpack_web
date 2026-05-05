<script setup lang="ts">
import { ref, watch } from 'vue'
import Checkbox from 'primevue/checkbox'
import Button from 'primevue/button'

import BaseDialog from '@/components/common/BaseDialog.vue'
import { useAppI18n } from '@/composables/useAppI18n'

const props = defineProps<{
  visible: boolean
  accountNo: string
  itemCode: string
  quantity: number
  price: number
  side: 'BUY' | 'SELL'
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  confirm: []
}>()

const confirmed = ref(false)
const { t } = useAppI18n()

watch(
  () => props.visible,
  (visible) => {
    if (visible) {
      confirmed.value = false
    }
  },
)
</script>

<template>
  <BaseDialog :visible="visible" :title="t('trade.market.orderTitle')" @update:visible="emit('update:visible', $event)">
    <div class="trade-dialog-stack">
      <p>{{ t('trade.market.liveBanner') }}</p>
      <dl class="trade-detail-list">
        <dt>{{ t('trade.label.accountNo') }}</dt>
        <dd>{{ accountNo }}</dd>
        <dt>{{ t('trade.label.itemCode') }}</dt>
        <dd>{{ itemCode }}</dd>
        <dt>{{ t('trade.label.division') }}</dt>
        <dd>{{ side === 'BUY' ? 'BUY' : 'SELL' }}</dd>
        <dt>{{ t('trade.label.quantity') }}</dt>
        <dd>{{ quantity }}</dd>
        <dt>{{ t('trade.label.price') }}</dt>
        <dd>{{ price }}</dd>
      </dl>
      <label class="checkbox-line">
        <Checkbox v-model="confirmed" binary />
        <span>{{ t('trade.market.liveBanner') }}</span>
      </label>
      <div class="cluster-inline">
        <Button :label="t('common.close')" severity="secondary" @click="emit('update:visible', false)" />
        <Button :label="t('trade.action.executeOrder')" severity="danger" :disabled="!confirmed" @click="emit('confirm')" />
      </div>
    </div>
  </BaseDialog>
</template>
