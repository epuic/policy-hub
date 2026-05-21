import { useEffect, useState } from 'react'
import { useNavigate, useParams, Link } from 'react-router-dom'
import {
  ArrowLeft,
  Pencil,
  Plus,
  Building2,
  Calendar,
  Layers,
  Ruler,
  DollarSign,
  ShieldAlert,
} from 'lucide-react'
import { Card, CardBody, CardHeader } from '../../components/ui/Card'
import Button from '../../components/ui/Button'
import Badge from '../../components/ui/Badge'
import Spinner from '../../components/ui/Spinner'
import EmptyState from '../../components/ui/EmptyState'
import { Table, THead, TH, TBody, TR, TD } from '../../components/ui/Table'
import { buildingsApi } from '../../api/buildings'
import { useToast } from '../../contexts/ToastContext'
import { apiError } from '../../lib/api'
import { formatDate, formatMoney, formatNumber } from '../../lib/utils'
import { labelFor, BUILDING_TYPES, RISK_FACTOR_TYPES } from '../../utils/constants'

export default function BuildingDetail() {
  const { buildingId } = useParams()
  const navigate = useNavigate()
  const toast = useToast()
  const [b, setB] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    setLoading(true)
    buildingsApi
      .getWithPolicies(buildingId)
      .then(setB)
      .catch((err) => toast.error(apiError(err)))
      .finally(() => setLoading(false))
  }, [buildingId])

  if (loading) return <Spinner label="Loading..." />
  if (!b) return null

  const InfoBox = ({ icon: Icon, label, value, accent = 'brand' }) => (
    <div className="rounded-lg border border-slate-200 dark:border-slate-800 p-4">
      <div className={`inline-flex rounded-md p-1.5 text-${accent}-600 bg-${accent}-50 dark:bg-${accent}-950/40`}>
        <Icon className="h-4 w-4" />
      </div>
      <div className="text-[11px] uppercase tracking-wider text-slate-400 mt-2">{label}</div>
      <div className="text-sm font-semibold text-slate-800 dark:text-slate-200 mt-0.5">
        {value}
      </div>
    </div>
  )

  return (
    <div className="space-y-5">
      <div className="flex items-center justify-between gap-3">
        <Button variant="ghost" size="sm" leftIcon={<ArrowLeft className="h-4 w-4" />} onClick={() => navigate(`/broker/clients/${b.clientId}`)}>
          Back to Client
        </Button>
        <div className="flex gap-2">
          <Button variant="outline" leftIcon={<Pencil className="h-4 w-4" />} onClick={() => navigate(`/broker/buildings/${buildingId}/edit`)}>
            Edit
          </Button>
          <Button
            leftIcon={<Plus className="h-4 w-4" />}
            onClick={() => navigate(`/broker/policies/new?clientId=${b.clientId}&buildingId=${b.id}`)}
          >
            New Policy
          </Button>
        </div>
      </div>

      <Card>
        <CardBody>
          <div className="flex items-start justify-between gap-4 flex-wrap">
            <div>
              <div className="flex items-center gap-2">
                <div className="rounded-xl bg-gradient-to-br from-brand-500 to-brand-700 p-2.5 text-white">
                  <Building2 className="h-5 w-5" />
                </div>
                <div>
                  <h2 className="text-xl font-bold text-slate-900 dark:text-slate-100">
                    {b.fullAddress}
                  </h2>
                  <p className="text-xs text-slate-500">
                    {b.cityName}, {b.countyName}, {b.countryName}
                  </p>
                </div>
              </div>
              <div className="mt-3">
                <Link to={`/broker/clients/${b.clientId}`} className="text-xs text-brand-600 hover:underline">
                  Client: {b.clientName}
                </Link>
              </div>
            </div>
            <Badge status={b.type}>{labelFor(BUILDING_TYPES, b.type)}</Badge>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-4 gap-3 mt-6">
            <InfoBox icon={Calendar} label="Construction Year" value={b.constructionYear} />
            <InfoBox icon={Layers} label="Floors" value={b.numberOfFloors} />
            <InfoBox icon={Ruler} label="Surface Area" value={`${formatNumber(b.surfaceArea)} sqm`} />
            <InfoBox icon={DollarSign} label="Insured Value" value={formatMoney(b.insuredValue, 'RON')} />
          </div>

          {b.riskFactorTypes?.length > 0 && (
            <div className="mt-6">
              <div className="flex items-center gap-2 mb-2">
                <ShieldAlert className="h-4 w-4 text-amber-500" />
                <span className="text-xs uppercase tracking-wider text-slate-500">
                  Risk Factors
                </span>
              </div>
              <div className="flex flex-wrap gap-2">
                {b.riskFactorTypes.map((r) => (
                  <Badge key={r} variant="warning">
                    {labelFor(RISK_FACTOR_TYPES, r)}
                  </Badge>
                ))}
              </div>
            </div>
          )}
        </CardBody>
      </Card>

      <Card>
        <CardHeader title="Insurance Policies" subtitle="Policies linked to this building" />
        {(!b.policies || b.policies.length === 0) ? (
          <EmptyState
            title="No policies"
            message="Create a policy for this building"
            action={
              <Button leftIcon={<Plus className="h-4 w-4" />} onClick={() => navigate(`/broker/policies/new?clientId=${b.clientId}&buildingId=${b.id}`)}>
                Create Policy
              </Button>
            }
          />
        ) : (
          <Table>
            <THead>
              <TR>
                <TH>Number</TH>
                <TH>Start</TH>
                <TH>End</TH>
                <TH>Premium</TH>
                <TH>Status</TH>
              </TR>
            </THead>
            <TBody>
              {b.policies.map((p) => (
                <TR key={p.id} onClick={() => navigate(`/broker/policies/${p.id}`)}>
                  <TD className="font-medium text-slate-900 dark:text-slate-100">
                    {p.policyNumber}
                  </TD>
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
