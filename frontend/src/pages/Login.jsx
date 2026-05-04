import { useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { Shield, Mail, Lock, Eye, EyeOff } from 'lucide-react'
import { useAuth } from '../contexts/AuthContext'
import { useToast } from '../contexts/ToastContext'
import { apiError } from '../lib/api'
import Input from '../components/ui/Input'
import Button from '../components/ui/Button'
import ThemeToggle from '../components/layout/ThemeToggle'

export default function Login() {
  const { login } = useAuth()
  const toast = useToast()
  const navigate = useNavigate()
  const location = useLocation()

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [showPwd, setShowPwd] = useState(false)
  const [loading, setLoading] = useState(false)

  const onSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      const user = await login(email, password)
      toast.success(`Bine ai venit, ${user.email}!`)
      const target =
        location.state?.from?.pathname ||
        (user.role === 'ADMIN' ? '/admin' : '/broker')
      navigate(target, { replace: true })
    } catch (err) {
      toast.error(apiError(err, 'Credențiale invalide'))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen gradient-bg flex items-center justify-center p-4 relative">
      <div className="absolute top-4 right-4">
        <ThemeToggle />
      </div>

      <div className="w-full max-w-md animate-slide-up">
        <div className="flex flex-col items-center mb-8">
          <div className="rounded-2xl bg-gradient-to-br from-brand-500 to-brand-700 p-3.5 shadow-glow">
            <Shield className="h-7 w-7 text-white" />
          </div>
          <h1 className="mt-4 text-2xl font-bold text-slate-900 dark:text-slate-100">
            Insurance Platform
          </h1>
          <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
            Autentifică-te pentru a continua
          </p>
        </div>

        <div className="surface rounded-2xl p-6 shadow-xl shadow-brand-900/5">
          <form onSubmit={onSubmit} className="space-y-4">
            <Input
              type="email"
              label="Email"
              placeholder="nume@companie.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              leftIcon={<Mail className="h-4 w-4" />}
              required
              autoFocus
            />
            <Input
              type={showPwd ? 'text' : 'password'}
              label="Parolă"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              leftIcon={<Lock className="h-4 w-4" />}
              rightIcon={
                <button
                  type="button"
                  onClick={() => setShowPwd((s) => !s)}
                  className="hover:text-slate-600"
                >
                  {showPwd ? (
                    <EyeOff className="h-4 w-4" />
                  ) : (
                    <Eye className="h-4 w-4" />
                  )}
                </button>
              }
              required
            />
            <Button
              type="submit"
              className="w-full"
              size="lg"
              loading={loading}
            >
              Conectare
            </Button>
          </form>
        </div>

        <p className="text-center text-xs text-slate-400 mt-6">
          © {new Date().getFullYear()} Insurance Platform · Endava
        </p>
      </div>
    </div>
  )
}
