import { useEffect, useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { ArrowLeft, Save } from 'lucide-react'
import { Card, CardBody, CardHeader } from '../../components/ui/Card'
import Input from '../../components/ui/Input'
import Select from '../../components/ui/Select'
import DatePicker from '../../components/ui/DatePicker'
import Button from '../../components/ui/Button'
import { policiesApi } from '../../api/policies'
import { clientsApi } from '../../api/clients'
import { buildingsApi } from '../../api/buildings'
import { currenciesApi } from '../../api/currencies'
import { useAuth } from '../../contexts/AuthContext'
import { useToast } from '../../contexts/ToastContext'
import { apiError } from '../../lib/api'
import { numberOrNull } from '../../lib/utils'

export default function PolicyForm() {
  const navigate = useNavigate()
  const [params] = useSearchParams()
  const { user } = useAuth()
  const toast = useToast()

  const initialClient = params.get('clientId') || ''
  const initialBuilding = params.get('buildingId') || ''

  const [form, setForm] = useState({
    clientId: initialClient,
    buildingId: initialBuilding,
    brokerId: user?.entityId || '',
    startDate: '',
    endDate: '',
    basePremiumAmount: '',
    currencyId: '',
  })
  const [clients, setClients] = useState([])
  const [buildings, setBuildings] = useState([])
  const [currencies, setCurrencies] = useState([])
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    clientsApi.list({ page: 0, size: 100 }).then((d) => setClients(d.content || []))
    currenciesApi.listForBroker({ page: 0, size: 100 }).then((d) => setCurrencies(d.content || []))
  }, [])

  useEffect(() => {
    if (!form.clientId) {
      setBuildings([])
      return
    }
    buildingsApi
      .listByClient(form.clientId, { page: 0, size: 100 })
      .then((d) => setBuildings(d.content || []))
  }, [form.clientId])

  const change = (k) => (e) => setForm({ ...form, [k]: e.target.value })

  const submit = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      const payload = {
        clientId: numberOrNull(form.clientId),
        buildingId: numberOrNull(form.buildingId),
        brokerId: numberOrNull(form.brokerId),
        startDate: form.startDate,
        endDate: form.endDate,
        basePremiumAmount: numberOrNull(form.basePremiumAmount),
        currencyId: numberOrNull(form.currencyId),
      }
      const created = await policiesApi.create(payload)
      toast.success('Draft created')
      navigate(`/broker/policies/${created.id}`)
    } catch (err) {
      toast.error(apiError(err))
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="space-y-5 max-w-3xl">
      <Button variant="ghost" size="sm" leftIcon={<ArrowLeft className="h-4 w-4" />} onClick={() => navigate(-1)}>
        Back
      </Button>

      <Card>
        <CardHeader title="New Policy" subtitle="Create a new insurance policy draft" />
        <CardBody>
          <form onSubmit={submit} noValidate className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="md:col-span-2">
              <Select
                label="Client"
                value={form.clientId}
                onChange={(e) => setForm({ ...form, clientId: e.target.value, buildingId: '' })}
                placeholder="Select client"
                options={clients.map((c) => ({ value: c.id, label: `${c.name} - ${c.identificationNumber}` }))}
                required
              />
            </div>
            <div className="md:col-span-2">
              <Select
                label="Building"
                value={form.buildingId}
                onChange={change('buildingId')}
                placeholder="Select building"
                options={buildings.map((b) => ({ value: b.id, label: `${b.fullAddress} - ${b.cityName}` }))}
                disabled={!form.clientId}
                required
              />
            </div>
            <DatePicker
              label="Start Date"
              value={form.startDate}
              onChange={(value) => setForm({ ...form, startDate: value })}
              placeholder="Choose start date"
              required
            />
            <DatePicker
              label="End Date"
              value={form.endDate}
              onChange={(value) => setForm({ ...form, endDate: value })}
              placeholder="Choose end date"
              min={form.startDate}
              required
            />
            <Input type="number" step="0.01" label="Base Premium" value={form.basePremiumAmount} onChange={change('basePremiumAmount')} required />
            <Select
              label="Currency"
              value={form.currencyId}
              onChange={change('currencyId')}
              placeholder="Select currency"
              options={currencies.map((c) => ({ value: c.id, label: `${c.code} - ${c.name}` }))}
              required
            />
            <div className="md:col-span-2 flex gap-2 justify-end mt-2">
              <Button variant="outline" type="button" onClick={() => navigate(-1)}>
                Cancel
              </Button>
              <Button type="submit" loading={saving} leftIcon={<Save className="h-4 w-4" />}>
                Create Draft
              </Button>
            </div>
          </form>
        </CardBody>
      </Card>
    </div>
  )
}
