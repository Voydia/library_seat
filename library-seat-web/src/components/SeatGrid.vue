<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  seats: { type: Array, default: () => [] },
  selectedId: { type: Number, default: null }
})
const emit = defineEmits(['select'])

const activeFloor = ref(null)

const floors = computed(() => {
  const set = props.seats
    .map(s => s.floor)
    .filter(f => f !== null && f !== undefined)
    .map(Number)
  return [...new Set(set)].sort((a, b) => a - b)
})

watch(
  floors,
  list => {
    if (list.length && !list.includes(activeFloor.value)) {
      activeFloor.value = list[0]
    }
  },
  { immediate: true }
)

const visibleSeats = computed(() =>
  props.seats.filter(s => Number(s.floor) === activeFloor.value)
)

const freeCount = computed(() => visibleSeats.value.filter(s => s.status === 0).length)
</script>

<template>
  <div class="seat-grid-wrap">
    <div class="floor-tabs">
      <button
        v-for="floor in floors"
        :key="floor"
        :class="['floor-tab', { active: floor === activeFloor }]"
        @click="activeFloor = floor"
      >
        {{ floor }} 楼
      </button>
      <span v-if="floors.length" class="floor-summary">
        空闲 {{ freeCount }} / {{ visibleSeats.length }}
      </span>
    </div>

    <div v-if="!seats.length" class="empty">暂无座位数据</div>

    <div v-else class="seat-grid">
      <button
        v-for="seat in visibleSeats"
        :key="seat.id"
        :class="[
          'seat-card',
          {
            'is-busy': seat.status === 1,
            'is-selected': seat.id === selectedId
          }
        ]"
        :disabled="seat.status === 1"
        @click="emit('select', seat)"
      >
        <span class="seat-no">{{ seat.seatNo }}</span>
        <span class="seat-state">{{ seat.status === 1 ? '已占用' : '空闲' }}</span>
      </button>
    </div>
  </div>
</template>
