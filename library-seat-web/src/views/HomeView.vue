<script setup>
import { ref, onMounted } from 'vue'
import SeatGrid from '../components/SeatGrid.vue'
import ReservationForm from '../components/ReservationForm.vue'
import ReservationList from '../components/ReservationList.vue'
import {
  getSeats,
  getMyReservations,
  createReservation,
  cancelReservation,
  checkin,
  logout as logoutApi
} from '../api'

defineProps({
  phone: { type: String, default: '' }
})
const emit = defineEmits(['logged-out'])

const seats = ref([])
const reservations = ref([])
const selectedSeat = ref(null)
const loading = ref(false)
const toast = ref({ show: false, type: 'success', text: '' })
let toastTimer = null

function showToast(text, type = 'success') {
  toast.value = { show: true, type, text }
  clearTimeout(toastTimer)
  toastTimer = setTimeout(() => {
    toast.value.show = false
  }, 2600)
}

async function refresh() {
  loading.value = true
  try {
    const [seatRes, reservationRes] = await Promise.all([getSeats(), getMyReservations()])
    seats.value = seatRes.data || []
    reservations.value = reservationRes.data || []
  } catch (e) {
    showToast(e.message, 'error')
  } finally {
    loading.value = false
  }
}

function handleSelect(seat) {
  selectedSeat.value = seat.id === selectedSeat.value?.id ? null : seat
}

async function handleSubmit({ startTime, endTime }) {
  if (!selectedSeat.value) {
    showToast('请先选择一个座位', 'error')
    return
  }
  try {
    await createReservation({
      seatId: selectedSeat.value.id,
      startTime,
      endTime
    })
    showToast('预约成功，请按时签到')
    selectedSeat.value = null
    await refresh()
  } catch (e) {
    showToast(e.message, 'error')
  }
}

async function handleCancel(id) {
  if (!window.confirm('确定取消这条预约吗？取消后座位会释放。')) {
    return
  }
  try {
    await cancelReservation(id)
    showToast('已取消预约')
    await refresh()
  } catch (e) {
    showToast(e.message, 'error')
  }
}

async function handleCheckin(id) {
  try {
    await checkin(id)
    showToast('签到成功')
    await refresh()
  } catch (e) {
    showToast(e.message, 'error')
  }
}

async function handleLogout() {
  try {
    await logoutApi()
  } catch (e) {
    // 即使请求失败也退出本地登录态
  }
  emit('logged-out')
}

onMounted(refresh)
</script>

<template>
  <div class="app-shell">
    <header class="topbar">
      <div class="brand">
        <span class="brand-mark">座</span>
        <span class="brand-name">图书馆座位预约</span>
      </div>
      <div class="user-area">
        <span class="user-phone">{{ phone || '已登录' }}</span>
        <button class="btn btn-ghost" @click="handleLogout">退出登录</button>
      </div>
    </header>

    <main class="layout">
      <section class="panel">
        <div class="panel-head">
          <h2>选择座位</h2>
          <span class="hint">{{ loading ? '加载中…' : '点击空闲座位后可预约' }}</span>
        </div>
        <SeatGrid
          :seats="seats"
          :selected-id="selectedSeat?.id"
          @select="handleSelect"
        />
      </section>

      <aside class="side">
        <ReservationForm :seat="selectedSeat" @submit="handleSubmit" />
        <ReservationList
          :reservations="reservations"
          @cancel="handleCancel"
          @checkin="handleCheckin"
        />
      </aside>
    </main>

    <transition name="toast">
      <div v-if="toast.show" :class="['toast', `toast-${toast.type}`]">
        {{ toast.text }}
      </div>
    </transition>
  </div>
</template>
