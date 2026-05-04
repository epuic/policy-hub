import { Moon, Sun } from 'lucide-react'
import { useTheme } from '../../contexts/ThemeContext'
import { cn } from '../../lib/utils'

export default function ThemeToggle({ className }) {
  const { theme, toggle } = useTheme()
  return (
    <button
      onClick={toggle}
      className={cn(
        'relative inline-flex h-9 w-9 items-center justify-center rounded-lg',
        'border border-slate-200 dark:border-slate-700',
        'bg-white dark:bg-slate-900 text-slate-600 dark:text-slate-300',
        'hover:bg-slate-50 dark:hover:bg-slate-800 transition',
        className,
      )}
      title="Schimbă tema"
    >
      {theme === 'dark' ? (
        <Sun className="h-4 w-4" />
      ) : (
        <Moon className="h-4 w-4" />
      )}
    </button>
  )
}
