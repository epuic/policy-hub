import api from '../lib/api'

export const aiApi = {
  runBuildingClustering: (k = 4) =>
    api.post('/v2/admin/ai/clusters/buildings/run', null, { params: { k } }).then((r) => r.data),
  runClientClustering: (k = 4) =>
    api.post('/v2/admin/ai/clusters/clients/run', null, { params: { k } }).then((r) => r.data),
  buildingAssignments: () =>
    api.get('/v2/admin/ai/clusters/buildings').then((r) => r.data),
  clientAssignments: () =>
    api.get('/v2/admin/ai/clusters/clients').then((r) => r.data),
  buildingConfigurations: () =>
    api.get('/v2/admin/ai/clusters/buildings/configurations').then((r) => r.data),
  clientConfigurations: () =>
    api.get('/v2/admin/ai/clusters/clients/configurations').then((r) => r.data),
  buildingAnalytics: () =>
    api.get('/v2/admin/ai/clusters/buildings/analytics').then((r) => r.data),
  clientAnalytics: () =>
    api.get('/v2/admin/ai/clusters/clients/analytics').then((r) => r.data),
  updateBuildingConfiguration: (clusterId, body) =>
    api.put(`/v2/admin/ai/clusters/buildings/configurations/${clusterId}`, body).then((r) => r.data),
  updateClientConfiguration: (clusterId, body) =>
    api.put(`/v2/admin/ai/clusters/clients/configurations/${clusterId}`, body).then((r) => r.data),
}
