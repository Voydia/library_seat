<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  seat: { type: Object, default: null }
})
const emit = defineEmits(['submit'])

function pad(n) {
  return String(n).padStart(2, '0')
}

function toInputValue(date) {
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`
}

function buildDefault(offsetHours) {
  const d = new Date()
  d.setMinutes(0, 0, 0)
  d.setHours(d.getHours() + offsetHours)
  return toInputValue(d)
}

const startTime = ref(buildDefault(1))
const endTime = ref(buildDefault(3))
const errorMsg = ref('')

const seatLabel = computed(() => (props.seat ? props.seat.seatNo || `#${props.seat.id}` : ''))

function handleSubmit() {
  errorMsg.value = ''
  if (!props.seat) {
    errorMsg.value = '请先在左侧选择一个座位'
    return
  }
  if (!startTime.value || !endTime.value) {
    errorMsg.value = '请选择开始和结束时间'
    return
  }
  if (endTime.value <= startTime.value) {
    errorMsg.value = '结束时间必须晚于开始时间'
    return
  }
  emit('submit', {
    startTime: `${startTime.value}:00`,
    endTime: `${endTime.value}:00`
  })
}
</script>

<template>
  <section class="panel">
    <div class="panel-head">
      <h2>预约座位</h2>
      <span v-if="seat" class="hint">已选 {{ seatLabel }}</span>
      <span v-else class="hint">尚未选择</span>
    </div>

    <form class="reserve-form" @submit.prevent="handleSubmit">
      <label class="field">
        <span>开始时间</span>
        <input v-model="startTime" type="datetime-local" />
      </label>

      <label class="field">
        <span>结束时间</span>
        <input v-model="endTime" type="datetime-local" />
      </label>

      <p class="field-tip">开始后 15 分钟内需签到，否则座位会被自动释放。</p>
      <p v-if="errorMsg" class="form-error">{{ errorMsg }}</p>

      <button class="btn btn-primary btn-block" type="submit" :disabled="!seat">
        {{ seat ? `预约 ${seatLabel}` : '请先选择座位' }}
      </button>
    </form>
  </section>
</template>
