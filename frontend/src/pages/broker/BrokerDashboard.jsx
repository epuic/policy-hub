import { useEffect, useState } from 'react'
import { Users, Building2, ScrollText, TrendingUp } from 'lucide-react'
import { Link } from 'react-router-dom'
import StatCard from '../../components/dashboard/StatCard'
import { Card, CardHeader, CardBody } from '../../components/ui/Card'
import Badge from '../../components/ui/Badge'
import { Table, THead, TH, TBody, TR, TD } from '../../components/ui/Table'
import { clientsApi } from '../../api/clients'
import { policiesApi } from '../../api/policies'
import { useToast } from '../../contexts/ToastContext'
import { apiError } from '../../lib/api'
import { formatDate, formatMoney } from '../../lib/utils'
import Spinner from '../../components/ui/Spinner'
import EmptyState from '../../components/ui/EmptyState'

export default function BrokerDashboard() {
  const toast = useToast()
  const [stats, setStats] = useState({
    clients: 0,
    policies: 0,
    active: 0,
    drafts: 0,
  })
  const [recent, setRecent] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let cancel = false
    async function run() {
      try {
        const [clients, all, active, drafts] = await Promise.all([
          clientsApi.list({ page: 0, size: 1 }),
          policiesApi.list({ page: 0, size: 5, sort: 'createdAt,desc' }),
          policiesApi.list({ page: 0, size: 1, status: 'ACTIVE' }),
          policiesApi.list({ page: 0, size: 1, status: 'DRAFT' }),
        ])
        if (cancel) return
        setStats({
          clients: clients.totalElements ?? 0,
          policies: all.totalElements ?? 0,
          active: active.totalElements ?? 0,
          drafts: drafts.totalElements ?? 0,
        })
        setRecent(all.content || [])
      } catch (err) {
        toast.error(apiError(err, 'Nu s-au putut încărca statisticile'))
      } finally {
        if (!cancel) setLoading(false)
      }
    }
    run()
    return () => (cancel = true)
  }, [])

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-slate-900 dark:text-slate-100">
          Panou de bord
        </h2>
        <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
          Prezentare generală a activității tale de broker
        </p>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          icon={Users}
          label="Clienți"
          value={stats.clients}
          accent="brand"
          loading={loading}
        />
        <StatCard
          icon={ScrollText}
          label="Politici totale"
          value={stats.policies}
          accent="violet"
          loading={loading}
        />
        <StatCard
          icon={TrendingUp}
          label="Politici active"
          value={stats.active}
          accent="emerald"
          loading={loading}
        />
        <StatCard
          icon={Building2}
          label="Ciorne"
          value={stats.drafts}
          accent="amber"
          loading={loading}
        />
      </div>

      <Card>
        <CardHeader
          title="Ultimele politici"
          subtitle="Cele mai recente 5 politici"
          actions={
            <Link
              to="/broker/policies"
              className="text-xs font-medium text-brand-600 hover:underline"
            >
              Vezi toate →
            </Link>
          }
        />
        {loading ? (
          <Spinner />
        ) : recent.length === 0 ? (
          <EmptyState
            title="Nicio politică"
            message="Creează prima politică pentru a începe"
          />
        ) : (
          <Table>
            <THead>
              <TR>
                <TH>Nr. Poliță</TH>
                <TH>Client</TH>
                <TH>Clădire</TH>
                <TH>Start</TH>
                <TH>Final</TH>
                <TH>Primă</TH>
                <TH>Status</TH>
              </TR>
            </THead>
            <TBody>
              {recent.map((p) => (
                <TR key={p.id}>
                  <TD className="font-medium text-slate-900 dark:text-slate-100">
                    {p.policyNumber}
                  </TD>
                  <TD>{p.clientName}</TD>
                  <TD className="truncate max-w-[220px]">{p.buildingAddress}</TD>
                  <TD>{formatDate(p.startDate)}</TD>
                  <TD>{formatDate(p.endDate)}</TD>
                  <TD>{formatMoney(p.finalPremium, p.currencyCode)}</TD>
                  <TD>
                    <Badge status={p.status}>{p.status}</Badge>
                  </TD>
                </TR>
              ))}
            </TBody>
          </Table>
        )}
      </Card>
    </div>
  )
}
