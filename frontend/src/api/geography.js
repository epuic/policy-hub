import api from '../lib/api'

export const geographyApi = {
  countries: (params = {}) =>
    api.get('/brokers/countries', { params }).then((r) => r.data),
  counties: (countryId, params = {}) =>
    api
      .get(`/brokers/countries/${countryId}/counties`, { params })
      .then((r) => r.data),
  cities: (countyId, params = {}) =>
    api
      .get(`/brokers/counties/${countyId}/cities`, { params })
      .then((r) => r.data),
}
