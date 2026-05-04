export const CLIENT_TYPES = [
  { value: 'INDIVIDUAL', label: 'Persoană fizică' },
  { value: 'COMPANY', label: 'Companie' },
]

export const BUILDING_TYPES = [
  { value: 'RESIDENTIAL', label: 'Rezidențial' },
  { value: 'OFFICE', label: 'Birouri' },
  { value: 'INDUSTRIAL', label: 'Industrial' },
]

export const POLICY_STATUSES = [
  { value: 'DRAFT', label: 'Ciornă' },
  { value: 'ACTIVE', label: 'Activă' },
  { value: 'EXPIRED', label: 'Expirată' },
  { value: 'CANCELLED', label: 'Anulată' },
]

export const BROKER_STATUSES = [
  { value: 'ACTIVE', label: 'Activ' },
  { value: 'INACTIVE', label: 'Inactiv' },
]

export const FEE_TYPES = [
  { value: 'BROKER_COMMISSION', label: 'Comision broker' },
  { value: 'RISK_ADJUSTMENT', label: 'Ajustare risc' },
  { value: 'ADMIN_FEE', label: 'Taxă administrativă' },
]

export const RISK_FACTOR_TYPES = [
  { value: 'FLOOD_ZONE', label: 'Zonă inundații' },
  { value: 'EARTHQUAKE_RISK_ZONE', label: 'Zonă seismică' },
  { value: 'WINDSTORM_ZONE', label: 'Zonă vânturi puternice' },
  { value: 'LANDSLIDE_RISK', label: 'Risc alunecări teren' },
]

export const RISK_FACTOR_CONFIG_LEVELS = [
  { value: 'COUNTRY', label: 'Țară' },
  { value: 'COUNTY', label: 'Județ' },
  { value: 'CITY', label: 'Oraș' },
  { value: 'BUILDING_TYPE', label: 'Tip clădire' },
  { value: 'RISK_FACTOR_TYPE', label: 'Tip factor de risc' },
]

export function labelFor(list, value) {
  return list.find((x) => x.value === value)?.label || value
}
