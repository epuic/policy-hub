import api from '../lib/api'

export const currenciesApi = {
  list: (params = {}) =>
    api.get('/v2/admin/currencies', { params }).then((r) => r.data),
  get: (id) => api.get(`/v2/admin/currencies/${id}`).then((r) => r.data),
  create: (body) => api.post('/v2/admin/currencies', body).then((r) => r.data),
  update: (id, body) =>
    api.put(`/v2/admin/currencies/${id}`, body).then((r) => r.data),
}
