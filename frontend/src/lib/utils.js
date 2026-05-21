import clsx from 'clsx'

export function cn(...inputs) {
  return clsx(inputs)
}

export function formatDate(value) {
  if (!value) return '-'
  try {
    const d = typeof value === 'string' ? new Date(value) : value
    return d.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: '2-digit' })
  } catch {
    return String(value)
  }
}

export function formatDateTime(value) {
  if (!value) return '-'
  try {
    const d = typeof value === 'string' ? new Date(value) : value
    return d.toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
    })
  } catch {
    return String(value)
  }
}

export function formatMoney(amount, currency = 'RON') {
  if (amount === null || amount === undefined) return '-'
  const n = typeof amount === 'string' ? parseFloat(amount) : amount
  if (Number.isNaN(n)) return '-'
  try {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency,
      maximumFractionDigits: 2,
    }).format(n)
  } catch {
    return `${n.toFixed(2)} ${currency}`
  }
}

export function formatNumber(n) {
  if (n === null || n === undefined) return '-'
  return new Intl.NumberFormat('en-US').format(n)
}

export function numberOrNull(value) {
  if (value === null || value === undefined || value === '') return null
  const number = Number(value)
  return Number.isNaN(number) ? null : number
}

export function initials(name = '') {
  return name
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((s) => s[0]?.toUpperCase())
    .join('')
}
