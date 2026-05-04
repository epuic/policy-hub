import api from '../lib/api'

export const reportsApi = {
  byCountry: (params = {}) =>
    api.get('/v2/admin/reports/policies-by-country', { params }).then((r) => r.data),
  byCounty: (params = {}) =>
    api.get('/v2/admin/reports/policies-by-county', { params }).then((r) => r.data),
  byCity: (params = {}) =>
    api.get('/v2/admin/reports/policies-by-city', { params }).then((r) => r.data),
  byBroker: (params = {}) =>
    api.get('/v2/admin/reports/policies-by-broker', { params }).then((r) => r.data),
}
