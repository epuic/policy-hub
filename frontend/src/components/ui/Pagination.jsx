import Button from './Button'
import { ChevronLeft, ChevronRight } from 'lucide-react'

export default function Pagination({ page, totalPages, onChange }) {
  if (!totalPages || totalPages <= 1) return null

  const restoreScrollPosition = (scrollY) => {
    const restore = () => window.scrollTo({ top: scrollY, left: 0 })

    window.requestAnimationFrame(restore)
    setTimeout(restore, 0)
    setTimeout(restore, 150)
    setTimeout(restore, 500)
  }

  const keepPageHeightStable = () => {
    const previousMinHeight = document.body.style.minHeight
    const currentHeight = document.documentElement.scrollHeight

    document.body.style.minHeight = `${currentHeight}px`

    setTimeout(() => {
      document.body.style.minHeight = previousMinHeight
    }, 700)
  }

  const changePage = (nextPage) => {
    const scrollY = window.scrollY
    keepPageHeightStable()
    document.activeElement?.blur()
    onChange(nextPage)
    restoreScrollPosition(scrollY)
  }

  return (
    <div className="flex items-center justify-between px-5 py-3 text-xs text-slate-500 dark:text-slate-400">
      <span>
        Page <span className="font-semibold text-slate-700 dark:text-slate-200">{page + 1}</span> of{' '}
        <span className="font-semibold text-slate-700 dark:text-slate-200">{totalPages}</span>
      </span>
      <div className="flex items-center gap-2">
        <Button
          type="button"
          variant="outline"
          size="sm"
          disabled={page <= 0}
          onClick={() => changePage(page - 1)}
          leftIcon={<ChevronLeft className="h-3.5 w-3.5" />}
        >
          Previous
        </Button>
        <Button
          type="button"
          variant="outline"
          size="sm"
          disabled={page >= totalPages - 1}
          onClick={() => changePage(page + 1)}
          rightIcon={<ChevronRight className="h-3.5 w-3.5" />}
        >
          Next
        </Button>
      </div>
    </div>
  )
}
