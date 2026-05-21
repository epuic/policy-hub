import { useEffect, useState } from 'react'
import { FileBarChart2, Filter, X } from 'lucide-react'
import { Card, CardBody } from '../../components/ui/Card'
import Button from '../../components/ui/Button'
import Input from '../../components/ui/Input'
import Select from '../../components/ui/Select'
import DatePicker from '../../components/ui/DatePicker'
import Spinner from '../../components/ui/Spinner'
import EmptyState from '../../components/ui/EmptyState'
import { Table, THead, TH, TBody, TR, TD } from '../../components/ui/Table'
import { reportsApi } from '../../api/reports'
import { currenciesApi } from '../../api/currencies'
import { useToast } from '../../contexts/ToastContext'
import { apiError } from '../../lib/api'
import { formatMoney, formatNumber } from '../../lib/utils'
import { POLICY_STATUSES, BUILDING_TYPES } from '../../utils/constants'

const REPORTS = [
  { key: 'byCountry', label: 'By Country' },
  { key: 'byCounty', label: 'By County' },
  { key: 'byCity', label: 'By City' },
  { key: 'byBroker', label: 'By Broker' },
]

export default function Reports() {
  const toast = useToast()
  const [active, setActive] = useState('byCountry')
  const [rows, setRows] = useState([])
  const [currencies, setCurrencies] = useState([])
  const [loading, setLoading] = useState(false)
  const [filters, setFilters] = useState({
    from: '',
    to: '',
    status: '',
    currencyCode: '',
    buildingType: '',
  })

  const fetch = async (nextFilters = filters) => {
    setLoading(true)
    try {
      const params = {}
      Object.entries(nextFilters).forEach(([k, v]) => {
        if (v) params[k] = v
      })
      const data = await reportsApi[active](params)
      setRows(data || [])
    } catch (err) {
      toast.error(apiError(err))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetch()
    // eslint-disable-next-line
  }, [active])

  useEffect(() => {
    currenciesApi
      .list({ page: 0, size: 200 })
      .then((data) => setCurrencies(data.content || []))
      .catch((err) => toast.error(apiError(err)))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const hasFilters = Boolean(
    filters.from ||
      filters.to ||
      filters.status ||
      filters.currencyCode ||
      filters.buildingType,
  )

  const clearFilters = () => {
    const emptyFilters = {
      from: '',
      to: '',
      status: '',
      currencyCode: '',
      buildingType: '',
    }
    setFilters(emptyFilters)
    fetch(emptyFilters)
  }

  const applyFilters = () => {
    fetch(filters)
  }

  const columns = {
    byCountry: ['Country', 'Currency', 'Policy Count', 'Total Premium', 'Base Currency'],
    byCounty: ['Country', 'County', 'Currency', 'Policy Count', 'Total Premium', 'Base Currency'],
    byCity: ['Country', 'County', 'City', 'Currency', 'Policy Count', 'Total Premium', 'Base Currency'],
    byBroker: ['Broker', 'Currency', 'Policy Count', 'Total Premium', 'Base Currency'],
  }

  const renderRow = (r, i) => {
    const cells = {
      byCountry: [r.countryName, r.currencyCode, formatNumber(r.policyCount), formatMoney(r.totalFinalPremium, r.currencyCode), formatMoney(r.totalFinalPremiumInBaseCurrency, 'RON')],
      byCounty: [r.countryName, r.countyName, r.currencyCode, formatNumber(r.policyCount), formatMoney(r.totalFinalPremium, r.currencyCode), formatMoney(r.totalFinalPremiumInBaseCurrency, 'RON')],
      byCity: [r.countryName, r.countyName, r.cityName, r.currencyCode, formatNumber(r.policyCount), formatMoney(r.totalFinalPremium, r.currencyCode), formatMoney(r.totalFinalPremiumInBaseCurrency, 'RON')],
      byBroker: [r.brokerName, r.currencyCode, formatNumber(r.policyCount), formatMoney(r.totalFinalPremium, r.currencyCode), formatMoney(r.totalFinalPremiumInBaseCurrency, 'RON')],
    }
    return (
      <TR key={i}>
        {cells[active].map((c, j) => (
          <TD key={j} className={j >= cells[active].length - 3 ? 'font-medium' : ''}>
            {c}
          </TD>
        ))}
      </TR>
    )
  }

  return (
    <div className="space-y-5">
      <div>
        <h2 className="text-2xl font-bold text-slate-900 dark:text-slate-100">
          Reports
        </h2>
        <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
          Policy analytics and summaries
        </p>
      </div>

      <div className="flex flex-wrap gap-1 border border-slate-200 dark:border-slate-800 rounded-lg p-1 bg-white dark:bg-slate-900/60 w-fit">
        {REPORTS.map((r) => (
          <button
            key={r.key}
            onClick={() => setActive(r.key)}
            className={`px-3.5 py-1.5 text-sm rounded-md transition ${
              active === r.key
                ? 'bg-brand-600 text-white shadow-sm'
                : 'text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800'
            }`}
          >
            {r.label}
          </button>
        ))}
      </div>

      <Card>
        <CardBody className="border-b border-slate-100 p-4 dark:border-slate-800">
          <div className="rounded-2xl border border-slate-200 bg-slate-50/70 p-2 dark:border-slate-800 dark:bg-slate-950/30">
            <div className="grid grid-cols-1 gap-2 md:grid-cols-[1fr_1fr_1fr_1fr_1.1fr_auto_auto] md:items-end">
              <DatePicker
                label="From"
                value={filters.from}
                onChange={(value) => setFilters({ ...filters, from: value })}
                placeholder="From date"
                className="h-11 rounded-xl border-0 bg-white shadow-sm dark:bg-slate-900"
              />
              <DatePicker
                label="To"
                value={filters.to}
                onChange={(value) => setFilters({ ...filters, to: value })}
                placeholder="To date"
                className="h-11 rounded-xl border-0 bg-white shadow-sm dark:bg-slate-900"
              />
              <Select
                value={filters.status}
                onChange={(e) => setFilters({ ...filters, status: e.target.value })}
                placeholder="Any status"
                options={POLICY_STATUSES}
                className="h-11 rounded-xl border-0 bg-white shadow-sm dark:bg-slate-900"
              />
              <Select
                value={filters.currencyCode}
                onChange={(e) =>
                  setFilters({ ...filters, currencyCode: e.target.value })
                }
                placeholder="Any currency"
                options={currencies.map((currency) => ({
                  value: currency.code,
                  label: `${currency.code} - ${currency.name}`,
                }))}
                className="h-11 rounded-xl border-0 bg-white shadow-sm dark:bg-slate-900"
              />
              <Select
                value={filters.buildingType}
                onChange={(e) =>
                  setFilters({ ...filters, buildingType: e.target.value })
                }
                placeholder="Any building"
                options={BUILDING_TYPES}
                className="h-11 rounded-xl border-0 bg-white shadow-sm dark:bg-slate-900"
              />
              {hasFilters && (
                <Button type="button" variant="ghost" onClick={clearFilters} leftIcon={<X className="h-4 w-4" />}>
                  Clear
                </Button>
              )}
              <Button type="button" onClick={applyFilters} leftIcon={<Filter className="h-4 w-4" />}>
                Filter
              </Button>
            </div>
          </div>
        </CardBody>

        {loading ? (
          <Spinner />
        ) : rows.length === 0 ? (
          <EmptyState icon={FileBarChart2} title="No results" />
        ) : (
          <Table>
            <THead>
              <TR>
                {columns[active].map((c) => (
                  <TH key={c}>{c}</TH>
                ))}
              </TR>
            </THead>
            <TBody>{rows.map(renderRow)}</TBody>
          </Table>
        )}
      </Card>
    </div>
  )
}
