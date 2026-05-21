import { useEffect } from 'react'
import { cn } from '../../lib/utils'
import { X } from 'lucide-react'

export default function Modal({
  open,
  onClose,
  title,
  children,
  size = 'md',
  contentClassName,
}) {
  useEffect(() => {
    if (!open) return
    const handler = (e) => e.key === 'Escape' && onClose?.()
    window.addEventListener('keydown', handler)
    document.body.style.overflow = 'hidden'
    return () => {
      window.removeEventListener('keydown', handler)
      document.body.style.overflow = ''
    }
  }, [open, onClose])

  if (!open) return null

  const sizes = {
    sm: 'max-w-md',
    md: 'max-w-lg',
    lg: 'max-w-2xl',
    xl: 'max-w-4xl',
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 animate-fade-in">
      <div
        className="absolute inset-0 bg-slate-950/60 backdrop-blur-sm"
        onClick={onClose}
      />
      <div
        className={cn(
          'relative w-full animate-slide-up',
          'rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800',
          'shadow-2xl',
          sizes[size],
        )}
      >
        <div className="flex items-center justify-between p-5 border-b border-slate-100 dark:border-slate-800">
          <h3 className="text-base font-semibold text-slate-900 dark:text-slate-100">
            {title}
          </h3>
          <button
            onClick={onClose}
            className="rounded-lg p-1.5 hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-500"
          >
            <X className="h-4 w-4" />
          </button>
        </div>
        <div
          className={cn(
            'p-5 max-h-[calc(100vh-10rem)] overflow-auto',
            contentClassName,
          )}
        >
          {children}
        </div>
      </div>
    </div>
  )
}
