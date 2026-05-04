import { useEffect, useState } from 'react'
import { Users2, Coins, Percent, ShieldAlert } from 'lucide-react'
import StatCard from '../../components/dashboard/StatCard'
import { Card, CardHeader, CardBody } from '../../components/ui/Card'
import { brokersApi } from '../../api/brokers'
import { currenciesApi } from '../../api/currencies'
import { feesApi } from '../../api/fees'
import { riskFactorsApi } from '../../api/riskFactors'
import { reportsApi } from '../../api/reports'
import { useToast } from '../../contexts/ToastContext'
import { apiError } from '../../lib/api'
import { formatMoney, formatNumber } from '../../lib/utils'
import Spinner from '../../components/ui/Spinner'

export default function AdminDashboard() {
  const toast = useToast()
  const [stats, setStats] = useState({
    brokers: 0,
    currencies: 0,
    fees: 0,
    riskFactors: 0,
  })
  const [reportData, setReportData] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    Promise.all([
      brokersApi.list({ page: 0, size: 1 }),
      currenciesApi.list({ page: 0, size: 1 }),
      feesApi.list({ page: 0, size: 1 }),
      riskFactorsApi.list({ page: 0, size: 1 }),
      reportsApi.byCountry(),
    ])
      .then(([b, c, f, r, rep]) => {
        setStats({
          brokers: b.totalElements ?? 0,
          currencies: c.totalElements ?? 0,
          fees: f.totalElements ?? 0,
          riskFactors: r.totalElements ?? 0,
        })
        setReportData(rep || [])
      })
      .catch((err) => toast.error(apiError(err)))
      .finally(() => setLoading(false))
  }, [])

  const maxPolicies = Math.max(...reportData.map((r) => r.policyCount || 0), 1)

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-slate-900 dark:text-slate-100">
          Panou administrator
        </h2>
        <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
          Configurare sistem și rapoarte globale
        </p>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          icon={Users2}
          label="Brokeri"
          value={stats.brokers}
          accent="brand"
          loading={loading}
        />
        <StatCard
          icon={Coins}
          label="Monede"
          value={stats.currencies}
          accent="emerald"
          loading={loading}
        />
        <StatCard
          icon={Percent}
          label="Taxe configurate"
          value={stats.fees}
          accent="amber"
          loading={loading}
        />
        <StatCard
          icon={ShieldAlert}
          label="Ajustări risc"
          value={stats.riskFactors}
          accent="violet"
          loading={loading}
        />
      </div>

      <Card>
        <CardHeader
          title="Politici pe țări"
          subtitle="Distribuție după țară"
        />
        <CardBody>
          {loading ? (
            <Spinner />
          ) : reportData.length === 0 ? (
            <div className="text-sm text-slate-500 py-6 text-center">
              Nu există date încă
            </div>
          ) : (
            <div className="space-y-3">
              {reportData.slice(0, 8).map((r, i) => (
                <div key={i} className="space-y-1">
                  <div className="flex items-center justify-between text-sm">
                    <span className="font-medium text-slate-700 dark:text-slate-200">
                      {r.countryName}{' '}
                      <span className="text-xs text-slate-400">({r.currencyCode})</span>
                    </span>
                    <span className="text-slate-500 dark:text-slate-400">
                      {formatNumber(r.policyCount)} politici ·{' '}
                      <span className="font-semibold text-slate-700 dark:text-slate-200">
                        {formatMoney(r.totalFinalPremium, r.currencyCode)}
                      </span>
                    </span>
                  </div>
                  <div className="h-2 rounded-full bg-slate-100 dark:bg-slate-800 overflow-hidden">
                    <div
                      className="h-full rounded-full bg-gradient-to-r from-brand-500 to-brand-400 transition-all"
                      style={{
                        width: `${Math.max(4, (r.policyCount / maxPolicies) * 100)}%`,
                      }}
                    />
                  </div>
                </div>
              ))}
            </div>
          )}
        </CardBody>
      </Card>
    </div>
  )
}
