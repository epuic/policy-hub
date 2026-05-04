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
      toast.success('Poliță activată cu succes')
    } catch (err) {
      toast.error(apiError(err))
    } finally {
      setActivating(false)
    }
  }

  const doCancel = async () => {
    if (!cancelReason.trim()) {
      toast.warning('Motivul este obligatoriu')
      return
    }
    setCancelling(true)
    try {
      const updated = await policiesApi.cancel(id, cancelReason)
      setP(updated)
      setCancelOpen(false)
      setCancelReason('')
      toast.success('Poliță anulată')
    } catch (err) {
      toast.error(apiError(err))
    } finally {
      setCancelling(false)
    }
  }

  if (loading) return <Spinner label="Se încarcă..." />
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
        <div
          className={`text-sm text-slate-800 dark:text-slate-200 ${mono ? 'font-mono' : ''}`}
        >
          {value || '—'}
        </div>
      </div>
    </div>
  )

  const canActivate = p.status === 'DRAFT'
  const canCancel = p.status === 'ACTIVE' || p.status === 'DRAFT'

  return (
    <div className="space-y-5">
      <div className="flex items-center justify-between gap-3">
        <Button
          variant="ghost"
          size="sm"
          leftIcon={<ArrowLeft className="h-4 w-4" />}
          onClick={() => navigate('/broker/policies')}
        >
          Politici
        </Button>
        <div className="flex gap-2">
          {canActivate && (
            <Button
              variant="success"
              loading={activating}
              leftIcon={<CheckCircle2 className="h-4 w-4" />}
              onClick={activate}
            >
              Activează
            </Button>
          )}
          {canCancel && (
            <Button
              variant="danger"
              leftIcon={<XCircle className="h-4 w-4" />}
              onClick={() => setCancelOpen(true)}
            >
              Anulează
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
                  Poliță {p.policyNumber}
                </h2>
                <p className="text-xs text-slate-500">
                  Creată {formatDateTime(p.createdAt)}
                </p>
              </div>
            </div>
            <Badge status={p.status}>{p.status}</Badge>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mt-6">
            <Field icon={Hash} label="Număr poliță" value={p.policyNumber} mono />
            <Field
              icon={User}
              label="Client"
              value={
                <Link
                  to={`/broker/clients/${p.clientId}`}
                  className="text-brand-600 hover:underline"
                >
                  {p.clientName}
                </Link>
              }
            />
            <Field
              icon={Building2}
              label="Clădire"
              value={
                <Link
                  to={`/broker/buildings/${p.buildingId}`}
                  className="text-brand-600 hover:underline"
                >
                  {p.buildingAddress}
                </Link>
              }
            />
            <Field
              icon={Calendar}
              label="Perioadă"
              value={`${formatDate(p.startDate)} → ${formatDate(p.endDate)}`}
            />
            <Field
              icon={DollarSign}
              label="Primă de bază"
              value={formatMoney(p.basePremiumAmount, p.currencyCode)}
            />
            <Field
              icon={DollarSign}
              label="Primă finală"
              value={
                <span className="font-semibold text-brand-600 dark:text-brand-400">
                  {formatMoney(p.finalPremium, p.currencyCode)}
                </span>
              }
            />
            <Field icon={User} label="Broker" value={p.brokerName} />
            <Field
              icon={Hash}
              label="Locație"
              value={`${p.cityName}, ${p.countyName}, ${p.countryName}`}
            />
          </div>

          {p.status === 'CANCELLED' && (
            <div className="mt-6 rounded-lg border border-rose-200 dark:border-rose-900/40 bg-rose-50 dark:bg-rose-950/30 p-4">
              <div className="text-xs uppercase tracking-wider text-rose-600 dark:text-rose-400 mb-1">
                Anulare
              </div>
              <div className="text-sm text-slate-700 dark:text-slate-200">
                {p.cancellationReason || '—'}
              </div>
              <div className="text-xs text-slate-500 mt-2">
                La data: {formatDate(p.cancellationDate)}
              </div>
            </div>
          )}
        </CardBody>
      </Card>

      <Modal
        open={cancelOpen}
        onClose={() => setCancelOpen(false)}
        title="Anulare poliță"
      >
        <div className="space-y-4">
          <p className="text-sm text-slate-600 dark:text-slate-300">
            Te rugăm să specifici motivul pentru anulare. Acțiunea nu poate fi anulată.
          </p>
          <Input
            label="Motiv"
            value={cancelReason}
            onChange={(e) => setCancelReason(e.target.value)}
            placeholder="Motivul anulării..."
            required
          />
          <div className="flex gap-2 justify-end">
            <Button variant="outline" onClick={() => setCancelOpen(false)}>
              Renunță
            </Button>
            <Button variant="danger" onClick={doCancel} loading={cancelling}>
              Anulează poliță
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  )
}
