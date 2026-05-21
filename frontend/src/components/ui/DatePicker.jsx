import { useEffect, useMemo, useRef, useState } from 'react'
import { CalendarDays, ChevronLeft, ChevronRight, X } from 'lucide-react'
import { cn, formatDate } from '../../lib/utils'

const WEEKDAYS = ['M', 'T', 'W', 'T', 'F', 'S', 'S']
const MONTHS = [
  'January',
  'February',
  'March',
  'April',
  'May',
  'June',
  'July',
  'August',
  'September',
  'October',
  'November',
  'December',
]

function parseDate(value) {
  if (!value) return null
  const [year, month, day] = value.split('-').map(Number)
  if (!year || !month || !day) return null
  return new Date(year, month - 1, day)
}

function toDateValue(date) {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

function sameDay(a, b) {
  return (
    a &&
    b &&
    a.getFullYear() === b.getFullYear() &&
    a.getMonth() === b.getMonth() &&
    a.getDate() === b.getDate()
  )
}

function startOfMonth(date) {
  return new Date(date.getFullYear(), date.getMonth(), 1)
}

function addMonths(date, count) {
  return new Date(date.getFullYear(), date.getMonth() + count, 1)
}

function addYears(date, count) {
  return new Date(date.getFullYear() + count, date.getMonth(), 1)
}

function yearRange(today, selectedDate, viewDate, minDate, maxDate) {
  const currentYear = today.getFullYear()
  const anchorYears = [
    currentYear,
    selectedDate?.getFullYear(),
    viewDate.getFullYear(),
    minDate?.getFullYear(),
    maxDate?.getFullYear(),
  ].filter(Boolean)
  const minYear = minDate?.getFullYear() ?? Math.min(...anchorYears) - 20
  const maxYear = maxDate?.getFullYear() ?? Math.max(...anchorYears) + 30

  return Array.from({ length: maxYear - minYear + 1 }, (_, index) => minYear + index)
}

function buildCalendarDays(monthDate) {
  const first = startOfMonth(monthDate)
  const mondayOffset = (first.getDay() + 6) % 7
  const start = new Date(first)
  start.setDate(first.getDate() - mondayOffset)

  return Array.from({ length: 42 }, (_, index) => {
    const date = new Date(start)
    date.setDate(start.getDate() + index)
    return date
  })
}

export default function DatePicker({
  className,
  label,
  value,
  onChange,
  placeholder = 'Pick a date',
  min,
  max,
  disabled,
  required,
}) {
  const selectedDate = parseDate(value)
  const today = new Date()
  const [open, setOpen] = useState(false)
  const [viewDate, setViewDate] = useState(startOfMonth(selectedDate || today))
  const rootRef = useRef(null)

  useEffect(() => {
    if (selectedDate) setViewDate(startOfMonth(selectedDate))
  }, [value])

  useEffect(() => {
    if (!open) return
    const close = (event) => {
      if (!rootRef.current?.contains(event.target)) setOpen(false)
    }
    document.addEventListener('mousedown', close)
    return () => document.removeEventListener('mousedown', close)
  }, [open])

  const minDate = parseDate(min)
  const maxDate = parseDate(max)
  const days = useMemo(() => buildCalendarDays(viewDate), [viewDate])
  const years = useMemo(
    () => yearRange(today, selectedDate, viewDate, minDate, maxDate),
    [max, min, value, viewDate],
  )

  const isDisabled = (date) => {
    if (minDate && date < minDate) return true
    if (maxDate && date > maxDate) return true
    return false
  }

  const pick = (date) => {
    if (isDisabled(date)) return
    onChange(toDateValue(date))
    setOpen(false)
  }

  const clear = (event) => {
    event.stopPropagation()
    onChange('')
  }

  const displayValue = value ? formatDate(value) : ''

  const changeMonth = (month) => {
    setViewDate((date) => new Date(date.getFullYear(), Number(month), 1))
  }

  const changeYear = (year) => {
    setViewDate((date) => new Date(Number(year), date.getMonth(), 1))
  }

  return (
    <div ref={rootRef} className="relative flex w-full flex-col gap-1.5">
      {label && (
        <label className="text-xs font-medium text-slate-600 dark:text-slate-300">
          {label}
          {required ? <span className="text-rose-500"> *</span> : null}
        </label>
      )}
      <button
        type="button"
        disabled={disabled}
        onClick={() => setOpen((current) => !current)}
        className={cn(
          'flex h-10 w-full items-center gap-2 rounded-lg border border-slate-200 bg-white px-3 text-left text-sm transition',
          'text-slate-900 shadow-sm shadow-slate-200/40 placeholder:text-slate-400',
          'focus:outline-none focus:ring-2 focus:ring-brand-500/30 focus:border-brand-500',
          'disabled:cursor-not-allowed disabled:opacity-60',
          'dark:border-slate-700 dark:bg-slate-900 dark:text-slate-100 dark:shadow-none',
          className,
        )}
      >
        <CalendarDays className="h-4 w-4 shrink-0 text-slate-400" />
        <span className={cn('flex-1 truncate', !displayValue && 'text-slate-400')}>
          {displayValue || placeholder}
        </span>
        {value && !disabled ? (
          <span
            role="button"
            tabIndex={0}
            onClick={clear}
            onKeyDown={(event) => {
              if (event.key === 'Enter' || event.key === ' ') clear(event)
            }}
            className="rounded-full p-0.5 text-slate-400 transition hover:bg-slate-100 hover:text-slate-700 dark:hover:bg-slate-800 dark:hover:text-slate-200"
            title="Clear date"
          >
            <X className="h-3.5 w-3.5" />
          </span>
        ) : null}
      </button>

      {open && (
        <div className="absolute left-0 top-full z-50 mt-2 w-[min(22rem,calc(100vw-2rem))] rounded-2xl border border-slate-200 bg-white p-3 shadow-xl shadow-slate-900/10 dark:border-slate-800 dark:bg-slate-950 dark:shadow-black/30">
          <div className="flex items-center justify-between gap-2 px-1">
            <button
              type="button"
              onClick={() => setViewDate((date) => addMonths(date, -1))}
              className="inline-flex h-8 w-8 items-center justify-center rounded-lg text-slate-500 transition hover:bg-slate-100 hover:text-slate-900 dark:hover:bg-slate-900 dark:hover:text-slate-100"
              title="Previous month"
            >
              <ChevronLeft className="h-4 w-4" />
            </button>
            <div className="grid flex-1 grid-cols-[1.25fr_0.9fr] gap-2">
              <select
                value={viewDate.getMonth()}
                onChange={(event) => changeMonth(event.target.value)}
                className="h-9 rounded-xl border border-slate-200 bg-slate-50 px-2 text-sm font-medium text-slate-800 outline-none transition hover:bg-white focus:border-brand-500 focus:ring-2 focus:ring-brand-500/20 dark:border-slate-800 dark:bg-slate-900 dark:text-slate-100"
                title="Choose month"
              >
                {MONTHS.map((month, index) => (
                  <option key={month} value={index}>
                    {month}
                  </option>
                ))}
              </select>
              <select
                value={viewDate.getFullYear()}
                onChange={(event) => changeYear(event.target.value)}
                className="h-9 rounded-xl border border-slate-200 bg-slate-50 px-2 text-sm font-medium text-slate-800 outline-none transition hover:bg-white focus:border-brand-500 focus:ring-2 focus:ring-brand-500/20 dark:border-slate-800 dark:bg-slate-900 dark:text-slate-100"
                title="Choose year"
              >
                {years.map((year) => (
                  <option key={year} value={year}>
                    {year}
                  </option>
                ))}
              </select>
            </div>
            <button
              type="button"
              onClick={() => setViewDate((date) => addMonths(date, 1))}
              className="inline-flex h-8 w-8 items-center justify-center rounded-lg text-slate-500 transition hover:bg-slate-100 hover:text-slate-900 dark:hover:bg-slate-900 dark:hover:text-slate-100"
              title="Next month"
            >
              <ChevronRight className="h-4 w-4" />
            </button>
          </div>

          <div className="mt-2 grid grid-cols-2 gap-2 px-1">
            <button
              type="button"
              onClick={() => setViewDate((date) => addYears(date, -1))}
              className="rounded-lg px-3 py-1.5 text-xs font-medium text-slate-500 transition hover:bg-slate-100 hover:text-slate-800 dark:hover:bg-slate-900 dark:hover:text-slate-100"
            >
              Previous year
            </button>
            <button
              type="button"
              onClick={() => setViewDate((date) => addYears(date, 1))}
              className="rounded-lg px-3 py-1.5 text-xs font-medium text-slate-500 transition hover:bg-slate-100 hover:text-slate-800 dark:hover:bg-slate-900 dark:hover:text-slate-100"
            >
              Next year
            </button>
          </div>

          <div className="mt-3 grid grid-cols-7 gap-1 text-center text-[11px] font-medium uppercase tracking-wider text-slate-400">
            {WEEKDAYS.map((day, index) => (
              <div key={`${day}-${index}`} className="py-1">
                {day}
              </div>
            ))}
          </div>

          <div className="mt-1 grid grid-cols-7 gap-1">
            {days.map((date) => {
              const inMonth = date.getMonth() === viewDate.getMonth()
              const selected = sameDay(date, selectedDate)
              const current = sameDay(date, today)
              const blocked = isDisabled(date)

              return (
                <button
                  type="button"
                  key={toDateValue(date)}
                  disabled={blocked}
                  onClick={() => pick(date)}
                  className={cn(
                    'flex aspect-square items-center justify-center rounded-xl text-sm transition',
                    selected
                      ? 'bg-brand-600 text-white shadow-sm shadow-brand-600/30'
                      : 'text-slate-700 hover:bg-brand-50 hover:text-brand-700 dark:text-slate-200 dark:hover:bg-brand-950/50 dark:hover:text-brand-300',
                    !inMonth && !selected && 'text-slate-300 dark:text-slate-700',
                    current && !selected && 'ring-1 ring-brand-300 dark:ring-brand-800',
                    blocked && 'cursor-not-allowed opacity-30 hover:bg-transparent',
                  )}
                >
                  {date.getDate()}
                </button>
              )
            })}
          </div>

          <div className="mt-3 flex items-center justify-between gap-2 border-t border-slate-100 pt-3 dark:border-slate-800">
            <button
              type="button"
              onClick={() => pick(today)}
              className="rounded-lg px-3 py-1.5 text-xs font-medium text-brand-600 transition hover:bg-brand-50 dark:text-brand-300 dark:hover:bg-brand-950/50"
            >
              Today
            </button>
            <button
              type="button"
              onClick={() => setOpen(false)}
              className="rounded-lg px-3 py-1.5 text-xs font-medium text-slate-500 transition hover:bg-slate-100 hover:text-slate-800 dark:hover:bg-slate-900 dark:hover:text-slate-100"
            >
              Close
            </button>
          </div>
        </div>
      )}
    </div>
  )
}
