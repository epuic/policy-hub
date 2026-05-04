import { forwardRef } from 'react'
import { cn } from '../../lib/utils'
import { ChevronDown } from 'lucide-react'

const Select = forwardRef(function Select(
  { className, label, error, hint, options = [], placeholder, children, ...props },
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
        <select
          ref={ref}
          className={cn(
            'appearance-none w-full h-10 px-3 pr-9 rounded-lg text-sm transition',
            'bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700',
            'text-slate-900 dark:text-slate-100',
            'focus:outline-none focus:ring-2 focus:ring-brand-500/30 focus:border-brand-500',
            error && 'border-rose-500',
            className,
          )}
          {...props}
        >
          {placeholder && (
            <option value="">{placeholder}</option>
          )}
          {children ||
            options.map((o) => (
              <option key={o.value ?? o} value={o.value ?? o}>
                {o.label ?? o}
              </option>
            ))}
        </select>
        <ChevronDown className="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
      </div>
      {error && <span className="text-xs text-rose-500">{error}</span>}
      {!error && hint && (
        <span className="text-xs text-slate-400">{hint}</span>
      )}
    </div>
  )
})

export default Select
