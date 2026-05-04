import api from '../lib/api'

export const brokersApi = {
  list: (params = {}) =>
    api.get('/v2/admin/brokers', { params }).then((r) => r.data),
  get: (id) => api.get(`/v2/admin/brokers/${id}`).then((r) => r.data),
  create: (body) => api.post('/v2/admin/brokers', body).then((r) => r.data),
  update: (id, body) =>
    api.put(`/v2/admin/brokers/${id}`, body).then((r) => r.data),
  activate: (id) =>
    api.post(`/v2/admin/brokers/${id}/activate`).then((r) => r.data),
  deactivate: (id) =>
    api.post(`/v2/admin/brokers/${id}/deactivate`).then((r) => r.data),
}
