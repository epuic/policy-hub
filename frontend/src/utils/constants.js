export const CLIENT_TYPES = [
  { value: 'INDIVIDUAL', label: 'Individual' },
  { value: 'COMPANY', label: 'Company' },
]

export const BUILDING_TYPES = [
  { value: 'RESIDENTIAL', label: 'Residential' },
  { value: 'OFFICE', label: 'Office' },
  { value: 'INDUSTRIAL', label: 'Industrial' },
]

export const POLICY_STATUSES = [
  { value: 'DRAFT', label: 'Draft' },
  { value: 'ACTIVE', label: 'Active' },
  { value: 'EXPIRED', label: 'Expired' },
  { value: 'CANCELLED', label: 'Cancelled' },
]

export const BROKER_STATUSES = [
  { value: 'ACTIVE', label: 'Active' },
  { value: 'INACTIVE', label: 'Inactive' },
]

export const FEE_TYPES = [
  { value: 'BROKER_COMMISSION', label: 'Broker commission' },
  { value: 'RISK_ADJUSTMENT', label: 'Risk adjustment' },
  { value: 'ADMIN_FEE', label: 'Administrative fee' },
]

export const RISK_FACTOR_TYPES = [
  { value: 'FLOOD_ZONE', label: 'Flood zone' },
  { value: 'EARTHQUAKE_RISK_ZONE', label: 'Earthquake risk zone' },
  { value: 'WINDSTORM_ZONE', label: 'Windstorm zone' },
  { value: 'LANDSLIDE_RISK', label: 'Landslide risk' },
]

export const RISK_FACTOR_CONFIG_LEVELS = [
  { value: 'COUNTRY', label: 'Country' },
  { value: 'COUNTY', label: 'County' },
  { value: 'CITY', label: 'City' },
  { value: 'BUILDING_TYPE', label: 'Building type' },
  { value: 'RISK_FACTOR_TYPE', label: 'Risk factor type' },
]

export function labelFor(list, value) {
  return list.find((x) => x.value === value)?.label || value
}
