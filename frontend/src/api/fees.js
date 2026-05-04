import api from '../lib/api'

export const feesApi = {
  list: (params = {}) => api.get('/v2/admin/fees', { params }).then((r) => r.data),
  get: (id) => api.get(`/v2/admin/fees/${id}`).then((r) => r.data),
  create: (body) => api.post('/v2/admin/fees', body).then((r) => r.data),
  update: (id, body) => api.put(`/v2/admin/fees/${id}`, body).then((r) => r.data),
}
