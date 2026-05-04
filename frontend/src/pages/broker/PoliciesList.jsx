import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Plus, Filter, ScrollText } from 'lucide-react'
import { Card, CardBody } from '../../components/ui/Card'
import Button from '../../components/ui/Button'
import Select from '../../components/ui/Select'
import Input from '../../components/ui/Input'
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

  const fetch = async () => {
    setLoading(true)
    try {
      const params = { page, size: 10 }
      if (filters.status) params.status = filters.status
      if (filters.startDateFrom) params.startDateFrom = filters.startDateFrom
      if (filters.endDateTo) params.endDateTo = filters.endDateTo
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
    fetch()
  }

  return (
    <div className="space-y-5">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div>
          <h2 className="text-2xl font-bold text-slate-900 dark:text-slate-100">
            Politici
          </h2>
          <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
            Toate politicile gestionate
          </p>
        </div>
        <Button
          leftIcon={<Plus className="h-4 w-4" />}
          onClick={() => navigate('/broker/policies/new')}
        >
          Poliță nouă
        </Button>
      </div>

      <Card>
        <CardBody className="border-b border-slate-100 dark:border-slate-800">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-3 items-end">
            <Select
              label="Status"
              value={filters.status}
              onChange={(e) => setFilters({ ...filters, status: e.target.value })}
              placeholder="Toate"
              options={POLICY_STATUSES}
            />
            <Input
              type="date"
              label="Start de la"
              value={filters.startDateFrom}
              onChange={(e) =>
                setFilters({ ...filters, startDateFrom: e.target.value })
              }
            />
            <Input
              type="date"
              label="Final până la"
              value={filters.endDateTo}
              onChange={(e) => setFilters({ ...filters, endDateTo: e.target.value })}
            />
            <Button onClick={applyFilter} leftIcon={<Filter className="h-4 w-4" />}>
              Aplică filtre
            </Button>
          </div>
        </CardBody>
        {loading ? (
          <Spinner />
        ) : data.content.length === 0 ? (
          <EmptyState
            icon={ScrollText}
            title="Nicio poliță"
            message="Nu există politici care corespund filtrelor"
          />
        ) : (
          <>
            <Table>
              <THead>
                <TR>
                  <TH>Nr. Poliță</TH>
                  <TH>Client</TH>
                  <TH>Clădire</TH>
                  <TH>Perioadă</TH>
                  <TH>Primă finală</TH>
                  <TH>Status</TH>
                </TR>
              </THead>
              <TBody>
                {data.content.map((p) => (
                  <TR
                    key={p.id}
                    onClick={() => navigate(`/broker/policies/${p.id}`)}
                  >
                    <TD className="font-medium text-slate-900 dark:text-slate-100">
                      {p.policyNumber}
                    </TD>
                    <TD>{p.clientName}</TD>
                    <TD className="truncate max-w-[240px]">{p.buildingAddress}</TD>
                    <TD className="text-xs">
                      {formatDate(p.startDate)} → {formatDate(p.endDate)}
                    </TD>
                    <TD>{formatMoney(p.finalPremium, p.currencyCode)}</TD>
                    <TD>
                      <Badge status={p.status}>{p.status}</Badge>
                    </TD>
                  </TR>
                ))}
              </TBody>
            </Table>
            <Pagination
              page={data.number || 0}
              totalPages={data.totalPages || 0}
              onChange={setPage}
            />
          </>
        )}
      </Card>
    </div>
  )
}
