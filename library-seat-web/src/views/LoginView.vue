<script setup>
import { ref, reactive } from 'vue'
import { login, register } from '../api'

const emit = defineEmits(['logged-in'])

const mode = ref('login')
const loading = ref(false)
const errorMsg = ref('')

const form = reactive({
  phone: '',
  username: '',
  password: ''
})

function switchMode(next) {
  mode.value = next
  errorMsg.value = ''
}

async function handleSubmit() {
  errorMsg.value = ''

  if (!/^1[3-9]\d{9}$/.test(form.phone)) {
    errorMsg.value = '请输入正确的 11 位手机号'
    return
  }
  if (form.password.length < 4) {
    errorMsg.value = '密码至少 4 位'
    return
  }
  if (mode.value === 'register' && !/^[a-zA-Z0-9_-]{4,16}$/.test(form.username)) {
    errorMsg.value = '用户名需为 4-16 位字母、数字或下划线'
    return
  }

  loading.value = true
  try {
    if (mode.value === 'login') {
      const res = await login({ phone: form.phone, password: form.password })
      emit('logged-in', { token: res.data, phone: form.phone })
    } else {
      await register({
        phone: form.phone,
        username: form.username,
        password: form.password
      })
      mode.value = 'login'
      errorMsg.value = ''
      form.password = ''
      alert('注册成功，请登录')
    }
  } catch (e) {
    errorMsg.value = e.message
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-brand">
        <span class="auth-logo">座</span>
        <div>
          <h1>图书馆座位预约</h1>
          <p>选好座位，按时签到</p>
        </div>
      </div>

      <div class="auth-tabs">
        <button
          :class="['auth-tab', { active: mode === 'login' }]"
          @click="switchMode('login')"
        >
          登录
        </button>
        <button
          :class="['auth-tab', { active: mode === 'register' }]"
          @click="switchMode('register')"
        >
          注册
        </button>
      </div>

      <form class="auth-form" @submit.prevent="handleSubmit">
        <label class="field">
          <span>手机号</span>
          <input
            v-model.trim="form.phone"
            type="tel"
            maxlength="11"
            placeholder="请输入手机号"
            autocomplete="username"
          />
        </label>

        <label v-if="mode === 'register'" class="field">
          <span>用户名</span>
          <input
            v-model.trim="form.username"
            type="text"
            maxlength="16"
            placeholder="4-16 位字母、数字或下划线"
          />
        </label>

        <label class="field">
          <span>密码</span>
          <input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            autocomplete="current-password"
          />
        </label>

        <p v-if="errorMsg" class="form-error">{{ errorMsg }}</p>

        <button class="btn btn-primary btn-block" type="submit" :disabled="loading">
          {{ loading ? '处理中…' : mode === 'login' ? '登录' : '注册' }}
        </button>
      </form>
    </div>
  </div>
</template>
