import { NavLink } from 'react-router-dom'
import { Shield } from 'lucide-react'
import { cn } from '../../lib/utils'

export default function Sidebar({ items = [], title = 'Insurance Platform', subtitle }) {
  return (
    <aside className="hidden md:flex w-64 shrink-0 flex-col border-r border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950/40">
      <div className="flex items-center gap-3 px-5 py-5 border-b border-slate-100 dark:border-slate-800">
        <div className="rounded-xl bg-gradient-to-br from-brand-500 to-brand-700 p-2 shadow-glow">
          <Shield className="h-5 w-5 text-white" />
        </div>
        <div>
          <div className="font-semibold text-sm text-slate-900 dark:text-slate-100">
            {title}
          </div>
          {subtitle && (
            <div className="text-[11px] uppercase tracking-wider text-slate-400">
              {subtitle}
            </div>
          )}
        </div>
      </div>

      <nav className="flex-1 overflow-y-auto p-3 space-y-1">
        {items.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.end}
            className={({ isActive }) =>
              cn(
                'flex items-center gap-3 rounded-lg px-3 py-2 text-sm transition',
                isActive
                  ? 'bg-brand-50 text-brand-700 dark:bg-brand-950/50 dark:text-brand-300 font-medium'
                  : 'text-slate-600 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-900',
              )
            }
          >
            {item.icon && <item.icon className="h-4 w-4 shrink-0" />}
            <span className="truncate">{item.label}</span>
          </NavLink>
        ))}
      </nav>

      <div className="p-4 border-t border-slate-100 dark:border-slate-800 text-[11px] text-slate-400">
        Insurance Platform
      </div>
    </aside>
  )
}
