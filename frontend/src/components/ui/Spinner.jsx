import { Loader2 } from 'lucide-react'
import { cn } from '../../lib/utils'

export default function Spinner({ className, size = 'md', label }) {
  const sizes = { sm: 'h-4 w-4', md: 'h-6 w-6', lg: 'h-8 w-8' }
  return (
    <div className={cn('flex items-center justify-center gap-3 py-8', className)}>
      <Loader2 className={cn('animate-spin text-brand-500', sizes[size])} />
      {label && <span className="text-sm text-slate-500">{label}</span>}
    </div>
  )
}
