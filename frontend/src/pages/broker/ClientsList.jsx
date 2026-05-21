import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Plus, Search, Users, X } from 'lucide-react'
import { Card } from '../../components/ui/Card'
import { Table, THead, TH, TBody, TR, TD } from '../../components/ui/Table'
import Badge from '../../components/ui/Badge'
import Button from '../../components/ui/Button'
import Input from '../../components/ui/Input'
import Spinner from '../../components/ui/Spinner'
import EmptyState from '../../components/ui/EmptyState'
import Pagination from '../../components/ui/Pagination'
import { clientsApi } from '../../api/clients'
import { useToast } from '../../contexts/ToastContext'
import { apiError } from '../../lib/api'
import { labelFor, CLIENT_TYPES } from '../../utils/constants'

export default function ClientsList() {
  const toast = useToast()
  const navigate = useNavigate()
  const [data, setData] = useState({ content: [], totalPages: 0, number: 0 })
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [query, setQuery] = useState('')

  const fetch = async (nextQuery = query, nextPage = page) => {
    setLoading(true)
    try {
      const params = { page: nextPage, size: 10 }
      const search = nextQuery.trim()
      const res = search
        ? await clientsApi.search({
            ...params,
            ...(/\d/.test(search) ? { identifier: search } : { name: search }),
          })
        : await clientsApi.list(params)
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

  const onSearch = (e) => {
    e.preventDefault()
    setPage(0)
    fetch(query, 0)
  }

  const clearSearch = () => {
    setQuery('')
    setPage(0)
    fetch('', 0)
  }

  return (
    <div className="space-y-5">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div>
          <h2 className="text-2xl font-bold text-slate-900 dark:text-slate-100">
            Clients
          </h2>
          <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
            Manage clients and their buildings
          </p>
        </div>
        <Button leftIcon={<Plus className="h-4 w-4" />} onClick={() => navigate('/broker/clients/new')}>
          New Client
        </Button>
      </div>

      <form
        onSubmit={onSearch}
        noValidate
        className="flex w-full max-w-xl items-center gap-2 rounded-2xl border border-slate-200 bg-white p-1.5 shadow-sm shadow-slate-200/50 dark:border-slate-800 dark:bg-slate-900/70 dark:shadow-none"
      >
        <div className="flex-1">
          <Input
            placeholder="Name, CNP, or CUI"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            leftIcon={<Search className="h-4 w-4" />}
            rightIcon={
              query ? (
                <button
                  type="button"
                  onClick={clearSearch}
                  className="rounded-full p-0.5 text-slate-400 transition hover:bg-slate-100 hover:text-slate-700 dark:hover:bg-slate-800 dark:hover:text-slate-200"
                  title="Clear search"
                >
                  <X className="h-3.5 w-3.5" />
                </button>
              ) : null
            }
            className="h-11 rounded-xl border-0 bg-transparent shadow-none focus:border-transparent focus:ring-0"
          />
        </div>
        <Button type="submit" className="h-10 rounded-xl px-4">
          Search
        </Button>
      </form>

      <Card>
        {loading ? (
          <Spinner />
        ) : data.content.length === 0 ? (
          <EmptyState
            icon={Users}
            title="No clients"
            message="Add your first client to get started"
            action={
              <Button leftIcon={<Plus className="h-4 w-4" />} onClick={() => navigate('/broker/clients/new')}>
                Create Client
              </Button>
            }
          />
        ) : (
          <>
            <Table>
              <THead>
                <TR>
                  <TH>Name</TH>
                  <TH>Type</TH>
                  <TH>CNP/CUI</TH>
                  <TH>Email</TH>
                  <TH>Phone</TH>
                  <TH>Country</TH>
                </TR>
              </THead>
              <TBody>
                {data.content.map((c) => (
                  <TR key={c.id} onClick={() => navigate(`/broker/clients/${c.id}`)}>
                    <TD className="font-medium text-slate-900 dark:text-slate-100">
                      {c.name}
                    </TD>
                    <TD>
                      <Badge status={c.type}>
                        {labelFor(CLIENT_TYPES, c.type)}
                      </Badge>
                    </TD>
                    <TD className="font-mono text-xs">{c.identificationNumber}</TD>
                    <TD>{c.email}</TD>
                    <TD>{c.phone}</TD>
                    <TD>{c.countryCode}</TD>
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
