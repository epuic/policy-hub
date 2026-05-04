import { createContext, useContext, useEffect, useState } from 'react'
import api from '../lib/api'
import { decodeJwt, isTokenExpired, rolesToPrimary } from '../lib/auth'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [token, setToken] = useState(null)
  const [ready, setReady] = useState(false)

  useEffect(() => {
    const t = localStorage.getItem('insurance_token')
    const u = localStorage.getItem('insurance_user')
    if (t && !isTokenExpired(t)) {
      setToken(t)
      setUser(u ? JSON.parse(u) : null)
    } else {
      localStorage.removeItem('insurance_token')
      localStorage.removeItem('insurance_user')
    }
    setReady(true)
  }, [])

  const login = async (email, password) => {
    const { data } = await api.post('/auth/login', { email, password })
    const decoded = decodeJwt(data.token) || {}
    const sessionUser = {
      email: data.email,
      roles: data.roles || [],
      role: rolesToPrimary(data.roles || []),
      entityId: decoded.entityId,
      entityType: decoded.entityType,
    }
    localStorage.setItem('insurance_token', data.token)
    localStorage.setItem('insurance_user', JSON.stringify(sessionUser))
    setToken(data.token)
    setUser(sessionUser)
    return sessionUser
  }

  const logout = () => {
    localStorage.removeItem('insurance_token')
    localStorage.removeItem('insurance_user')
    setUser(null)
    setToken(null)
  }

  return (
    <AuthContext.Provider value={{ user, token, ready, login, logout, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider')
  return ctx
}
