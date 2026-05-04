import { forwardRef } from 'react'
import { cn } from '../../lib/utils'

const Input = forwardRef(function Input(
  { className, label, error, hint, leftIcon, rightIcon, ...props },
  ref,
) {
  return (
    <div className="flex flex-col gap-1.5 w-full">
      {label && (
        <label className="text-xs font-medium text-slate-600 dark:text-slate-300">
          {label}
        </label>
      )}
      <div className="relative">
        {leftIcon && (
          <span className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400">
            {leftIcon}
          </span>
        )}
        <input
          ref={ref}
          className={cn(
            'w-full h-10 px-3 rounded-lg text-sm transition',
            'bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700',
            'text-slate-900 dark:text-slate-100 placeholder:text-slate-400',
            'focus:outline-none focus:ring-2 focus:ring-brand-500/30 focus:border-brand-500',
            leftIcon && 'pl-10',
            rightIcon && 'pr-10',
            error && 'border-rose-500 focus:border-rose-500 focus:ring-rose-500/30',
            className,
          )}
          {...props}
        />
        {rightIcon && (
          <span className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400">
            {rightIcon}
          </span>
        )}
      </div>
      {error && <span className="text-xs text-rose-500">{error}</span>}
      {!error && hint && (
        <span className="text-xs text-slate-400">{hint}</span>
      )}
    </div>
  )
})

export default Input
