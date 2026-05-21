import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('insurance_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      const path = window.location.pathname
      if (path !== '/login') {
        localStorage.removeItem('insurance_token')
        localStorage.removeItem('insurance_user')
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  },
)

export function apiError(err, fallback = 'An error occurred') {
  const response = err?.response?.data
  if (response?.message && response?.details?.length) {
    return `${response.message}\n${response.details.filter(Boolean).join('\n')}`
  }
  if (response?.message) return response.message
  if (response?.details?.length) return response.details.filter(Boolean).join('\n')
  if (response?.error) return response.error
  if (err?.message) return err.message
  return fallback
}

export default api
