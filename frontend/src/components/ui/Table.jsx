import { cn } from '../../lib/utils'

export function Table({ className, children }) {
  return (
    <div className={cn('overflow-x-auto', className)}>
      <table className="w-full text-sm">{children}</table>
    </div>
  )
}

export function THead({ children }) {
  return (
    <thead className="text-xs uppercase tracking-wider text-slate-500 dark:text-slate-400 bg-slate-50 dark:bg-slate-900/40">
      {children}
    </thead>
  )
}

export function TH({ children, className }) {
  return (
    <th
      className={cn(
        'px-4 py-3 font-semibold text-left border-b border-slate-100 dark:border-slate-800',
        className,
      )}
    >
      {children}
    </th>
  )
}

export function TBody({ children }) {
  return <tbody>{children}</tbody>
}

export function TR({ children, onClick, className }) {
  return (
    <tr
      onClick={onClick}
      className={cn(
        'border-b border-slate-50 dark:border-slate-900',
        'hover:bg-slate-50/60 dark:hover:bg-slate-900/40 transition',
        onClick && 'cursor-pointer',
        className,
      )}
    >
      {children}
    </tr>
  )
}

export function TD({ children, className }) {
  return (
    <td className={cn('px-4 py-3 text-slate-700 dark:text-slate-200', className)}>
      {children}
    </td>
  )
}
