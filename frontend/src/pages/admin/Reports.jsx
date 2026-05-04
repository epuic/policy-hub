import { useEffect, useState } from 'react'
import { FileBarChart2, Filter } from 'lucide-react'
import { Card, CardBody, CardHeader } from '../../components/ui/Card'
import Button from '../../components/ui/Button'
import Input from '../../components/ui/Input'
import Select from '../../components/ui/Select'
import Spinner from '../../components/ui/Spinner'
import EmptyState from '../../components/ui/EmptyState'
import { Table, THead, TH, TBody, TR, TD } from '../../components/ui/Table'
import { reportsApi } from '../../api/reports'
import { useToast } from '../../contexts/ToastContext'
import { apiError } from '../../lib/api'
import { formatMoney, formatNumber } from '../../lib/utils'
import { POLICY_STATUSES, BUILDING_TYPES } from '../../utils/constants'

const REPORTS = [
  { key: 'byCountry', label: 'Pe țară' },
  { key: 'byCounty', label: 'Pe județ' },
  { key: 'byCity', label: 'Pe oraș' },
  { key: 'byBroker', label: 'Pe broker' },
]

export default function Reports() {
  const toast = useToast()
  const [active, setActive] = useState('byCountry')
  const [rows, setRows] = useState([])
  const [loading, setLoading] = useState(false)
  const [filters, setFilters] = useState({
    from: '',
    to: '',
    status: '',
    currencyCode: '',
    buildingType: '',
  })

  const fetch = async () => {
    setLoading(true)
    try {
      const params = {}
      Object.entries(filters).forEach(([k, v]) => {
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

  const columns = {
    byCountry: ['Țară', 'Monedă', 'Nr. politici', 'Primă totală', 'În bază'],
    byCounty: ['Țară', 'Județ', 'Monedă', 'Nr. politici', 'Primă totală', 'În bază'],
    byCity: ['Țară', 'Județ', 'Oraș', 'Monedă', 'Nr. politici', 'Primă totală', 'În bază'],
    byBroker: ['Broker', 'Monedă', 'Nr. politici', 'Primă totală', 'În bază'],
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
          Rapoarte
        </h2>
        <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
          Analize și sinteze ale politicilor
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
        <CardBody className="border-b border-slate-100 dark:border-slate-800">
          <div className="grid grid-cols-1 md:grid-cols-6 gap-3 items-end">
            <Input
              type="date"
              label="De la"
              value={filters.from}
              onChange={(e) => setFilters({ ...filters, from: e.target.value })}
            />
            <Input
              type="date"
              label="Până la"
              value={filters.to}
              onChange={(e) => setFilters({ ...filters, to: e.target.value })}
            />
            <Select
              label="Status"
              value={filters.status}
              onChange={(e) => setFilters({ ...filters, status: e.target.value })}
              placeholder="Toate"
              options={POLICY_STATUSES}
            />
            <Input
              label="Monedă"
              value={filters.currencyCode}
              onChange={(e) =>
                setFilters({ ...filters, currencyCode: e.target.value.toUpperCase() })
              }
              placeholder="RON / EUR"
            />
            <Select
              label="Tip clădire"
              value={filters.buildingType}
              onChange={(e) =>
                setFilters({ ...filters, buildingType: e.target.value })
              }
              placeholder="Toate"
              options={BUILDING_TYPES}
            />
            <Button onClick={fetch} leftIcon={<Filter className="h-4 w-4" />}>
              Aplică
            </Button>
          </div>
        </CardBody>

        {loading ? (
          <Spinner />
        ) : rows.length === 0 ? (
          <EmptyState icon={FileBarChart2} title="Niciun rezultat" />
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
