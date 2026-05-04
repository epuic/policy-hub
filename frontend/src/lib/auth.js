export function decodeJwt(token) {
  if (!token) return null
  try {
    const payload = token.split('.')[1]
    const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'))
    return JSON.parse(decodeURIComponent(escape(decoded)))
  } catch {
    return null
  }
}

export function isTokenExpired(token) {
  const decoded = decodeJwt(token)
  if (!decoded?.exp) return true
  return Date.now() >= decoded.exp * 1000
}

export function rolesToPrimary(roles = []) {
  const r = roles.map((x) => String(x).toUpperCase())
  if (r.some((x) => x.includes('ADMIN') || x.includes('MANAGER'))) return 'ADMIN'
  if (r.some((x) => x.includes('BROKER'))) return 'BROKER'
  return 'USER'
}
