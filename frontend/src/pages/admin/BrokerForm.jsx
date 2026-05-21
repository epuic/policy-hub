import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { ArrowLeft, Save } from 'lucide-react'
import { Card, CardBody, CardHeader } from '../../components/ui/Card'
import Input from '../../components/ui/Input'
import Select from '../../components/ui/Select'
import Button from '../../components/ui/Button'
import { brokersApi } from '../../api/brokers'
import { useToast } from '../../contexts/ToastContext'
import { apiError } from '../../lib/api'
import { BROKER_STATUSES } from '../../utils/constants'
import { numberOrNull } from '../../lib/utils'

export default function BrokerForm() {
  const { id } = useParams()
  const isEdit = !!id
  const navigate = useNavigate()
  const toast = useToast()

  const [form, setForm] = useState({
    brokerCode: '',
    name: '',
    email: '',
    phone: '',
    password: '',
    status: 'ACTIVE',
    commissionPercentage: '',
  })
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    if (!isEdit) return
    setLoading(true)
    brokersApi
      .get(id)
      .then((b) =>
        setForm({
          brokerCode: b.brokerCode,
          name: b.name,
          email: b.email || '',
          phone: b.phone || '',
          password: '',
          status: b.status,
          commissionPercentage: b.commissionPercentage ?? '',
        }),
      )
      .catch((err) => toast.error(apiError(err)))
      .finally(() => setLoading(false))
  }, [id])

  const change = (k) => (e) => setForm({ ...form, [k]: e.target.value })

  const submit = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      if (isEdit) {
        await brokersApi.update(id, {
          name: form.name,
          email: form.email.trim(),
          phone: form.phone || null,
          commissionPercentage:
            form.commissionPercentage === ''
              ? null
              : numberOrNull(form.commissionPercentage),
        })
        toast.success('Broker updated')
      } else {
        await brokersApi.create({
          name: form.name,
          email: form.email.trim(),
          phone: form.phone || null,
          password: form.password,
          status: form.status,
          commissionPercentage:
            form.commissionPercentage === ''
              ? null
              : numberOrNull(form.commissionPercentage),
        })
        toast.success('Broker created')
      }
      navigate('/admin/brokers')
    } catch (err) {
      toast.error(apiError(err))
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="space-y-5 max-w-3xl">
      <Button
        variant="ghost"
        size="sm"
        leftIcon={<ArrowLeft className="h-4 w-4" />}
        onClick={() => navigate('/admin/brokers')}
      >
        Brokers
      </Button>

      <Card>
        <CardHeader
          title={isEdit ? 'Edit Broker' : 'New Broker'}
          subtitle={
            isEdit
              ? 'Update broker details'
              : 'Create a new broker account'
          }
        />
        <CardBody>
          <form onSubmit={submit} noValidate className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {isEdit && (
              <Input
                label="Broker Code"
                value={form.brokerCode}
                disabled
                hint="Generated automatically on creation"
              />
            )}
            <Input label="Name" value={form.name} onChange={change('name')} required />
            <Input type="email" label="Email" value={form.email} onChange={change('email')} required />
            <Input
              label="Phone"
              value={form.phone}
              onChange={change('phone')}
              hint="10-15 digits, optional +"
            />
            {!isEdit && (
              <Input
                type="password"
                label="Password"
                value={form.password}
                onChange={change('password')}
                required
                hint="Minimum 8 characters"
              />
            )}
            <Select
              label="Status"
              value={form.status}
              onChange={change('status')}
              options={BROKER_STATUSES}
              disabled={isEdit}
            />
            <Input
              type="number"
              step="0.01"
              label="Commission (%)"
              value={form.commissionPercentage}
              onChange={change('commissionPercentage')}
              min={0}
              max={100}
            />
            <div className="md:col-span-2 flex gap-2 justify-end mt-2">
              <Button
                variant="outline"
                type="button"
                onClick={() => navigate('/admin/brokers')}
              >
                Cancel
              </Button>
              <Button
                type="submit"
                loading={saving || loading}
                leftIcon={<Save className="h-4 w-4" />}
              >
                {isEdit ? 'Save' : 'Create'}
              </Button>
            </div>
          </form>
        </CardBody>
      </Card>
    </div>
  )
}
