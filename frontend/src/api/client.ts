import axios from 'axios'

const API_BASE = import.meta.env.VITE_API_BASE || '/api'

export const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (r) => r,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      if (!window.location.pathname.startsWith('/login')) {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

// ---------- Auth ----------
export const registerUser = (data: { username: string; email: string; password: string }) =>
  api.post('/auth/register', data).then((r) => r.data)

export const loginUser = (data: { username: string; password: string }) =>
  api.post('/auth/login', data).then((r) => r.data)

// ---------- Routing ----------
export const computeRoute = (data: {
  startId: number
  endId: number
  wheelchair?: boolean
  peakHours?: boolean
}) => api.post('/route', data).then((r) => r.data)

export const computeMultiStop = (data: {
  startId: number
  stopIds: number[]
  endId: number
  wheelchair?: boolean
  peakHours?: boolean
}) => api.post('/route/multi-stop', data).then((r) => r.data)

export const nearestPoi = (fromNode: number, type: string, wheelchair = false) =>
  api
    .get('/poi/nearest', { params: { fromNode, type, wheelchair } })
    .then((r) => r.data)

// ---------- Monitoring ----------
export const getCacheStats = () => api.get('/monitoring/cache-stats').then((r) => r.data)

// ---------- Admin ----------
export const reloadGraph = () => api.post('/admin/reload-graph').then((r) => r.data)
export const invalidateCache = () => api.post('/admin/invalidate-cache').then((r) => r.data)