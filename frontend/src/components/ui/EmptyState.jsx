import { cn } from '../../lib/utils'
import { Inbox } from 'lucide-react'

export default function EmptyState({
  icon: Icon = Inbox,
  title = 'Nothing here yet',
  message,
  action,
  className,
}) {
  return (
    <div
      className={cn(
        'flex flex-col items-center justify-center text-center py-12 px-6',
        className,
      )}
    >
      <div className="rounded-2xl bg-slate-100 dark:bg-slate-800/60 p-4 mb-4">
        <Icon className="h-7 w-7 text-slate-400" />
      </div>
      <h3 className="text-sm font-semibold text-slate-900 dark:text-slate-100">
        {title}
      </h3>
      {message && (
        <p className="mt-1 text-xs text-slate-500 max-w-sm">{message}</p>
      )}
      {action && <div className="mt-4">{action}</div>}
    </div>
  )
}
