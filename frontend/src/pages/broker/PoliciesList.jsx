import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Plus, Filter, ScrollText, X } from 'lucide-react'
import { Card, CardBody } from '../../components/ui/Card'
import Button from '../../components/ui/Button'
import Select from '../../components/ui/Select'
import DatePicker from '../../components/ui/DatePicker'
import Badge from '../../components/ui/Badge'
import Spinner from '../../components/ui/Spinner'
import EmptyState from '../../components/ui/EmptyState'
import Pagination from '../../components/ui/Pagination'
import { Table, THead, TH, TBody, TR, TD } from '../../components/ui/Table'
import { policiesApi } from '../../api/policies'
import { useToast } from '../../contexts/ToastContext'
import { apiError } from '../../lib/api'
import { formatDate, formatMoney } from '../../lib/utils'
import { POLICY_STATUSES } from '../../utils/constants'

export default function PoliciesList() {
  const navigate = useNavigate()
  const toast = useToast()
  const [data, setData] = useState({ content: [], totalPages: 0, number: 0 })
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [filters, setFilters] = useState({
    status: '',
    startDateFrom: '',
    endDateTo: '',
  })

  const fetch = async (nextFilters = filters, nextPage = page) => {
    setLoading(true)
    try {
      const params = { page: nextPage, size: 10 }
      if (nextFilters.status) params.status = nextFilters.status
      if (nextFilters.startDateFrom) params.startDateFrom = nextFilters.startDateFrom
      if (nextFilters.endDateTo) params.endDateTo = nextFilters.endDateTo
      const res = await policiesApi.list(params)
      setData(res)
    } catch (err) {
      toast.error(apiError(err))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetch()
    // eslint-disable-next-line
  }, [page])

  const applyFilter = () => {
    setPage(0)
    fetch(filters, 0)
  }

  const hasFilters = Boolean(filters.status || filters.startDateFrom || filters.endDateTo)

  const clearFilters = () => {
    const emptyFilters = { status: '', startDateFrom: '', endDateTo: '' }
    setFilters(emptyFilters)
    setPage(0)
    fetch(emptyFilters, 0)
  }

  return (
    <div className="space-y-5">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div>
          <h2 className="text-2xl font-bold text-slate-900 dark:text-slate-100">
            Policies
          </h2>
          <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
            All managed policies
          </p>
        </div>
        <Button leftIcon={<Plus className="h-4 w-4" />} onClick={() => navigate('/broker/policies/new')}>
          New Policy
        </Button>
      </div>

      <Card>
        <CardBody className="border-b border-slate-100 p-4 dark:border-slate-800">
          <div className="rounded-2xl border border-slate-200 bg-slate-50/70 p-2 dark:border-slate-800 dark:bg-slate-950/30">
            <div className="grid grid-cols-1 gap-2 md:grid-cols-[1.1fr_1fr_1fr_auto_auto] md:items-end">
              <Select
                value={filters.status}
                onChange={(e) => setFilters({ ...filters, status: e.target.value })}
                placeholder="Any status"
                options={POLICY_STATUSES}
                className="h-11 rounded-xl border-0 bg-white shadow-sm dark:bg-slate-900"
              />
              <DatePicker
                label="Start"
                value={filters.startDateFrom}
                onChange={(value) => setFilters({ ...filters, startDateFrom: value })}
                placeholder="Start date"
                className="h-11 rounded-xl border-0 bg-white shadow-sm dark:bg-slate-900"
              />
              <DatePicker
                label="End"
                value={filters.endDateTo}
                onChange={(value) => setFilters({ ...filters, endDateTo: value })}
                placeholder="End date"
                className="h-11 rounded-xl border-0 bg-white shadow-sm dark:bg-slate-900"
              />
              {hasFilters && (
                <Button type="button" variant="ghost" onClick={clearFilters} leftIcon={<X className="h-4 w-4" />}>
                  Clear
                </Button>
              )}
              <Button type="button" onClick={applyFilter} leftIcon={<Filter className="h-4 w-4" />}>
                Filter
              </Button>
            </div>
          </div>
        </CardBody>
        {loading ? (
          <Spinner />
        ) : data.content.length === 0 ? (
          <EmptyState icon={ScrollText} title="No policies" message="No policies match the current filters" />
        ) : (
          <>
            <Table>
              <THead>
                <TR>
                  <TH>Policy No.</TH>
                  <TH>Client</TH>
                  <TH>Building</TH>
                  <TH>Period</TH>
                  <TH>Final Premium</TH>
                  <TH>Status</TH>
                </TR>
              </THead>
              <TBody>
                {data.content.map((p) => (
                  <TR key={p.id} onClick={() => navigate(`/broker/policies/${p.id}`)}>
                    <TD className="font-medium text-slate-900 dark:text-slate-100">
                      {p.policyNumber}
                    </TD>
                    <TD>{p.clientName}</TD>
                    <TD className="truncate max-w-[240px]">{p.buildingAddress}</TD>
                    <TD className="text-xs">
                      {formatDate(p.startDate)} to {formatDate(p.endDate)}
                    </TD>
                    <TD>{formatMoney(p.finalPremium, p.currencyCode)}</TD>
                    <TD>
                      <Badge status={p.status}>{p.status}</Badge>
                    </TD>
                  </TR>
                ))}
              </TBody>
            </Table>
            <Pagination page={data.number || 0} totalPages={data.totalPages || 0} onChange={setPage} />
          </>
        )}
      </Card>
    </div>
  )
}
