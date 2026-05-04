import { LogOut, User } from 'lucide-react'
import { useAuth } from '../../contexts/AuthContext'
import ThemeToggle from './ThemeToggle'
import { initials } from '../../lib/utils'
import { useNavigate } from 'react-router-dom'

export default function Topbar({ title, actions }) {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const doLogout = () => {
    logout()
    navigate('/login', { replace: true })
  }

  return (
    <header className="h-16 border-b border-slate-200 dark:border-slate-800 bg-white/80 dark:bg-slate-950/40 backdrop-blur-md sticky top-0 z-30">
      <div className="flex items-center justify-between h-full px-5">
        <div>
          {title && (
            <h1 className="text-lg font-semibold text-slate-900 dark:text-slate-100">
              {title}
            </h1>
          )}
        </div>
        <div className="flex items-center gap-2">
          {actions}
          <ThemeToggle />
          <div className="flex items-center gap-2 rounded-lg border border-slate-200 dark:border-slate-700 pl-1 pr-2.5 h-9">
            <div className="h-7 w-7 rounded-md bg-gradient-to-br from-brand-500 to-brand-700 text-white text-xs font-semibold flex items-center justify-center">
              {initials(user?.email || 'U')}
            </div>
            <div className="hidden sm:flex flex-col -space-y-0.5">
              <span className="text-xs font-medium text-slate-700 dark:text-slate-200 max-w-[180px] truncate">
                {user?.email}
              </span>
              <span className="text-[10px] uppercase tracking-wider text-slate-400">
                {user?.role}
              </span>
            </div>
          </div>
          <button
            onClick={doLogout}
            className="inline-flex h-9 w-9 items-center justify-center rounded-lg border border-slate-200 dark:border-slate-700 text-slate-500 hover:text-rose-600 hover:border-rose-300 dark:hover:border-rose-700 transition"
            title="Deconectare"
          >
            <LogOut className="h-4 w-4" />
          </button>
        </div>
      </div>
    </header>
  )
}
