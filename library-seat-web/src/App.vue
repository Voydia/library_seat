<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import LoginView from './views/LoginView.vue'
import HomeView from './views/HomeView.vue'

const token = ref(localStorage.getItem('token') || '')
const phone = ref(localStorage.getItem('phone') || '')

function handleLoggedIn({ token: t, phone: p }) {
  token.value = t
  phone.value = p
  localStorage.setItem('token', t)
  localStorage.setItem('phone', p)
}

function handleLoggedOut() {
  token.value = ''
  phone.value = ''
  localStorage.removeItem('token')
  localStorage.removeItem('phone')
}

function handleAuthExpired() {
  handleLoggedOut()
}

onMounted(() => window.addEventListener('auth-expired', handleAuthExpired))
onUnmounted(() => window.removeEventListener('auth-expired', handleAuthExpired))
</script>

<template>
  <LoginView v-if="!token" @logged-in="handleLoggedIn" />
  <HomeView v-else :phone="phone" @logged-out="handleLoggedOut" />
</template>
