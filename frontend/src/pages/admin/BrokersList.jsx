import { useEffect, useState } from 'react'
import { Plus, Users2, Power, PowerOff, Pencil } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { Card } from '../../components/ui/Card'
import Button from '../../components/ui/Button'
import Badge from '../../components/ui/Badge'
import Spinner from '../../components/ui/Spinner'
import EmptyState from '../../components/ui/EmptyState'
import Pagination from '../../components/ui/Pagination'
import { Table, THead, TH, TBody, TR, TD } from '../../components/ui/Table'
import { brokersApi } from '../../api/brokers'
import { useToast } from '../../contexts/ToastContext'
import { apiError } from '../../lib/api'

export default function BrokersList() {
  const navigate = useNavigate()
  const toast = useToast()
  const [data, setData] = useState({ content: [], totalPages: 0, number: 0 })
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)

  const fetch = async () => {
    setLoading(true)
    try {
      const res = await brokersApi.list({ page, size: 10 })
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

  const toggleStatus = async (b, e) => {
    e.stopPropagation()
    try {
      if (b.status === 'ACTIVE') {
        await brokersApi.deactivate(b.id)
        toast.success('Broker dezactivat')
      } else {
        await brokersApi.activate(b.id)
        toast.success('Broker activat')
      }
      fetch()
    } catch (err) {
      toast.error(apiError(err))
    }
  }

  return (
    <div className="space-y-5">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div>
          <h2 className="text-2xl font-bold text-slate-900 dark:text-slate-100">
            Brokeri
          </h2>
          <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
            Gestionează echipa de brokeri
          </p>
        </div>
        <Button
          leftIcon={<Plus className="h-4 w-4" />}
          onClick={() => navigate('/admin/brokers/new')}
        >
          Broker nou
        </Button>
      </div>

      <Card>
        {loading ? (
          <Spinner />
        ) : data.content.length === 0 ? (
          <EmptyState icon={Users2} title="Niciun broker" />
        ) : (
          <>
            <Table>
              <THead>
                <TR>
                  <TH>Cod</TH>
                  <TH>Nume</TH>
                  <TH>Email</TH>
                  <TH>Telefon</TH>
                  <TH>Comision</TH>
                  <TH>Status</TH>
                  <TH className="text-right">Acțiuni</TH>
                </TR>
              </THead>
              <TBody>
                {data.content.map((b) => (
                  <TR
                    key={b.id}
                    onClick={() => navigate(`/admin/brokers/${b.id}/edit`)}
                  >
                    <TD className="font-mono text-xs">{b.brokerCode}</TD>
                    <TD className="font-medium text-slate-900 dark:text-slate-100">
                      {b.name}
                    </TD>
                    <TD>{b.email || '—'}</TD>
                    <TD>{b.phone || '—'}</TD>
                    <TD>
                      {b.commissionPercentage != null
                        ? `${b.commissionPercentage}%`
                        : '—'}
                    </TD>
                    <TD>
                      <Badge status={b.status}>{b.status}</Badge>
                    </TD>
                    <TD>
                      <div className="flex gap-1 justify-end">
                        <Button
                          size="icon"
                          variant="ghost"
                          onClick={(e) => {
                            e.stopPropagation()
                            navigate(`/admin/brokers/${b.id}/edit`)
                          }}
                          title="Editează"
                        >
                          <Pencil className="h-4 w-4" />
                        </Button>
                        <Button
                          size="icon"
                          variant="ghost"
                          onClick={(e) => toggleStatus(b, e)}
                          title={b.status === 'ACTIVE' ? 'Dezactivează' : 'Activează'}
                        >
                          {b.status === 'ACTIVE' ? (
                            <PowerOff className="h-4 w-4 text-rose-500" />
                          ) : (
                            <Power className="h-4 w-4 text-emerald-500" />
                          )}
                        </Button>
                      </div>
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
