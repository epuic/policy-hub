import Button from './Button'
import { ChevronLeft, ChevronRight } from 'lucide-react'

export default function Pagination({ page, totalPages, onChange }) {
  if (!totalPages || totalPages <= 1) return null
  return (
    <div className="flex items-center justify-between px-5 py-3 text-xs text-slate-500 dark:text-slate-400">
      <span>
        Pagina <span className="font-semibold text-slate-700 dark:text-slate-200">{page + 1}</span> din{' '}
        <span className="font-semibold text-slate-700 dark:text-slate-200">{totalPages}</span>
      </span>
      <div className="flex items-center gap-2">
        <Button
          variant="outline"
          size="sm"
          disabled={page <= 0}
          onClick={() => onChange(page - 1)}
          leftIcon={<ChevronLeft className="h-3.5 w-3.5" />}
        >
          Anterior
        </Button>
        <Button
          variant="outline"
          size="sm"
          disabled={page >= totalPages - 1}
          onClick={() => onChange(page + 1)}
          rightIcon={<ChevronRight className="h-3.5 w-3.5" />}
        >
          Următor
        </Button>
      </div>
    </div>
  )
}
