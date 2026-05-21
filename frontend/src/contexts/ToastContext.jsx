import { createContext, useCallback, useContext, useState } from 'react'
import { CheckCircle2, XCircle, Info, AlertTriangle, X } from 'lucide-react'
import { cn } from '../lib/utils'

const ToastContext = createContext(null)

const ICONS = {
  success: CheckCircle2,
  error: XCircle,
  info: Info,
  warning: AlertTriangle,
}

const STYLES = {
  success: 'text-emerald-500 bg-emerald-500/10 border-emerald-500/30',
  error: 'text-rose-500 bg-rose-500/10 border-rose-500/30',
  info: 'text-sky-500 bg-sky-500/10 border-sky-500/30',
  warning: 'text-amber-500 bg-amber-500/10 border-amber-500/30',
}

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([])

  const remove = (id) => setToasts((t) => t.filter((x) => x.id !== id))

  const push = useCallback((type, message) => {
    const id = Math.random().toString(36).slice(2)
    setToasts((t) => [...t, { id, type, message }])
    setTimeout(() => remove(id), 4000)
  }, [])

  const toast = {
    success: (m) => push('success', m),
    error: (m) => push('error', m),
    info: (m) => push('info', m),
    warning: (m) => push('warning', m),
  }

  return (
    <ToastContext.Provider value={toast}>
      {children}
      <div className="fixed left-1/2 top-1/2 z-[100] flex w-[min(460px,calc(100vw-2.5rem))] -translate-x-1/2 -translate-y-1/2 flex-col gap-2">
        {toasts.map((t) => {
          const Icon = ICONS[t.type] || Info
          return (
            <div
              key={t.id}
              className={cn(
                'flex items-start gap-3 rounded-xl border p-3 shadow-lg animate-slide-up backdrop-blur-md',
                'bg-white/90 dark:bg-slate-900/90',
              )}
            >
              <div
                className={cn(
                  'shrink-0 rounded-lg border p-1.5',
                  STYLES[t.type],
                )}
              >
                <Icon className="h-4 w-4" />
              </div>
              <div className="flex-1 whitespace-pre-line text-sm pt-0.5 text-slate-800 dark:text-slate-100">
                {t.message}
              </div>
              <button
                onClick={() => remove(t.id)}
                className="text-slate-400 hover:text-slate-700 dark:hover:text-slate-200"
              >
                <X className="h-4 w-4" />
              </button>
            </div>
          )
        })}
      </div>
    </ToastContext.Provider>
  )
}

export function useToast() {
  const ctx = useContext(ToastContext)
  if (!ctx) throw new Error('useToast must be used inside ToastProvider')
  return ctx
}
