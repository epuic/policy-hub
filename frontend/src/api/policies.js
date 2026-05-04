import api from '../lib/api'

export const policiesApi = {
  list: (params = {}) =>
    api.get('/v2/brokers/policies', { params }).then((r) => r.data),
  get: (id) => api.get(`/v2/brokers/policies/${id}`).then((r) => r.data),
  create: (body) => api.post('/v2/brokers/policies', body).then((r) => r.data),
  activate: (id) =>
    api.post(`/v2/brokers/policies/${id}/activate`).then((r) => r.data),
  cancel: (id, reason) =>
    api
      .post(`/v2/brokers/policies/${id}/cancel`, { reason })
      .then((r) => r.data),
}
