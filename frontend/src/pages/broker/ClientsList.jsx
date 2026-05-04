import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Plus, Search, Users } from 'lucide-react'
import { Card, CardHeader } from '../../components/ui/Card'
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

  const fetch = async () => {
    setLoading(true)
    try {
      const params = { page, size: 10 }
      const res = query
        ? await clientsApi.search({ ...params, name: query })
        : await clientsApi.list(params)
      setData(res)
    } catch (err) {
      toast.error(apiError(err, 'Nu s-au putut încărca clienții'))
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
    fetch()
  }

  return (
    <div className="space-y-5">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div>
          <h2 className="text-2xl font-bold text-slate-900 dark:text-slate-100">
            Clienți
          </h2>
          <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
            Gestionează clienții și clădirile lor
          </p>
        </div>
        <Button
          leftIcon={<Plus className="h-4 w-4" />}
          onClick={() => navigate('/broker/clients/new')}
        >
          Client nou
        </Button>
      </div>

      <form onSubmit={onSearch} className="flex gap-2 max-w-md">
        <Input
          placeholder="Caută după nume..."
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          leftIcon={<Search className="h-4 w-4" />}
        />
        <Button type="submit" variant="secondary">
          Caută
        </Button>
      </form>

      <Card>
        {loading ? (
          <Spinner />
        ) : data.content.length === 0 ? (
          <EmptyState
            icon={Users}
            title="Niciun client"
            message="Adaugă primul tău client pentru a începe"
            action={
              <Button
                leftIcon={<Plus className="h-4 w-4" />}
                onClick={() => navigate('/broker/clients/new')}
              >
                Creează client
              </Button>
            }
          />
        ) : (
          <>
            <Table>
              <THead>
                <TR>
                  <TH>Nume</TH>
                  <TH>Tip</TH>
                  <TH>CNP/CUI</TH>
                  <TH>Email</TH>
                  <TH>Telefon</TH>
                  <TH>Țară</TH>
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
