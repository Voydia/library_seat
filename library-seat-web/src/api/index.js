import axios from 'axios'

const http = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截：自动带上 token
http.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截：统一处理 Result 结构和 401
http.interceptors.response.use(
  res => {
    const body = res.data
    if (body && body.success === false) {
      return Promise.reject(new Error(body.errorMsg || '请求失败'))
    }
    return body
  },
  err => {
    if (err.response && err.response.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('phone')
      window.dispatchEvent(new CustomEvent('auth-expired'))
      return Promise.reject(new Error('登录已失效，请重新登录'))
    }
    const msg = err.response?.data?.errorMsg || err.message || '网络异常'
    return Promise.reject(new Error(msg))
  }
)

export const login = data => http.post('/user/login', data)
export const register = data => http.post('/user/register', data)
export const logout = () => http.post('/user/logout')

export const getSeats = () => http.get('/seat/all')
export const getMyReservations = () => http.get('/reservation/my')

export const createReservation = data => http.post('/reservation', data)
export const cancelReservation = id => http.delete(`/reservation/${id}`)
export const checkin = id => http.put(`/reservation/checkin/${id}`)
