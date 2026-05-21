import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import {
  ArrowLeft,
  CheckCircle2,
  XCircle,
  ScrollText,
  Calendar,
  DollarSign,
  User,
  Building2,
  Hash,
  Percent,
} from 'lucide-react'
import { Card, CardBody, CardHeader } from '../../components/ui/Card'
import Button from '../../components/ui/Button'
import Badge from '../../components/ui/Badge'
import Spinner from '../../components/ui/Spinner'
import Modal from '../../components/ui/Modal'
import Input from '../../components/ui/Input'
import { policiesApi } from '../../api/policies'
import { useToast } from '../../contexts/ToastContext'
import { apiError } from '../../lib/api'
import { formatDate, formatDateTime, formatMoney } from '../../lib/utils'

export default function PolicyDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const toast = useToast()
  const [p, setP] = useState(null)
  const [loading, setLoading] = useState(true)
  const [activating, setActivating] = useState(false)
  const [cancelOpen, setCancelOpen] = useState(false)
  const [cancelReason, setCancelReason] = useState('')
  const [cancelling, setCancelling] = useState(false)

  const fetch = () => {
    setLoading(true)
    policiesApi
      .get(id)
      .then(setP)
      .catch((err) => toast.error(apiError(err)))
      .finally(() => setLoading(false))
  }
  useEffect(fetch, [id])

  const activate = async () => {
    setActivating(true)
    try {
      const updated = await policiesApi.activate(id)
      setP(updated)
      toast.success('Policy activated successfully')
    } catch (err) {
      toast.error(apiError(err))
    } finally {
      setActivating(false)
    }
  }

  const doCancel = async () => {
    setCancelling(true)
    try {
      const updated = await policiesApi.cancel(id, cancelReason)
      setP(updated)
      setCancelOpen(false)
      setCancelReason('')
      toast.success('Policy cancelled')
    } catch (err) {
      toast.error(apiError(err))
    } finally {
      setCancelling(false)
    }
  }

  if (loading) return <Spinner label="Loading..." />
  if (!p) return null

  const Field = ({ icon: Icon, label, value, mono }) => (
    <div className="flex items-start gap-3">
      <div className="rounded-lg bg-slate-100 dark:bg-slate-800 p-2 text-slate-500">
        <Icon className="h-4 w-4" />
      </div>
      <div className="min-w-0 flex-1">
        <div className="text-[11px] uppercase tracking-wider text-slate-400">
          {label}
        </div>
        <div className={`text-sm text-slate-800 dark:text-slate-200 ${mono ? 'font-mono' : ''}`}>
          {value || '-'}
        </div>
      </div>
    </div>
  )

  const canActivate = p.status === 'DRAFT'
  const canCancel = p.status === 'ACTIVE' || p.status === 'DRAFT'
  const premiumAdjustments = p.premiumAdjustments || []
  const totalAdjustmentAmount = premiumAdjustments.reduce(
    (sum, item) => sum + Number(item.amount || 0),
    0,
  )

  return (
    <div className="space-y-5">
      <div className="flex items-center justify-between gap-3">
        <Button variant="ghost" size="sm" leftIcon={<ArrowLeft className="h-4 w-4" />} onClick={() => navigate('/broker/policies')}>
          Policies
        </Button>
        <div className="flex gap-2">
          {canActivate && (
            <Button variant="success" loading={activating} leftIcon={<CheckCircle2 className="h-4 w-4" />} onClick={activate}>
              Activate
            </Button>
          )}
          {canCancel && (
            <Button variant="danger" leftIcon={<XCircle className="h-4 w-4" />} onClick={() => setCancelOpen(true)}>
              Cancel
            </Button>
          )}
        </div>
      </div>

      <Card>
        <CardBody>
          <div className="flex items-start justify-between gap-4 flex-wrap">
            <div className="flex items-center gap-3">
              <div className="rounded-xl bg-gradient-to-br from-brand-500 to-brand-700 p-2.5 text-white">
                <ScrollText className="h-5 w-5" />
              </div>
              <div>
                <h2 className="text-xl font-bold text-slate-900 dark:text-slate-100">
                  Policy {p.policyNumber}
                </h2>
                <p className="text-xs text-slate-500">
                  Created {formatDateTime(p.createdAt)}
                </p>
              </div>
            </div>
            <Badge status={p.status}>{p.status}</Badge>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mt-6">
            <Field icon={Hash} label="Policy Number" value={p.policyNumber} mono />
            <Field
              icon={User}
              label="Client"
              value={
                <Link to={`/broker/clients/${p.clientId}`} className="text-brand-600 hover:underline">
                  {p.clientName}
                </Link>
              }
            />
            <Field
              icon={Building2}
              label="Building"
              value={
                <Link to={`/broker/buildings/${p.buildingId}`} className="text-brand-600 hover:underline">
                  {p.buildingAddress}
                </Link>
              }
            />
            <Field icon={Calendar} label="Period" value={`${formatDate(p.startDate)} to ${formatDate(p.endDate)}`} />
            <Field icon={DollarSign} label="Base Premium" value={formatMoney(p.basePremiumAmount, p.currencyCode)} />
            <Field
              icon={DollarSign}
              label="Final Premium"
              value={<span className="font-semibold text-brand-600 dark:text-brand-400">{formatMoney(p.finalPremium, p.currencyCode)}</span>}
            />
            <Field icon={User} label="Broker" value={p.brokerName} />
            <Field icon={Hash} label="Location" value={`${p.cityName}, ${p.countyName}, ${p.countryName}`} />
          </div>

          {p.status === 'CANCELLED' && (
            <div className="mt-6 rounded-lg border border-rose-200 dark:border-rose-900/40 bg-rose-50 dark:bg-rose-950/30 p-4">
              <div className="text-xs uppercase tracking-wider text-rose-600 dark:text-rose-400 mb-1">
                Cancellation
              </div>
              <div className="text-sm text-slate-700 dark:text-slate-200">
                {p.cancellationReason || '-'}
              </div>
              <div className="text-xs text-slate-500 mt-2">
                Date: {formatDate(p.cancellationDate)}
              </div>
            </div>
          )}
        </CardBody>
      </Card>

      <Card>
        <CardHeader title="Final Premium Calculation" subtitle="Adjustments applied over the base premium" />
        <CardBody>
          {premiumAdjustments.length === 0 ? (
            <div className="text-sm text-slate-500 dark:text-slate-400">
              No adjustments were applied to this policy.
            </div>
          ) : (
            <div className="space-y-3">
              {premiumAdjustments.map((item, index) => {
                const percentage = Number(item.percentage || 0)
                const amount = Number(item.amount || 0)
                return (
                  <div key={`${item.category}-${item.label}-${index}`} className="flex items-center justify-between gap-4 rounded-lg border border-slate-100 dark:border-slate-800 px-4 py-3">
                    <div className="flex min-w-0 items-center gap-3">
                      <div className="rounded-lg bg-slate-100 dark:bg-slate-800 p-2 text-slate-500">
                        <Percent className="h-4 w-4" />
                      </div>
                      <div className="min-w-0">
                        <div className="text-sm font-medium text-slate-900 dark:text-slate-100">
                          {item.label}
                        </div>
                        <div className="text-xs text-slate-500">
                          {item.category}
                        </div>
                      </div>
                    </div>
                    <div className="text-right">
                      <div className={`text-sm font-semibold ${percentage >= 0 ? 'text-rose-600 dark:text-rose-400' : 'text-emerald-600 dark:text-emerald-400'}`}>
                        {percentage > 0 ? '+' : ''}
                        {percentage}%
                      </div>
                      <div className="text-xs text-slate-500">
                        {amount > 0 ? '+' : ''}
                        {formatMoney(amount, p.currencyCode)}
                      </div>
                    </div>
                  </div>
                )
              })}
              <div className="flex items-center justify-between border-t border-slate-100 dark:border-slate-800 pt-3 text-sm">
                <span className="font-medium text-slate-700 dark:text-slate-200">
                  Total Adjustments
                </span>
                <span className={`font-semibold ${totalAdjustmentAmount >= 0 ? 'text-rose-600 dark:text-rose-400' : 'text-emerald-600 dark:text-emerald-400'}`}>
                  {totalAdjustmentAmount > 0 ? '+' : ''}
                  {formatMoney(totalAdjustmentAmount, p.currencyCode)}
                </span>
              </div>
            </div>
          )}
        </CardBody>
      </Card>

      <Modal open={cancelOpen} onClose={() => setCancelOpen(false)} title="Cancel Policy">
        <div className="space-y-4">
          <p className="text-sm text-slate-600 dark:text-slate-300">
            Please specify the cancellation reason. This action cannot be undone.
          </p>
          <Input label="Reason" value={cancelReason} onChange={(e) => setCancelReason(e.target.value)} placeholder="Cancellation reason..." required />
          <div className="flex gap-2 justify-end">
            <Button variant="outline" onClick={() => setCancelOpen(false)}>
              Close
            </Button>
            <Button variant="danger" onClick={doCancel} loading={cancelling}>
              Cancel Policy
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  )
}
