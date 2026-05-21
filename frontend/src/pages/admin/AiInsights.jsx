import { useEffect, useMemo, useState } from 'react'
import { BarChart3, BrainCircuit, Building2, ListChecks, Play, Save, Users } from 'lucide-react'
import { Card, CardBody, CardHeader } from '../../components/ui/Card'
import Button from '../../components/ui/Button'
import Input from '../../components/ui/Input'
import Spinner from '../../components/ui/Spinner'
import EmptyState from '../../components/ui/EmptyState'
import Badge from '../../components/ui/Badge'
import { Table, THead, TH, TBody, TR, TD } from '../../components/ui/Table'
import { aiApi } from '../../api/ai'
import { apiError } from '../../lib/api'
import { formatMoney, formatNumber, numberOrNull } from '../../lib/utils'
import { useToast } from '../../contexts/ToastContext'

const TARGETS = {
  buildings: {
    label: 'Buildings',
    icon: Building2,
    entityLabel: 'buildings',
  },
  clients: {
    label: 'Clients',
    icon: Users,
    entityLabel: 'clients',
  },
}

const METRIC_LABELS = {
  averageInsuredValue: 'Average insured value',
  averageSurfaceArea: 'Average surface area',
  averageBuildingAge: 'Average age',
  averageFloors: 'Average floors',
  averageRiskFactorCount: 'Average risk factors',
  averagePolicyCount: 'Average policies',
  averageFinalPremium: 'Average premium',
  averageBuildingCount: 'Average buildings',
  averageActivePolicyCount: 'Average active policies',
  averageCancelledPolicyCount: 'Average cancelled policies',
  averageTotalInsuredValue: 'Average insured value per client',
  averageTotalFinalPremium: 'Average total premium per client',
}

const MODE_LABELS = {
  buildingType: 'Dominant type',
  city: 'Dominant city',
  county: 'Dominant county',
  country: 'Dominant country',
  clientType: 'Dominant client type',
  dominantRiskFactor: 'Dominant risk factor',
  countryCode: 'Dominant country code',
  mainCity: 'Main city',
  mainCounty: 'Main county',
}

function moneyMetric(key, value) {
  if (key.toLowerCase().includes('premium') || key.toLowerCase().includes('insured')) {
    return formatMoney(value, 'RON')
  }
  return formatNumber(value)
}

function metricEntries(metrics = {}) {
  return Object.entries(metrics).filter(([, value]) => value !== null && value !== undefined)
}

function modeEntries(modes = {}) {
  return Object.entries(modes).filter(([, value]) => value && value !== 'N/A')
}

function ConfigTable({ rows, target, onChange, onSave }) {
  if (!rows.length) {
    return <EmptyState icon={BrainCircuit} title="No configured clusters" />
  }

  return (
    <Table>
      <THead>
        <TR>
          <TH>Cluster</TH>
          <TH>Label</TH>
          <TH>Status</TH>
          <TH className="text-right">Actions</TH>
        </TR>
      </THead>
      <TBody>
        {rows.map((row) => (
          <TR key={`${row.target}-${row.clusterId}`}>
            <TD className="font-medium">#{row.clusterId}</TD>
            <TD>
              <Input
                value={row.label}
                onChange={(e) => onChange(target, row.clusterId, 'label', e.target.value)}
              />
            </TD>
            <TD>{row.active ? 'Active' : 'Inactive'}</TD>
            <TD className="text-right">
              <Button
                size="sm"
                variant="outline"
                leftIcon={<Save className="h-4 w-4" />}
                onClick={() => onSave(target, row)}
              >
                Save
              </Button>
            </TD>
          </TR>
        ))}
      </TBody>
    </Table>
  )
}

