import api from '../lib/api'

export const riskFactorsApi = {
  list: (params = {}) =>
    api.get('/v2/admin/risk-factors', { params }).then((r) => r.data),
  get: (id) => api.get(`/v2/admin/risk-factors/${id}`).then((r) => r.data),
  create: (body) =>
    api.post('/v2/admin/risk-factors', body).then((r) => r.data),
  update: (id, body) =>
    api.put(`/v2/admin/risk-factors/${id}`, body).then((r) => r.data),
}
