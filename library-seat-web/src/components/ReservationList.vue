<script setup>
const props = defineProps({
  reservations: { type: Array, default: () => [] }
})
const emit = defineEmits(['cancel', 'checkin'])

const STATUS_MAP = {
  0: { text: '待签到', cls: 'tag-pending' },
  1: { text: '已签到', cls: 'tag-checked' },
  2: { text: '已取消', cls: 'tag-muted' },
  3: { text: '已释放', cls: 'tag-muted' }
}

function statusOf(status) {
  return STATUS_MAP[status] || { text: '未知', cls: 'tag-muted' }
}

function formatTime(value) {
  if (!value) {
    return '-'
  }
  return value.replace('T', ' ').slice(5, 16)
}
</script>

<template>
  <section class="panel">
    <div class="panel-head">
      <h2>我的预约</h2>
      <span class="hint">共 {{ reservations.length }} 条</span>
    </div>

    <div v-if="!reservations.length" class="empty">还没有预约记录</div>

    <ul v-else class="reservation-list">
      <li v-for="item in reservations" :key="item.id" class="reservation-item">
        <div class="res-head">
          <span class="res-seat">座位 #{{ item.seatId }}</span>
          <span :class="['tag', statusOf(item.status).cls]">
            {{ statusOf(item.status).text }}
          </span>
        </div>

        <div class="res-time">
          {{ formatTime(item.startTime) }} — {{ formatTime(item.endTime) }}
        </div>

        <div v-if="item.status === 0" class="res-actions">
          <button class="btn btn-primary btn-sm" @click="emit('checkin', item.id)">
            签到
          </button>
          <button class="btn btn-ghost btn-sm" @click="emit('cancel', item.id)">
            取消
          </button>
        </div>
      </li>
    </ul>
  </section>
</template>
