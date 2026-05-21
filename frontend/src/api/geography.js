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
  adminCountries: (params = {}) =>
    api.get('/v2/admin/geography/countries', { params }).then((r) => r.data),
  adminCounties: (countryId, params = {}) =>
    api
      .get(`/v2/admin/geography/countries/${countryId}/counties`, { params })
      .then((r) => r.data),
  adminCities: (countyId, params = {}) =>
    api
      .get(`/v2/admin/geography/counties/${countyId}/cities`, { params })
      .then((r) => r.data),
}
