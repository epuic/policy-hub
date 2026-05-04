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

export function apiError(err, fallback = 'A apărut o eroare') {
  if (err?.response?.data?.message) return err.response.data.message
  if (err?.response?.data?.details?.length)
    return err.response.data.details.join(', ')
  if (err?.message) return err.message
  return fallback
}

export default api
