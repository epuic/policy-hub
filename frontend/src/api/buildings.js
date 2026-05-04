import api from '../lib/api'

export const buildingsApi = {
  listByClient: (clientId, params = {}) =>
    api.get(`/brokers/clients/${clientId}/buildings`, { params }).then((r) => r.data),
  listByClientWithPolicies: (clientId, params = {}) =>
    api.get(`/v2/brokers/clients/${clientId}/buildings`, { params }).then((r) => r.data),
  get: (buildingId) =>
    api.get(`/brokers/buildings/${buildingId}`).then((r) => r.data),
  getWithPolicies: (buildingId) =>
    api.get(`/v2/brokers/buildings/${buildingId}`).then((r) => r.data),
  create: (clientId, body) =>
    api.post(`/brokers/clients/${clientId}/buildings`, body).then((r) => r.data),
  update: (buildingId, body) =>
    api.put(`/brokers/buildings/${buildingId}`, body).then((r) => r.data),
}
