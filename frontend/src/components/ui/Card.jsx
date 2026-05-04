import { cn } from '../../lib/utils'

export function Card({ className, children, ...props }) {
  return (
    <div
      className={cn(
        'rounded-xl border border-slate-200 dark:border-slate-800',
        'bg-white dark:bg-slate-900/60',
        'shadow-sm',
        className,
      )}
      {...props}
    >
      {children}
    </div>
  )
}

export function CardHeader({ className, title, subtitle, actions, children }) {
  return (
    <div
      className={cn(
        'flex items-start justify-between gap-4 p-5 border-b border-slate-100 dark:border-slate-800',
        className,
      )}
    >
      {children || (
        <>
          <div>
            {title && (
              <h3 className="text-base font-semibold text-slate-900 dark:text-slate-100">
                {title}
              </h3>
            )}
            {subtitle && (
              <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
                {subtitle}
              </p>
            )}
          </div>
          {actions && <div className="flex items-center gap-2">{actions}</div>}
        </>
      )}
    </div>
  )
}

export function CardBody({ className, children }) {
  return <div className={cn('p-5', className)}>{children}</div>
}

export function CardFooter({ className, children }) {
  return (
    <div
      className={cn(
        'flex items-center gap-2 p-4 border-t border-slate-100 dark:border-slate-800',
        className,
      )}
    >
      {children}
    </div>
  )
}
