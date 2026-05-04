import { cn } from '../../lib/utils'

const ACCENTS = {
  brand: 'from-brand-500/20 to-brand-600/0 text-brand-600 dark:text-brand-400',
  emerald:
    'from-emerald-500/20 to-emerald-600/0 text-emerald-600 dark:text-emerald-400',
  amber: 'from-amber-500/20 to-amber-600/0 text-amber-600 dark:text-amber-400',
  rose: 'from-rose-500/20 to-rose-600/0 text-rose-600 dark:text-rose-400',
  sky: 'from-sky-500/20 to-sky-600/0 text-sky-600 dark:text-sky-400',
  violet: 'from-violet-500/20 to-violet-600/0 text-violet-600 dark:text-violet-400',
}

export default function StatCard({
  icon: Icon,
  label,
  value,
  hint,
  accent = 'brand',
  loading,
}) {
  return (
    <div className="relative overflow-hidden rounded-xl border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900/60 p-5">
      <div
        className={cn(
          'absolute -top-10 -right-10 h-40 w-40 rounded-full bg-gradient-radial blur-2xl opacity-70 pointer-events-none',
          'bg-gradient-to-br',
          ACCENTS[accent],
        )}
      />
      <div className="relative flex items-start justify-between">
        <div>
          <p className="text-xs uppercase tracking-wider text-slate-500 dark:text-slate-400">
            {label}
          </p>
          <p className="mt-2 text-2xl font-bold text-slate-900 dark:text-slate-100">
            {loading ? (
              <span className="inline-block h-7 w-16 rounded bg-slate-200 dark:bg-slate-800 animate-pulse" />
            ) : (
              value
            )}
          </p>
          {hint && (
            <p className="mt-1 text-xs text-slate-500 dark:text-slate-400">{hint}</p>
          )}
        </div>
        {Icon && (
          <div
            className={cn(
              'rounded-xl p-2.5 bg-white/60 dark:bg-slate-900/60 border border-slate-200/60 dark:border-slate-700/60',
              ACCENTS[accent].split(' ').slice(-2).join(' '),
            )}
          >
            <Icon className="h-5 w-5" />
          </div>
        )}
      </div>
    </div>
  )
}