function ClusterCard({ cluster, target }) {
  const memberLabel = TARGETS[target].entityLabel
  const topMetrics = metricEntries(cluster.numericAverages).slice(0, 6)
  const topModes = modeEntries(cluster.categoricalModes).slice(0, 6)

  return (
    <Card>
      <CardBody>
        <div className="flex flex-wrap items-start justify-between gap-3">
          <div>
            <div className="flex items-center gap-2">
              <Badge variant="brand">Cluster #{cluster.clusterId}</Badge>
              <span className="text-xs text-slate-500">
                {formatNumber(cluster.size)} {memberLabel}
              </span>
            </div>
            <h3 className="mt-2 text-base font-semibold text-slate-900 dark:text-slate-100">
              {cluster.label}
            </h3>
          </div>
          <div className="rounded-lg bg-slate-100 p-2 text-slate-500 dark:bg-slate-800">
            <BarChart3 className="h-4 w-4" />
          </div>
        </div>

        <div className="mt-4 grid grid-cols-1 sm:grid-cols-2 gap-3">
          {topMetrics.map(([key, value]) => (
            <div key={key} className="rounded-lg bg-slate-50 px-3 py-2 dark:bg-slate-900/60">
              <div className="text-[11px] uppercase tracking-wider text-slate-400">
                {METRIC_LABELS[key] || key}
              </div>
              <div className="mt-1 text-sm font-semibold text-slate-900 dark:text-slate-100">
                {moneyMetric(key, value)}
              </div>
            </div>
          ))}
        </div>

        {topModes.length > 0 && (
          <div className="mt-4 flex flex-wrap gap-2">
            {topModes.map(([key, value]) => (
              <span
                key={key}
                className="rounded-full bg-slate-100 px-3 py-1 text-xs text-slate-600 dark:bg-slate-800 dark:text-slate-300"
              >
                {MODE_LABELS[key] || key}: {value}
              </span>
            ))}
          </div>
        )}

        <div className="mt-4 border-t border-slate-100 pt-3 dark:border-slate-800">
          <div className="mb-2 flex items-center gap-2 text-xs font-medium uppercase tracking-wider text-slate-400">
            <ListChecks className="h-3.5 w-3.5" />
            Cluster examples
          </div>
          <div className="space-y-2">
            {(cluster.members || []).slice(0, 5).map((member) => (
              <div key={member.entityId} className="flex items-center justify-between gap-3 text-sm">
                <div className="min-w-0">
                  <div className="truncate font-medium text-slate-800 dark:text-slate-100">
                    {member.name}
                  </div>
                  <div className="truncate text-xs text-slate-500">
                    {member.subtitle}
                  </div>
                </div>
                <div className="shrink-0 text-xs text-slate-500">
                  #{member.entityId}
                </div>
              </div>
            ))}
          </div>
        </div>
      </CardBody>
    </Card>
  )
}

