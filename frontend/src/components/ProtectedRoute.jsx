import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import Spinner from './ui/Spinner'

export default function ProtectedRoute({ children, role }) {
  const { user, ready } = useAuth()
  const location = useLocation()

  if (!ready) return <Spinner label="Loading..." />

  if (!user) {
    return <Navigate to="/login" state={{ from: location }} replace />
  }

  if (role && user.role !== role) {
    const target = user.role === 'ADMIN' ? '/admin' : '/broker'
    return <Navigate to={target} replace />
  }

  return children
}
