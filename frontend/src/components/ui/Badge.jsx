import { cn } from '../../lib/utils'

const VARIANTS = {
  default:
    'bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-200',
  brand: 'bg-brand-50 text-brand-700 dark:bg-brand-950 dark:text-brand-300',
  success:
    'bg-emerald-50 text-emerald-700 dark:bg-emerald-950/50 dark:text-emerald-300',
  warning:
    'bg-amber-50 text-amber-700 dark:bg-amber-950/50 dark:text-amber-300',
  danger: 'bg-rose-50 text-rose-700 dark:bg-rose-950/50 dark:text-rose-300',
  info: 'bg-sky-50 text-sky-700 dark:bg-sky-950/50 dark:text-sky-300',
  muted:
    'bg-slate-50 text-slate-500 dark:bg-slate-900 dark:text-slate-400 border border-slate-200 dark:border-slate-800',
}

const STATUS_MAP = {
  DRAFT: 'muted',
  ACTIVE: 'success',
  EXPIRED: 'warning',
  CANCELLED: 'danger',
  INACTIVE: 'muted',
  INDIVIDUAL: 'info',
  COMPANY: 'brand',
  RESIDENTIAL: 'info',
  OFFICE: 'brand',
  INDUSTRIAL: 'warning',
}

export default function Badge({ variant, status, className, children, ...props }) {
  const v = variant || (status && STATUS_MAP[status]) || 'default'
  return (
    <span
      className={cn(
        'inline-flex items-center gap-1 rounded-full px-2.5 py-0.5 text-xs font-medium',
        VARIANTS[v],
        className,
      )}
      {...props}
    >
      {children}
    </span>
  )
}