export default function AiInsights() {
  const toast = useToast()
  const [activeTarget, setActiveTarget] = useState('buildings')
  const [k, setK] = useState(4)
  const [loading, setLoading] = useState(true)
  const [running, setRunning] = useState('')
  const [buildingConfigs, setBuildingConfigs] = useState([])
  const [clientConfigs, setClientConfigs] = useState([])
  const [buildingAnalytics, setBuildingAnalytics] = useState([])
  const [clientAnalytics, setClientAnalytics] = useState([])

  const activeConfigs = activeTarget === 'buildings' ? buildingConfigs : clientConfigs
  const activeAnalytics = activeTarget === 'buildings' ? buildingAnalytics : clientAnalytics
  const activeMeta = TARGETS[activeTarget]
  const ActiveIcon = activeMeta.icon

  const totals = useMemo(() => {
    const clusters = activeAnalytics.length
    const entities = activeAnalytics.reduce((sum, cluster) => sum + Number(cluster.size || 0), 0)
    return { clusters, entities }
  }, [activeAnalytics])

  const load = async () => {
    setLoading(true)
    try {
      const [bc, cc, ba, ca] = await Promise.all([
        aiApi.buildingConfigurations(),
        aiApi.clientConfigurations(),
        aiApi.buildingAnalytics(),
        aiApi.clientAnalytics(),
      ])
      setBuildingConfigs(bc || [])
      setClientConfigs(cc || [])
      setBuildingAnalytics(ba || [])
      setClientAnalytics(ca || [])
    } catch (err) {
      toast.error(apiError(err))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const run = async (target) => {
    setRunning(target)
    try {
      if (target === 'buildings') {
        await aiApi.runBuildingClustering(k)
      } else {
        await aiApi.runClientClustering(k)
      }
      toast.success('Clustering completed successfully')
      await load()
    } catch (err) {
      toast.error(apiError(err))
    } finally {
      setRunning('')
    }
  }

  const changeConfig = (target, clusterId, key, value) => {
    const setter = target === 'buildings' ? setBuildingConfigs : setClientConfigs
    setter((rows) =>
      rows.map((row) => (row.clusterId === clusterId ? { ...row, [key]: value } : row)),
    )
  }

  const saveConfig = async (target, row) => {
    try {
      const payload = {
        label: row.label,
        adjustmentPercentage: numberOrNull(row.adjustmentPercentage) ?? 0,
        active: row.active,
      }
      if (target === 'buildings') {
        await aiApi.updateBuildingConfiguration(row.clusterId, payload)
      } else {
        await aiApi.updateClientConfiguration(row.clusterId, payload)
      }
      toast.success('Cluster label saved')
      await load()
    } catch (err) {
      toast.error(apiError(err))
    }
  }

  if (loading) return <Spinner />

  return (
    <div className="space-y-5">
      <div className="flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
        <div>
          <h2 className="text-2xl font-bold text-slate-900 dark:text-slate-100">
            AI Insights
          </h2>
          <p className="mt-1 text-sm text-slate-500 dark:text-slate-400">
            K-Prototypes segmentation for portfolio analysis
          </p>
        </div>
        <div className="flex flex-wrap items-end gap-2">
          <Input
            type="number"
            min={2}
            max={8}
            label="Clusters"
            value={k}
            onChange={(e) => setK(Number(e.target.value || 4))}
          />
          <Button
            leftIcon={<Play className="h-4 w-4" />}
            loading={running === 'buildings'}
            onClick={() => run('buildings')}
          >
            Run Buildings
          </Button>
          <Button
            variant="secondary"
            leftIcon={<Play className="h-4 w-4" />}
            loading={running === 'clients'}
            onClick={() => run('clients')}
          >
            Run Clients
          </Button>
        </div>
      </div>

      <div className="flex flex-wrap gap-2">
        {Object.entries(TARGETS).map(([key, target]) => {
          const Icon = target.icon
          const active = activeTarget === key
          return (
            <Button
              key={key}
              type="button"
              variant={active ? 'primary' : 'outline'}
              leftIcon={<Icon className="h-4 w-4" />}
              onClick={() => setActiveTarget(key)}
            >
              {target.label}
            </Button>
          )
        })}
      </div>

      <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
        <div className="rounded-lg border border-slate-200 bg-white p-4 dark:border-slate-800 dark:bg-slate-900/60">
          <div className="flex items-center gap-3">
            <div className="rounded-lg bg-brand-500/10 p-2 text-brand-600">
              <ActiveIcon className="h-4 w-4" />
            </div>
            <div>
              <div className="text-xs uppercase tracking-wider text-slate-400">Segmented Entities</div>
              <div className="text-xl font-semibold text-slate-900 dark:text-slate-100">
                {formatNumber(totals.entities)}
              </div>
            </div>
          </div>
        </div>
        <div className="rounded-lg border border-slate-200 bg-white p-4 dark:border-slate-800 dark:bg-slate-900/60">
          <div className="flex items-center gap-3">
            <div className="rounded-lg bg-sky-500/10 p-2 text-sky-600">
              <BrainCircuit className="h-4 w-4" />
            </div>
            <div>
              <div className="text-xs uppercase tracking-wider text-slate-400">Clusters</div>
              <div className="text-xl font-semibold text-slate-900 dark:text-slate-100">
                {formatNumber(totals.clusters)}
              </div>
            </div>
          </div>
        </div>
        <div className="rounded-lg border border-slate-200 bg-white p-4 dark:border-slate-800 dark:bg-slate-900/60">
          <div className="text-xs uppercase tracking-wider text-slate-400">Usage</div>
          <div className="mt-1 text-sm text-slate-700 dark:text-slate-200">
            Clusters are used for analysis and insights, not automatic premium calculation.
          </div>
        </div>
      </div>

      <Card>
        <CardHeader
          title={`${activeMeta.label} Cluster Labels`}
          subtitle="Labels help interpret segments in reports"
        />
        <CardBody>
          <ConfigTable
            rows={activeConfigs}
            target={activeTarget}
            onChange={changeConfig}
            onSave={saveConfig}
          />
        </CardBody>
      </Card>

      {activeAnalytics.length === 0 ? (
        <Card>
          <CardBody>
            <EmptyState
              icon={BrainCircuit}
              title="No AI Results"
              message="Run clustering to generate segments and statistics."
            />
          </CardBody>
        </Card>
      ) : (
        <div className="grid grid-cols-1 gap-5 xl:grid-cols-2">
          {activeAnalytics.map((cluster) => (
            <ClusterCard
              key={`${cluster.target}-${cluster.clusterId}`}
              cluster={cluster}
              target={activeTarget}
            />
          ))}
        </div>
      )}
    </div>
  )
}
