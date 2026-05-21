import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { ArrowLeft, Save } from 'lucide-react'
import { Card, CardBody, CardHeader } from '../../components/ui/Card'
import Input from '../../components/ui/Input'
import Select from '../../components/ui/Select'
import Button from '../../components/ui/Button'
import { clientsApi } from '../../api/clients'
import { useToast } from '../../contexts/ToastContext'
import { apiError } from '../../lib/api'
import { CLIENT_TYPES } from '../../utils/constants'

export default function ClientForm() {
  const { id } = useParams()
  const isEdit = !!id
  const navigate = useNavigate()
  const toast = useToast()

  const [form, setForm] = useState({
    countryCode: 'RO',
    type: 'INDIVIDUAL',
    name: '',
    identificationNumber: '',
    email: '',
    phone: '',
    address: '',
  })
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    if (!isEdit) return
    setLoading(true)
    clientsApi
      .get(id)
      .then((c) =>
        setForm({
          countryCode: c.countryCode || 'RO',
          type: c.type,
          name: c.name || '',
          identificationNumber: c.identificationNumber || '',
          email: c.email || '',
          phone: c.phone || '',
          address: c.address || '',
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
        await clientsApi.update(id, {
          name: form.name,
          email: form.email,
          phone: form.phone,
          address: form.address,
        })
        toast.success('Client updated')
      } else {
        const created = await clientsApi.create(form)
        toast.success('Client created')
        navigate(`/broker/clients/${created.id}`)
        return
      }
      navigate(`/broker/clients/${id}`)
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
        <CardHeader
          title={isEdit ? 'Edit Client' : 'New Client'}
          subtitle={isEdit ? 'Update client details' : 'Complete client details'}
        />
        <CardBody>
          <form onSubmit={submit} noValidate className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Select label="Client Type" value={form.type} onChange={change('type')} options={CLIENT_TYPES} disabled={isEdit} required />
            <Input label="Country Code (ISO)" value={form.countryCode} onChange={change('countryCode')} maxLength={3} disabled={isEdit} required hint="ex: RO, DE, US" />
            <Input label="Name" value={form.name} onChange={change('name')} required />
            <Input
              label={form.type === 'COMPANY' ? 'CUI / Registration No.' : 'CNP / ID'}
              value={form.identificationNumber}
              onChange={change('identificationNumber')}
              disabled={isEdit}
              required
            />
            <Input type="email" label="Email" value={form.email} onChange={change('email')} required />
            <Input label="Phone" value={form.phone} onChange={change('phone')} required hint="10-15 digits, optional +" />
            <div className="md:col-span-2">
              <Input label="Address" value={form.address} onChange={change('address')} />
            </div>
            <div className="md:col-span-2 flex gap-2 justify-end mt-2">
              <Button variant="outline" type="button" onClick={() => navigate(-1)}>
                Cancel
              </Button>
              <Button type="submit" loading={saving || loading} leftIcon={<Save className="h-4 w-4" />}>
                {isEdit ? 'Save' : 'Create'}
              </Button>
            </div>
          </form>
        </CardBody>
      </Card>
    </div>
  )
}
