import api from '../lib/api'

export const clientsApi = {
  list: (params = {}) => api.get('/brokers/clients', { params }).then((r) => r.data),
  search: (params = {}) => api.get('/brokers/clients/search', { params }).then((r) => r.data),
  get: (id, params = {}) =>
    api.get(`/brokers/clients/${id}`, { params }).then((r) => r.data),
  create: (body) => api.post('/brokers/clients', body).then((r) => r.data),
  update: (id, body) => api.put(`/brokers/clients/${id}`, body).then((r) => r.data),
}
